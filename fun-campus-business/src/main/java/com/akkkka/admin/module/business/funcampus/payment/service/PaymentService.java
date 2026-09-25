package com.akkkka.admin.module.business.funcampus.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityRefundEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityRefundManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.payment.channel.PaymentChannel;
import com.akkkka.admin.module.business.funcampus.payment.constant.PayChannelEnum;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayNotifyResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayPrepayResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundChannelResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundNotifyResult;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.module.support.message.constant.MessageTemplateEnum;
import com.akkkka.module.support.message.domain.MessageTemplateSendForm;
import com.akkkka.module.support.message.service.MessageService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付服务：渠道路由 + 回调统一处理
 * <p>
 * - 支付/退款回调统一收敛到 handlePayNotify / handleRefundNotify（mock 与真实渠道同一入口）；
 * - 订单状态全部通过 CAS 条件更新（where status=旧状态）保证幂等：
 *   重复回调、关单与回调竞态，都靠影响行数判定，谁先成功谁生效；
 * - 支付成功后才写报名记录，对现有签到、审核代码零侵入
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class PaymentService implements PaymentNotifyHandler {

    /**
     * 当前启用的支付渠道标识（如 mock；生产环境配置真实渠道）
     */
    @Value("${pay.channel:}")
    private String payChannelConfig;

    private final ObjectProvider<PaymentChannel> channelProvider;

    private final TransactionTemplate transactionTemplate;

    private final ActivityOrderManager orderManager;

    private final ActivityRefundManager refundManager;

    private final ActivityEnrollmentService enrollmentService;

    private final ActivityEnrollmentManager enrollmentManager;

    private final ActivityEnrollNumDao activityEnrollNumDao;

    private final ActivityManager activityManager;

    private final MessageService messageService;

    /**
     * 预下单：使用当前启用的渠道生成支付参数（mock 渠道返回模拟收银台链接）
     */
    public PayPrepayResult prepay(ActivityOrderEntity order) {
        PaymentChannel channel = requireActiveChannel();
        return channel.prepay(order);
    }

    /**
     * 发起渠道退款：受理成功不代表到账，最终以渠道异步回调为准
     *
     * @return 渠道受理结果（accepted=false 时调用方需回滚订单状态）
     */
    public RefundChannelResult refund(ActivityOrderEntity order, ActivityRefundEntity refund) {
        PaymentChannel channel = requireChannel(resolveRefundChannel(order));
        return channel.refund(order, refund);
    }

    /**
     * 获取回调参数解析渠道（POST /payment/callback/{channel} 入口使用）
     */
    public PaymentChannel getChannel(PayChannelEnum channelEnum) {
        return requireChannel(channelEnum);
    }

    /**
     * 处理支付回调（mock 与真实渠道统一入口）
     * <p>
     * 流程：金额校验 → CAS 更新订单（待支付→已支付）→ 写报名记录 → 通知；
     * CAS 失败时按订单当前状态区分：重复回调（幂等）、回调晚于关单（需退款补偿）
     */
    @Override
    public void handlePayNotify(PayNotifyResult notifyResult) {
        if (!notifyResult.isSuccess()) {
            log.info("支付失败回调，不改变订单状态：orderNo:{}，原因:{}", notifyResult.getOrderNo(), notifyResult.getFailReason());
            return;
        }
        if (notifyResult.getOrderNo() == null) {
            log.warn("支付回调缺少订单号，忽略");
            return;
        }
        ActivityOrderEntity order = orderManager.getByOrderNo(notifyResult.getOrderNo());
        if (order == null) {
            log.error("支付回调订单不存在：orderNo:{}", notifyResult.getOrderNo());
            return;
        }
        // 金额校验：一律以服务端订单金额为准，回调金额不一致拒绝入账
        if (notifyResult.getAmountFen() == null || !notifyResult.getAmountFen().equals(order.getAmountFen())) {
            log.error("支付回调金额不一致，拒绝处理！orderNo:{}，回调金额:{}，订单金额:{}",
                    order.getOrderNo(), notifyResult.getAmountFen(), order.getAmountFen());
            return;
        }

        boolean[] handled = {false};
        transactionTemplate.executeWithoutResult(status -> {
            // CAS 幂等：仅当订单仍为待支付时，才由本次调用完成流转
            boolean cas = orderManager.markPaidCas(order.getId(), notifyResult.getChannelOrderNo(),
                    notifyResult.getChannel(), LocalDateTime.now());
            if (!cas) {
                return;
            }
            handled[0] = true;
            // 支付成功后才写报名记录（对现有签到/审核代码零侵入）
            enrollmentService.saveEnrollmentRecord(order.getActivityId(), order.getUserId());
        });

        if (handled[0]) {
            log.info("支付回调处理成功：orderNo:{}，activityId:{}，userId:{}",
                    order.getOrderNo(), order.getActivityId(), order.getUserId());
            sendPaySuccessMessage(order);
            return;
        }

        // CAS 未成功：读取订单最新状态，区分竞态处理
        ActivityOrderEntity latest = orderManager.getById(order.getId());
        OrderStatus latestStatus = latest == null ? null : latest.getStatus();
        if (latestStatus == OrderStatus.PAID) {
            // 重复回调：首次回调已完成处理，幂等返回成功
            log.info("支付回调重复到达，订单已支付，幂等返回：orderNo:{}", order.getOrderNo());
        } else if (latestStatus == OrderStatus.CLOSED) {
            // 竞态：关单任务先赢，但渠道侧资金已入账 → 现实中需触发退款补偿，人工介入
            log.error("支付回调晚于关单：资金已入账但订单已关闭，需退款补偿！orderNo:{}，userId:{}",
                    order.getOrderNo(), order.getUserId());
        } else if (latestStatus == OrderStatus.REFUNDING || latestStatus == OrderStatus.REFUNDED
                || latestStatus == OrderStatus.REFUND_FAILED) {
            log.warn("支付回调到达时订单已进入退款流程：orderNo:{}，status:{}", order.getOrderNo(), latestStatus);
        } else {
            log.warn("支付回调未完成状态流转：orderNo:{}，当前状态:{}", order.getOrderNo(), latestStatus);
        }
    }

    /**
     * 处理退款回调（mock 与真实渠道统一入口）
     * <p>
     * 流程：退款单 CAS（退款中→成功）→ 订单 CAS（退款中→已退款）→ 逻辑删除报名记录 + 释放名额 → 通知；
     * 失败回调：退款单标失败 + 订单退款中→退款失败（支持重新申请）
     */
    @Override
    public void handleRefundNotify(RefundNotifyResult notifyResult) {
        if (notifyResult.getRefundNo() == null) {
            log.warn("退款回调缺少退款单号，忽略");
            return;
        }
        ActivityRefundEntity refund = refundManager.getByRefundNo(notifyResult.getRefundNo());
        if (refund == null) {
            log.error("退款回调退款单不存在：refundNo:{}", notifyResult.getRefundNo());
            return;
        }
        ActivityOrderEntity order = orderManager.getById(refund.getOrderId());
        if (order == null) {
            log.error("退款回调订单不存在：refundNo:{}，orderId:{}", refund.getRefundNo(), refund.getOrderId());
            return;
        }

        boolean[] handled = {false};
        transactionTemplate.executeWithoutResult(status -> {
            if (!notifyResult.isSuccess()) {
                // 渠道退款失败：退款单 退款中→失败，订单 退款中→退款失败（均 CAS，重复回调幂等）
                refundManager.markFailedCas(refund.getId());
                if (!orderManager.markRefundFailedCas(order.getId())) {
                    log.warn("退款失败回调时订单状态非退款中：orderNo:{}", order.getOrderNo());
                }
                handled[0] = true;
                return;
            }
            // CAS 幂等：退款单 退款中→成功
            LocalDateTime refundTime = notifyResult.getRefundTime() == null ? LocalDateTime.now() : notifyResult.getRefundTime();
            boolean cas = refundManager.markSuccessCas(refund.getId(), notifyResult.getChannelRefundNo(), refundTime);
            if (!cas) {
                return;
            }
            handled[0] = true;
            // 订单 退款中→已退款
            if (!orderManager.markRefundedCas(order.getId())) {
                log.warn("退款成功回调时订单状态非退款中：orderNo:{}", order.getOrderNo());
            }
            // 逻辑删除报名记录（幂等：报名审核剔除场景记录可能已删除）+ 释放名额
            releaseEnrollment(order.getActivityId(), order.getUserId());
        });

        if (!handled[0]) {
            log.info("退款回调重复到达，退款单已终态，幂等返回：refundNo:{}", refund.getRefundNo());
            return;
        }
        log.info("退款回调处理完成：refundNo:{}，success:{}，orderNo:{}", refund.getRefundNo(), notifyResult.isSuccess(), order.getOrderNo());
        if (notifyResult.isSuccess()) {
            sendRefundSuccessMessage(order);
        }
    }

    /**
     * 逻辑删除报名记录并释放名额
     * <p>
     * 逻辑删除幂等（已删除时 0 行）；名额释放只应在退款终态流程中执行一次，
     * 由退款单 CAS 保证重复回调不会重复执行
     */
    private void releaseEnrollment(Long activityId, Long userId) {
        LambdaUpdateWrapper<ActivityEnrollmentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false)
                .set(ActivityEnrollmentEntity::getDeletedFlag, true);
        enrollmentManager.update(updateWrapper);
        if (!activityEnrollNumDao.decreaseEnrollNum(activityId)) {
            log.warn("退款释放名额失败：activityId:{}，userId:{}", activityId, userId);
        }
    }

    /**
     * 解析退款渠道：优先原路退回（订单支付时记录的渠道），兜底当前启用渠道
     */
    private PayChannelEnum resolveRefundChannel(ActivityOrderEntity order) {
        if (order.getPayChannel() != null) {
            return order.getPayChannel();
        }
        return requireActiveChannel().channel();
    }

    /**
     * 获取当前启用的支付渠道
     */
    private PaymentChannel requireActiveChannel() {
        PayChannelEnum channelEnum = PayChannelEnum.fromChannelCode(payChannelConfig);
        if (channelEnum == null) {
            throw new BusinessException(SystemErrorCode.SYSTEM_ERROR, "支付渠道未配置");
        }
        return requireChannel(channelEnum);
    }

    /**
     * 按渠道枚举查找已注册的实现类
     */
    private PaymentChannel requireChannel(PayChannelEnum channelEnum) {
        return channelProvider.stream()
                .filter(channel -> channel.channel() == channelEnum)
                .findFirst()
                .orElseThrow(() -> new BusinessException(SystemErrorCode.SYSTEM_ERROR,
                        "支付渠道不可用：" + channelEnum.getLabel()));
    }

    /**
     * 支付成功站内信（发送失败仅记录日志）
     */
    private void sendPaySuccessMessage(ActivityOrderEntity order) {
        try {
            Map<String, Object> contentParam = new HashMap<>();
            contentParam.put("activityTitle", getActivityTitle(order.getActivityId()));
            MessageTemplateSendForm sendForm = new MessageTemplateSendForm();
            sendForm.setMessageTemplateEnum(MessageTemplateEnum.ACTIVITY_ORDER_PAID);
            sendForm.setReceiverUserType(UserTypeEnum.PORTAL_USER);
            sendForm.setReceiverUserId(order.getUserId());
            sendForm.setDataId(order.getActivityId());
            sendForm.setContentParam(contentParam);
            messageService.sendTemplateMessage(sendForm);
        } catch (Exception e) {
            log.warn("支付成功站内信发送失败：orderNo:{}", order.getOrderNo(), e);
        }
    }

    /**
     * 退款到账站内信（发送失败仅记录日志）
     */
    private void sendRefundSuccessMessage(ActivityOrderEntity order) {
        try {
            Map<String, Object> contentParam = new HashMap<>();
            contentParam.put("activityTitle", getActivityTitle(order.getActivityId()));
            contentParam.put("amount", fenToYuan(order.getAmountFen()));
            MessageTemplateSendForm sendForm = new MessageTemplateSendForm();
            sendForm.setMessageTemplateEnum(MessageTemplateEnum.ACTIVITY_REFUND_SUCCESS);
            sendForm.setReceiverUserType(UserTypeEnum.PORTAL_USER);
            sendForm.setReceiverUserId(order.getUserId());
            sendForm.setDataId(order.getActivityId());
            sendForm.setContentParam(contentParam);
            messageService.sendTemplateMessage(sendForm);
        } catch (Exception e) {
            log.warn("退款到账站内信发送失败：orderNo:{}", order.getOrderNo(), e);
        }
    }

    private String getActivityTitle(Long activityId) {
        ActivityEntity activity = activityManager.getById(activityId);
        return activity == null ? "" : Objects.toString(activity.getTitle(), "");
    }

    /**
     * 分转元展示（如 150 → 1.50）
     */
    private String fenToYuan(Integer fen) {
        if (fen == null) {
            return "0.00";
        }
        return BigDecimal.valueOf(fen).movePointLeft(2).toPlainString();
    }
}
