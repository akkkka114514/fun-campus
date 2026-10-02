package com.akkkka.admin.module.business.funcampus.activityOrder.job;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderLogAction;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityOrderLogService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.module.support.job.core.SmartJob;
import com.akkkka.module.support.message.constant.MessageTemplateEnum;
import com.akkkka.module.support.message.domain.MessageTemplateSendForm;
import com.akkkka.module.support.message.service.MessageService;
import com.akkkka.module.support.pay.domain.dto.PayNotifyResult;
import com.akkkka.module.support.pay.domain.dto.PayQueryResult;
import com.akkkka.module.support.pay.service.PaymentService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 待支付订单超时处理任务（关单前主动查单，防止回调丢失造成资金-状态不一致）
 * <p>
 * 扫描超过 expire_time 仍未支付的订单，逐条处理：
 * 1. 先向渠道主动查单：渠道已支付（回调丢失）→ 以查单结果走回调同一入账路径补偿（CAS 幂等）；
 *    查单异常时本轮跳过不关单（资金可能已入账），留待下一轮重试；
 * 2. 渠道确认未支付 → 事务内 CAS 关单（待支付→已关闭）+ 释放锁定的名额 + 通知用户；
 * 与支付回调抢同一个 where status=待支付 条件，数据库保证只有一个能成功，
 * CAS 失败（已被支付/取消）直接跳过；单条异常隔离，下一轮重试
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
@Slf4j
@Service
public class ActivityOrderTimeoutJob implements SmartJob {

    /**
     * 单轮最多处理订单数，防止堆积时单轮执行过长
     */
    private static final int BATCH_LIMIT = 1000;

    @Resource
    private ActivityOrderManager orderManager;

    @Resource
    private ActivityOrderLogService orderLogService;

    @Resource
    private ActivityEnrollNumDao activityEnrollNumDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ActivityManager activityManager;

    @Resource
    private MessageService messageService;

    @Resource
    private PaymentService paymentService;

    /**
     * 执行超时关单
     *
     * @param param 可选参数
     * @return 执行结果描述
     */
    @Override
    public String run(String param) {
        List<ActivityOrderEntity> timeoutOrders = orderManager.listTimeoutOrders(LocalDateTime.now(), BATCH_LIMIT);
        if (timeoutOrders.isEmpty()) {
            return "没有超时未支付订单，无需处理";
        }

        int compensated = 0;
        int closed = 0;
        int skipped = 0;
        int failed = 0;
        for (ActivityOrderEntity order : timeoutOrders) {
            try {
                // 关单前先向渠道查单：回调可能丢失，渠道已支付则补偿入账，确认未支付才允许关单
                PayQueryResult queryResult;
                try {
                    queryResult = paymentService.queryPay(order);
                } catch (Exception e) {
                    // 查单失败不贸然关单（资金可能已入账），本轮跳过，下一轮重试
                    log.error("超时订单查单失败，本轮跳过不关单：orderNo:{}", order.getOrderNo(), e);
                    failed++;
                    continue;
                }
                if (queryResult.isPaid()) {
                    compensatePaidOrder(order, queryResult);
                    compensated++;
                    continue;
                }
                if (closeTimeoutOrder(order)) {
                    closed++;
                    orderLogService.recordSystem(order, OrderLogAction.CLOSE, "订单超时未支付（渠道查单确认未支付），系统自动关闭");
                    sendTimeoutMessage(order);
                } else {
                    // 支付回调/用户取消抢先处理，跳过
                    skipped++;
                }
            } catch (Exception e) {
                // 单条隔离：一条失败不影响其他订单，下一轮重试
                log.error("超时订单处理失败：orderNo:{}", order.getOrderNo(), e);
                failed++;
            }
        }
        return String.format("超时订单处理完成：查单补偿%d单，关闭%d单，跳过%d单，失败%d单",
                compensated, closed, skipped, failed);
    }

    /**
     * 回调丢失兜底：以渠道查单结果构造通知，走与支付回调完全相同的入账路径
     * <p>
     * 金额以渠道查回金额参与校验（与订单不一致会被 handlePayNotify 拒绝）；
     * CAS 幂等保证与并发到达的支付回调不会重复入账
     */
    private void compensatePaidOrder(ActivityOrderEntity order, PayQueryResult queryResult) {
        PayNotifyResult notifyResult = new PayNotifyResult();
        notifyResult.setChannel(queryResult.getChannel());
        notifyResult.setOrderNo(order.getOrderNo());
        notifyResult.setChannelOrderNo(queryResult.getChannelOrderNo());
        notifyResult.setAmountFen(queryResult.getAmountFen());
        notifyResult.setSuccess(true);
        orderLogService.recordSystem(order, OrderLogAction.QUERY_COMPENSATE,
                "超时查单发现渠道已支付（回调可能丢失），提交补入账");
        paymentService.handlePayNotify(notifyResult);
        log.warn("超时查单补偿入账：orderNo:{}，channelOrderNo:{}", order.getOrderNo(), queryResult.getChannelOrderNo());
    }

    /**
     * 事务：CAS 关单 + 释放锁定的名额
     *
     * @return 是否由本任务完成关单（false=已被支付回调/用户取消处理）
     */
    private boolean closeTimeoutOrder(ActivityOrderEntity order) {
        boolean[] handled = {false};
        transactionTemplate.executeWithoutResult(status -> {
            if (!orderManager.closeIfWaitPayCas(order.getId(), LocalDateTime.now())) {
                return;
            }
            handled[0] = true;
            if (!activityEnrollNumDao.decreaseEnrollNum(order.getActivityId())) {
                log.warn("超时关单释放名额失败：orderNo:{}", order.getOrderNo());
            }
        });
        return handled[0];
    }

    /**
     * 关单站内信（发送失败仅记录日志）
     */
    private void sendTimeoutMessage(ActivityOrderEntity order) {
        try {
            ActivityEntity activity = activityManager.getById(order.getActivityId());
            Map<String, Object> contentParam = new HashMap<>();
            contentParam.put("activityTitle", activity == null ? "" : Objects.toString(activity.getTitle(), ""));
            MessageTemplateSendForm sendForm = new MessageTemplateSendForm();
            sendForm.setMessageTemplateEnum(MessageTemplateEnum.ACTIVITY_ORDER_TIMEOUT);
            sendForm.setReceiverUserType(UserTypeEnum.PORTAL_USER);
            sendForm.setReceiverUserId(order.getUserId());
            sendForm.setDataId(order.getActivityId());
            sendForm.setContentParam(contentParam);
            messageService.sendTemplateMessage(sendForm);
        } catch (Exception e) {
            log.warn("订单超时关闭站内信发送失败：orderNo:{}", order.getOrderNo(), e);
        }
    }
}
