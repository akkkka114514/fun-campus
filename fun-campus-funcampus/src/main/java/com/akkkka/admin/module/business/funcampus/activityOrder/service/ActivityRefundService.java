package com.akkkka.admin.module.business.funcampus.activityOrder.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundPolicy;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundReasonType;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityRefundEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityRefundApplyForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityRefundQueryForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityRefundVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityRefundManager;
import com.akkkka.admin.module.business.funcampus.activityOrder.util.OrderNoUtil;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundChannelResult;
import com.akkkka.admin.module.business.funcampus.payment.service.PaymentService;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 活动报名退款 Service
 * <p>
 * - 退款申请：订单 CAS（已支付→退款中）防重复申请，退款单落库后向渠道发起；
 *   渠道受理失败或调用异常均回退为「退款失败」，支持重新申请（退款失败→退款中）；
 * - 到账以渠道异步回调为准，由 PaymentService.handleRefundNotify 统一处理（释放名额 + 通知）；
 * - 审核剔除联动：报名审核剔除的已支付订单走系统退款（reasonType=报名审核未通过），
 *   不受活动退款政策限制，由审核流程在事务提交后调用；
 * - 活动取消联动：管理端取消活动后对全部已支付订单走系统退款（reasonType=活动取消），
 *   同样不受活动退款政策限制（待支付订单由超时关单任务兜底关闭）
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivityRefundService {

    private final ActivityOrderManager orderManager;

    private final ActivityRefundManager refundManager;

    private final ActivityManager activityManager;

    private final ActivityScheduleManager activityScheduleManager;

    private final TransactionTemplate transactionTemplate;

    private final PaymentService paymentService;

    /**
     * 用户申请退款
     * <p>
     * 流程：归属与状态校验（已支付/退款失败）→ 退款政策校验 → 事务[订单 CAS 置退款中 + 退款单落库]
     * → 渠道受理（失败则回退为退款失败）
     *
     * @return 退款单号
     */
    public String refundApply(ActivityRefundApplyForm form) {
        Long userId = getCurrentPortalUserId();
        ActivityOrderEntity order = orderManager.getByOrderNo(form.getOrderNo());
        if (order == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "订单不存在");
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "无权操作该订单");
        }
        // 已支付可申请；退款失败支持重新申请（渠道失败后用户重试）
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.REFUND_FAILED) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "当前订单状态不可申请退款");
        }
        validateRefundPolicy(order);

        String reason = form.getReason() == null || form.getReason().isBlank() ? "用户申请退款" : form.getReason();
        ActivityRefundEntity refund = buildRefund(order, RefundReasonType.USER_APPLY, reason);
        startRefunding(order, refund);

        // 事务提交后向渠道发起退款：受理成功不代表到账，最终以异步回调为准
        RefundChannelResult result = requestChannelRefund(order, refund);
        if (result == null || !result.isAccepted()) {
            rollbackRefunding(order, refund);
            throw new BusinessException(SystemErrorCode.SYSTEM_ERROR, "渠道退款受理失败，请稍后重试");
        }
        log.info("退款申请成功：orderNo:{}，refundNo:{}，amountFen:{}", order.getOrderNo(), refund.getRefundNo(), refund.getAmountFen());
        return refund.getRefundNo();
    }

    /**
     * 管理端分页查询退款单（全量，支持状态/活动/用户/关键词/时间范围筛选）
     */
    public PageResult<ActivityRefundVO> queryPageForAdmin(ActivityRefundQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityRefundVO> list = refundManager.getBaseMapper().queryPageForAdmin(page, queryForm);
        list.forEach(vo -> {
            RefundReasonType reasonType = RefundReasonType.fromCode(vo.getReasonType());
            vo.setReasonTypeName(reasonType == null ? null : reasonType.getLabel());
            RefundStatus refundStatus = RefundStatus.fromCode(vo.getStatus());
            vo.setStatusName(refundStatus == null ? null : refundStatus.getLabel());
        });
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 管理端重试失败退款单
     * <p>
     * - 仅「退款失败」的退款单可重试，订单须为「退款失败」（与用户重新申请走同一 CAS 链路）；
     * - 跳过退款政策校验：用户申请时已校验，此处仅重试渠道调用；
     * - 新建退款单（保留原单作为历史凭证），渠道再次受理失败仍回退为退款失败
     *
     * @param refundNo 原退款单号
     * @return 新退款单号
     */
    public String retryRefund(String refundNo) {
        ActivityRefundEntity originRefund = refundManager.getByRefundNo(refundNo);
        if (originRefund == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "退款单不存在");
        }
        if (originRefund.getStatus() != RefundStatus.FAILED) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "仅退款失败的退款单可重试");
        }
        ActivityOrderEntity order = orderManager.getById(originRefund.getOrderId());
        if (order == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "关联订单不存在");
        }
        if (order.getStatus() != OrderStatus.REFUND_FAILED) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "订单当前状态不可重试退款");
        }

        String originReason = originRefund.getReason() == null ? "" : originRefund.getReason();
        String retryReason = "管理端重试（原退款单 " + refundNo + "）：" + originReason;
        // reason 列 varchar(255)，拼接后做截断保护
        if (retryReason.length() > 200) {
            retryReason = retryReason.substring(0, 200);
        }
        ActivityRefundEntity retryRefund = buildRefund(order, originRefund.getReasonType(), retryReason);
        startRefunding(order, retryRefund);

        RefundChannelResult result = requestChannelRefund(order, retryRefund);
        if (result == null || !result.isAccepted()) {
            rollbackRefunding(order, retryRefund);
            throw new BusinessException(SystemErrorCode.SYSTEM_ERROR, "渠道退款受理失败，请稍后重试");
        }
        log.info("管理端退款重试成功：orderNo:{}，originRefundNo:{}，refundNo:{}",
                order.getOrderNo(), refundNo, retryRefund.getRefundNo());
        return retryRefund.getRefundNo();
    }

    /**
     * 报名审核剔除后联动系统退款（reasonType=报名审核未通过）
     * <p>
     * - 系统退款不受活动退款政策限制（剔除非用户意愿，费用必须退回）；
     * - 由审核流程在事务提交后调用，单笔独立 try/catch，失败仅记录日志不影响审核结果
     */
    public void refundForRejectedPaidUsers(Long activityId, List<Long> rejectUserIds) {
        if (rejectUserIds == null || rejectUserIds.isEmpty()) {
            return;
        }
        List<ActivityOrderEntity> paidOrders = orderManager.listPaidOrdersByActivityAndUsers(activityId, rejectUserIds);
        if (paidOrders.isEmpty()) {
            return;
        }
        for (ActivityOrderEntity order : paidOrders) {
            try {
                refundOrderAutomatically(order, RefundReasonType.ENROLL_REVIEW_REJECT, "报名审核未通过，系统自动退款");
            } catch (Exception e) {
                log.error("审核剔除联动退款失败：activityId:{}，orderNo:{}", activityId, order.getOrderNo(), e);
            }
        }
    }

    /**
     * 活动取消后批量系统退款（reasonType=活动取消）
     * <p>
     * - 仅处理已支付订单；待支付订单由超时关单任务兜底关闭，不在退款范围；
     * - 不受活动退款政策限制（活动取消非用户意愿，费用必须退回）；
     * - 由取消流程在状态更新成功后调用，单笔独立 try/catch，
     *   失败仅记日志（订单将为退款失败，可在退款管理页重试）
     */
    public void refundForCanceledActivity(Long activityId) {
        List<ActivityOrderEntity> paidOrders = orderManager.listPaidOrdersByActivity(activityId);
        if (paidOrders.isEmpty()) {
            return;
        }
        for (ActivityOrderEntity order : paidOrders) {
            try {
                refundOrderAutomatically(order, RefundReasonType.ACTIVITY_CANCEL, "活动已取消，系统自动退款");
            } catch (Exception e) {
                log.error("活动取消联动退款失败：activityId:{}，orderNo:{}", activityId, order.getOrderNo(), e);
            }
        }
    }

    /**
     * 单笔系统自动退款：与用户申请走同一套 CAS 与退款单流程，政策校验除外
     */
    private void refundOrderAutomatically(ActivityOrderEntity order, RefundReasonType reasonType, String reason) {
        ActivityRefundEntity refund = buildRefund(order, reasonType, reason);
        startRefunding(order, refund);

        RefundChannelResult result = requestChannelRefund(order, refund);
        if (result == null || !result.isAccepted()) {
            rollbackRefunding(order, refund);
            return;
        }
        log.info("系统自动退款已发起：orderNo:{}，refundNo:{}，reasonType:{}", order.getOrderNo(), refund.getRefundNo(), reasonType);
    }

    /**
     * 调用渠道退款并兜住异常：渠道不可用/网络异常与受理失败同样处理，
     * 回退为「退款失败」（可重新申请或由管理端重试），避免订单卡在退款中
     *
     * @return 渠道受理结果；调用异常时返回 null
     */
    private RefundChannelResult requestChannelRefund(ActivityOrderEntity order, ActivityRefundEntity refund) {
        try {
            return paymentService.refund(order, refund);
        } catch (Exception e) {
            log.error("渠道退款调用异常：orderNo:{}，refundNo:{}", order.getOrderNo(), refund.getRefundNo(), e);
            return null;
        }
    }

    /**
     * 事务：订单 CAS 置为退款中 + 退款单落库（同一事务，CAS 失败说明订单已被并发处理）
     */
    private void startRefunding(ActivityOrderEntity order, ActivityRefundEntity refund) {
        transactionTemplate.executeWithoutResult(status -> {
            boolean cas = order.getStatus() == OrderStatus.PAID
                    ? orderManager.markRefundingCas(order.getId())
                    : orderManager.markRefundingFromFailedCas(order.getId());
            if (!cas) {
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.PARAM_ERROR, "订单状态已变更，退款申请失败");
            }
            if (!refundManager.save(refund)) {
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.SERVICE_BUSY, "退款单创建失败，请稍后重试");
            }
        });
    }

    /**
     * 渠道受理失败：退款单标失败 + 订单回退为退款失败（用户可重新申请）
     */
    private void rollbackRefunding(ActivityOrderEntity order, ActivityRefundEntity refund) {
        transactionTemplate.executeWithoutResult(status -> {
            refundManager.markFailedCas(refund.getId());
            orderManager.markRefundFailedCas(order.getId());
        });
        log.warn("渠道受理退款失败，已回退为退款失败：orderNo:{}，refundNo:{}", order.getOrderNo(), refund.getRefundNo());
    }

    /**
     * 校验活动退款政策（用户主动退款受时间窗口限制）
     */
    private void validateRefundPolicy(ActivityOrderEntity order) {
        ActivityEntity activity = activityManager.getById(order.getActivityId());
        RefundPolicy policy = activity == null ? null : activity.getRefundPolicy();
        if (policy == null || policy == RefundPolicy.NOT_REFUNDABLE) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "该活动不支持退款");
        }
        ActivityScheduleEntity schedule = activityScheduleManager.getById(order.getActivityId());
        if (schedule == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动时间表不存在，暂不可申请退款");
        }
        LocalDateTime now = LocalDateTime.now();
        if (policy == RefundPolicy.BEFORE_ENROLL_END
                && (schedule.getEnrollEndTime() == null || !now.isBefore(schedule.getEnrollEndTime()))) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "报名已截止，按活动退款政策不可退款");
        }
        if (policy == RefundPolicy.BEFORE_ACTIVITY_START
                && (schedule.getActivityStartTime() == null || !now.isBefore(schedule.getActivityStartTime()))) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动已开始，按活动退款政策不可退款");
        }
    }

    private ActivityRefundEntity buildRefund(ActivityOrderEntity order, RefundReasonType reasonType, String reason) {
        ActivityRefundEntity refund = new ActivityRefundEntity();
        refund.setRefundNo(OrderNoUtil.generate(OrderNoUtil.REFUND_PREFIX));
        refund.setOrderId(order.getId());
        refund.setActivityId(order.getActivityId());
        refund.setUserId(order.getUserId());
        refund.setAmountFen(order.getAmountFen());
        refund.setReasonType(reasonType);
        refund.setReason(reason);
        refund.setStatus(RefundStatus.REFUNDING);
        refund.setDeletedFlag(false);
        return refund;
    }

    /**
     * 获取当前登录的门户用户id（退款接口仅允许门户用户访问）
     */
    private Long getCurrentPortalUserId() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (requestUser == null || requestUser.getUserType() != UserTypeEnum.PORTAL_USER) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "用户类型错误");
        }
        return requestUser.getUserId();
    }
}
