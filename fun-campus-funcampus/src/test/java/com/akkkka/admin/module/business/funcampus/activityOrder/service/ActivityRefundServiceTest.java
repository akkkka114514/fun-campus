package com.akkkka.admin.module.business.funcampus.activityOrder.service;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundPolicy;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundReasonType;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityRefundEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityRefundApplyForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityRefundManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundChannelResult;
import com.akkkka.admin.module.business.funcampus.payment.service.PaymentService;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 活动报名退款 Service 单元测试
 * <p>
 * 覆盖：退款政策校验（不可退款/报名截止后/活动开始后）、
 * 退款申请（归属与状态校验、默认原因、CAS 回退路径、渠道受理失败与调用异常回退）、
 * 管理端重试（状态门槛、原因截断）、系统批量退款（单笔失败隔离、审核剔除场景）
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 */
@ExtendWith(MockitoExtension.class)
public class ActivityRefundServiceTest {

    @Mock
    private ActivityOrderManager orderManager;
    @Mock
    private ActivityRefundManager refundManager;
    @Mock
    private ActivityManager activityManager;
    @Mock
    private ActivityScheduleManager activityScheduleManager;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ActivityRefundService service;

    @AfterEach
    void cleanRequestUser() {
        SmartRequestUtil.remove();
    }

    /** 让事务模板真正执行传入的消费逻辑 */
    private void runTransactionNow() {
        doAnswer(invocation -> {
            Consumer<TransactionStatus> consumer = invocation.getArgument(0);
            consumer.accept(mock(TransactionStatus.class));
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
    }

    private void setPortalUser(Long userId) {
        RequestPortalUser portalUser = new RequestPortalUser();
        portalUser.setId(userId);
        portalUser.setUsername("student" + userId);
        portalUser.setUserType(UserTypeEnum.PORTAL_USER);
        SmartRequestUtil.setRequestUser(portalUser);
    }

    private ActivityOrderEntity order(Long id, OrderStatus status, Long userId) {
        ActivityOrderEntity order = new ActivityOrderEntity();
        order.setId(id);
        order.setOrderNo("AO1");
        order.setActivityId(7L);
        order.setUserId(userId);
        order.setAmountFen(150);
        order.setStatus(status);
        return order;
    }

    private ActivityRefundEntity failedRefund() {
        ActivityRefundEntity refund = new ActivityRefundEntity();
        refund.setId(5L);
        refund.setRefundNo("AR-ORIGIN");
        refund.setOrderId(9L);
        refund.setActivityId(7L);
        refund.setUserId(20L);
        refund.setAmountFen(150);
        refund.setReasonType(RefundReasonType.USER_APPLY);
        refund.setReason("临时有事");
        refund.setStatus(RefundStatus.FAILED);
        return refund;
    }

    private ActivityEntity activityWithPolicy(RefundPolicy policy) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(7L);
        activity.setTitle("羽毛球比赛");
        activity.setRefundPolicy(policy);
        return activity;
    }

    private ActivityScheduleEntity schedule(LocalDateTime enrollEnd, LocalDateTime activityStart) {
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setEnrollEndTime(enrollEnd);
        schedule.setActivityStartTime(activityStart);
        return schedule;
    }

    private RefundChannelResult channelResult(boolean accepted) {
        RefundChannelResult result = new RefundChannelResult();
        result.setAccepted(accepted);
        result.setChannelRefundNo(accepted ? "MOCKRF-1" : null);
        return result;
    }

    private ActivityRefundApplyForm applyForm(String reason) {
        ActivityRefundApplyForm form = new ActivityRefundApplyForm();
        form.setOrderNo("AO1");
        form.setReason(reason);
        return form;
    }

    /** 模拟 MyBatis-Plus insert 回写主键，使回退链路能拿到退款单 id */
    private void stubRefundSavedWithId(Long refundId) {
        when(refundManager.save(any())).thenAnswer(invocation -> {
            ((ActivityRefundEntity) invocation.getArgument(0)).setId(refundId);
            return true;
        });
    }

    /** 走完「政策校验通过」的前置链路 */
    private void stubRefundPolicyPass() {
        when(activityManager.getById(7L)).thenReturn(activityWithPolicy(RefundPolicy.BEFORE_ACTIVITY_START));
        when(activityScheduleManager.getById(7L))
                .thenReturn(schedule(null, LocalDateTime.now().plusHours(3)));
    }

    // ---------------------------------- 退款申请 refundApply ----------------------------------

    @Test
    void refundApply_whenOrderNotExist_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("订单不存在"));
    }

    @Test
    void refundApply_whenNotOrderOwner_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.PAID, 99L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("无权操作"));
    }

    @Test
    void refundApply_whenOrderClosed_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.CLOSED, 20L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("不可申请退款"));
    }

    @Test
    void refundApply_whenNotRefundablePolicy_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.PAID, 20L));
        when(activityManager.getById(7L)).thenReturn(activityWithPolicy(RefundPolicy.NOT_REFUNDABLE));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("不支持退款"));
    }

    @Test
    void refundApply_whenScheduleMissing_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.PAID, 20L));
        when(activityManager.getById(7L)).thenReturn(activityWithPolicy(RefundPolicy.BEFORE_ENROLL_END));
        when(activityScheduleManager.getById(7L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("时间表不存在"));
    }

    @Test
    void refundApply_whenPastEnrollEnd_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.PAID, 20L));
        when(activityManager.getById(7L)).thenReturn(activityWithPolicy(RefundPolicy.BEFORE_ENROLL_END));
        when(activityScheduleManager.getById(7L))
                .thenReturn(schedule(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(3)));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("报名已截止"));
    }

    @Test
    void refundApply_whenPastActivityStart_throwParamError() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.PAID, 20L));
        when(activityManager.getById(7L)).thenReturn(activityWithPolicy(RefundPolicy.BEFORE_ACTIVITY_START));
        when(activityScheduleManager.getById(7L))
                .thenReturn(schedule(null, LocalDateTime.now().minusHours(1)));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("活动已开始"));
    }

    @Test
    void refundApply_success_usesDefaultReasonAndStartsRefunding() {
        setPortalUser(20L);
        ActivityOrderEntity order = order(9L, OrderStatus.PAID, 20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order);
        stubRefundPolicyPass();
        runTransactionNow();
        when(orderManager.markRefundingCas(9L)).thenReturn(true);
        stubRefundSavedWithId(66L);
        when(paymentService.refund(eq(order), any())).thenReturn(channelResult(true));

        String refundNo = service.refundApply(applyForm("  "));

        assertTrue(refundNo.startsWith("AR"));
        ArgumentCaptor<ActivityRefundEntity> refundCaptor = ArgumentCaptor.forClass(ActivityRefundEntity.class);
        verify(refundManager).save(refundCaptor.capture());
        ActivityRefundEntity refund = refundCaptor.getValue();
        assertEquals(RefundReasonType.USER_APPLY, refund.getReasonType());
        assertEquals("用户申请退款", refund.getReason());
        assertEquals(RefundStatus.REFUNDING, refund.getStatus());
        assertEquals(150, refund.getAmountFen());
        assertEquals(9L, refund.getOrderId());
        verify(paymentService).refund(order, refund);
    }

    @Test
    void refundApply_success_customReasonKept() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.PAID, 20L));
        stubRefundPolicyPass();
        runTransactionNow();
        when(orderManager.markRefundingCas(9L)).thenReturn(true);
        stubRefundSavedWithId(66L);
        when(paymentService.refund(any(), any())).thenReturn(channelResult(true));

        service.refundApply(applyForm("临时有事"));

        ArgumentCaptor<ActivityRefundEntity> refundCaptor = ArgumentCaptor.forClass(ActivityRefundEntity.class);
        verify(refundManager).save(refundCaptor.capture());
        assertEquals("临时有事", refundCaptor.getValue().getReason());
    }

    @Test
    void refundApply_whenOrderRefundFailed_usesFromFailedCas() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.REFUND_FAILED, 20L));
        stubRefundPolicyPass();
        runTransactionNow();
        // 退款失败重新申请：CAS 从「退款失败→退款中」
        when(orderManager.markRefundingFromFailedCas(9L)).thenReturn(true);
        stubRefundSavedWithId(66L);
        when(paymentService.refund(any(), any())).thenReturn(channelResult(true));

        String refundNo = service.refundApply(applyForm(null));

        assertTrue(refundNo.startsWith("AR"));
        verify(orderManager).markRefundingFromFailedCas(9L);
        verify(orderManager, never()).markRefundingCas(any());
    }

    @Test
    void refundApply_whenOrderCasFails_throwAndNoRefundRow() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.PAID, 20L));
        stubRefundPolicyPass();
        runTransactionNow();
        when(orderManager.markRefundingCas(9L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("状态已变更"));
        verify(refundManager, never()).save(any());
        verify(paymentService, never()).refund(any(), any());
    }

    @Test
    void refundApply_whenChannelRejected_rollsBackAndThrow() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.PAID, 20L));
        stubRefundPolicyPass();
        runTransactionNow();
        when(orderManager.markRefundingCas(9L)).thenReturn(true);
        stubRefundSavedWithId(66L);
        when(paymentService.refund(any(), any())).thenReturn(channelResult(false));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("渠道退款受理失败"));
        // 受理失败：退款单标失败 + 订单回退为退款失败（可重新申请）
        verify(refundManager).markFailedCas(66L);
        verify(orderManager).markRefundFailedCas(9L);
    }

    @Test
    void refundApply_whenChannelThrows_rollsBackAndThrow() {
        setPortalUser(20L);
        when(orderManager.getByOrderNo("AO1")).thenReturn(order(9L, OrderStatus.PAID, 20L));
        stubRefundPolicyPass();
        runTransactionNow();
        when(orderManager.markRefundingCas(9L)).thenReturn(true);
        stubRefundSavedWithId(66L);
        // 渠道调用异常（如渠道不可用/网络异常）与受理失败同处理，不允许订单卡在退款中
        when(paymentService.refund(any(), any())).thenThrow(new BusinessException(UserErrorCode.SERVICE_BUSY, "渠道暂不可用"));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("渠道退款受理失败"));
        verify(refundManager).markFailedCas(66L);
        verify(orderManager).markRefundFailedCas(9L);
    }

    @Test
    void refundApply_whenNotPortalUser_throwParamError() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.refundApply(applyForm(null)));
        assertTrue(ex.getMessage().contains("用户类型错误"));
    }

    // ---------------------------------- 管理端重试 retryRefund ----------------------------------

    @Test
    void retryRefund_whenRefundNotExist_throwParamError() {
        when(refundManager.getByRefundNo("AR-ORIGIN")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.retryRefund("AR-ORIGIN"));
        assertTrue(ex.getMessage().contains("退款单不存在"));
    }

    @Test
    void retryRefund_whenRefundNotFailed_throwParamError() {
        ActivityRefundEntity origin = failedRefund();
        origin.setStatus(RefundStatus.SUCCESS);
        when(refundManager.getByRefundNo("AR-ORIGIN")).thenReturn(origin);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.retryRefund("AR-ORIGIN"));
        assertTrue(ex.getMessage().contains("退款失败的退款单可重试"));
    }

    @Test
    void retryRefund_whenOrderNotExist_throwParamError() {
        when(refundManager.getByRefundNo("AR-ORIGIN")).thenReturn(failedRefund());
        when(orderManager.getById(9L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.retryRefund("AR-ORIGIN"));
        assertTrue(ex.getMessage().contains("关联订单不存在"));
    }

    @Test
    void retryRefund_whenOrderNotRefundFailed_throwParamError() {
        when(refundManager.getByRefundNo("AR-ORIGIN")).thenReturn(failedRefund());
        when(orderManager.getById(9L)).thenReturn(order(9L, OrderStatus.PAID, 20L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.retryRefund("AR-ORIGIN"));
        assertTrue(ex.getMessage().contains("订单当前状态不可重试退款"));
    }

    @Test
    void retryRefund_success_newRefundRowWithTruncatedReason() {
        ActivityRefundEntity origin = failedRefund();
        origin.setReason("X".repeat(300));
        when(refundManager.getByRefundNo("AR-ORIGIN")).thenReturn(origin);
        when(orderManager.getById(9L)).thenReturn(order(9L, OrderStatus.REFUND_FAILED, 20L));
        runTransactionNow();
        when(orderManager.markRefundingFromFailedCas(9L)).thenReturn(true);
        stubRefundSavedWithId(66L);
        when(paymentService.refund(any(), any())).thenReturn(channelResult(true));

        String newRefundNo = service.retryRefund("AR-ORIGIN");

        assertNotNull(newRefundNo);
        assertNotEquals("AR-ORIGIN", newRefundNo);
        assertTrue(newRefundNo.startsWith("AR"));
        ArgumentCaptor<ActivityRefundEntity> refundCaptor = ArgumentCaptor.forClass(ActivityRefundEntity.class);
        verify(refundManager).save(refundCaptor.capture());
        ActivityRefundEntity retryRefund = refundCaptor.getValue();
        // 新单保留原单作为历史凭证；原因拼接后按 reason 列长做截断保护
        assertEquals(RefundReasonType.USER_APPLY, retryRefund.getReasonType());
        assertTrue(retryRefund.getReason().startsWith("管理端重试（原退款单 AR-ORIGIN）："));
        assertEquals(200, retryRefund.getReason().length());
    }

    @Test
    void retryRefund_whenChannelThrows_rollsBackAndThrow() {
        when(refundManager.getByRefundNo("AR-ORIGIN")).thenReturn(failedRefund());
        when(orderManager.getById(9L)).thenReturn(order(9L, OrderStatus.REFUND_FAILED, 20L));
        runTransactionNow();
        when(orderManager.markRefundingFromFailedCas(9L)).thenReturn(true);
        stubRefundSavedWithId(66L);
        when(paymentService.refund(any(), any())).thenThrow(new RuntimeException("渠道网络超时"));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.retryRefund("AR-ORIGIN"));
        assertTrue(ex.getMessage().contains("渠道退款受理失败"));
        verify(refundManager).markFailedCas(66L);
        verify(orderManager).markRefundFailedCas(9L);
    }

    // ---------------------------------- 系统批量退款 ----------------------------------

    @Test
    void refundForCanceledActivity_whenNoPaidOrders_noop() {
        when(orderManager.listPaidOrdersByActivity(7L)).thenReturn(List.of());

        service.refundForCanceledActivity(7L);

        verifyNoInteractions(paymentService);
    }

    @Test
    void refundForCanceledActivity_isolatesSingleFailureAndContinues() {
        ActivityOrderEntity first = order(1L, OrderStatus.PAID, 20L);
        first.setOrderNo("AO1");
        ActivityOrderEntity second = order(2L, OrderStatus.PAID, 21L);
        second.setOrderNo("AO2");
        when(orderManager.listPaidOrdersByActivity(7L)).thenReturn(List.of(first, second));
        runTransactionNow();
        when(orderManager.markRefundingCas(any())).thenReturn(true);
        stubRefundSavedWithId(66L);
        when(paymentService.refund(eq(first), any())).thenThrow(new RuntimeException("渠道网络超时"));
        when(paymentService.refund(eq(second), any())).thenReturn(channelResult(true));

        assertDoesNotThrow(() -> service.refundForCanceledActivity(7L));

        // 两笔都发起了渠道退款；第一笔异常被兜住回退为退款失败，第二笔正常受理
        verify(paymentService, times(2)).refund(any(), any());
        verify(refundManager).markFailedCas(66L);
        verify(orderManager).markRefundFailedCas(1L);
    }

    @Test
    void refundForCanceledActivity_refundsAllPaidOrdersWithCancelReason() {
        ActivityOrderEntity order = order(1L, OrderStatus.PAID, 20L);
        when(orderManager.listPaidOrdersByActivity(7L)).thenReturn(List.of(order));
        runTransactionNow();
        when(orderManager.markRefundingCas(1L)).thenReturn(true);
        stubRefundSavedWithId(66L);
        when(paymentService.refund(any(), any())).thenReturn(channelResult(true));

        service.refundForCanceledActivity(7L);

        ArgumentCaptor<ActivityRefundEntity> refundCaptor = ArgumentCaptor.forClass(ActivityRefundEntity.class);
        verify(refundManager).save(refundCaptor.capture());
        assertEquals(RefundReasonType.ACTIVITY_CANCEL, refundCaptor.getValue().getReasonType());
        assertEquals("活动已取消，系统自动退款", refundCaptor.getValue().getReason());
    }

    @Test
    void refundForRejectedPaidUsers_whenNullOrEmptyList_noop() {
        service.refundForRejectedPaidUsers(7L, null);
        service.refundForRejectedPaidUsers(7L, List.of());

        verifyNoInteractions(orderManager);
        verifyNoInteractions(paymentService);
    }

    @Test
    void refundForRejectedPaidUsers_refundsMatchingOrdersWithRejectReason() {
        ActivityOrderEntity order = order(1L, OrderStatus.PAID, 20L);
        when(orderManager.listPaidOrdersByActivityAndUsers(7L, List.of(20L))).thenReturn(List.of(order));
        runTransactionNow();
        when(orderManager.markRefundingCas(1L)).thenReturn(true);
        stubRefundSavedWithId(66L);
        when(paymentService.refund(any(), any())).thenReturn(channelResult(true));

        service.refundForRejectedPaidUsers(7L, List.of(20L));

        ArgumentCaptor<ActivityRefundEntity> refundCaptor = ArgumentCaptor.forClass(ActivityRefundEntity.class);
        verify(refundManager).save(refundCaptor.capture());
        assertEquals(RefundReasonType.ENROLL_REVIEW_REJECT, refundCaptor.getValue().getReasonType());
        assertEquals("报名审核未通过，系统自动退款", refundCaptor.getValue().getReason());
    }
}
