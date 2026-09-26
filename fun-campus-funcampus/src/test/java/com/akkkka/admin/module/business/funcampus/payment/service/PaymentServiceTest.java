package com.akkkka.admin.module.business.funcampus.payment.service;

import com.akkkka.admin.module.business.funcampus.MybatisPlusTestRegistry;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundStatus;
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
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundNotifyResult;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.module.support.message.constant.MessageTemplateEnum;
import com.akkkka.module.support.message.domain.MessageTemplateSendForm;
import com.akkkka.module.support.message.service.MessageService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 支付服务 单元测试（回调统一入口）
 * <p>
 * 覆盖：支付回调（成功落报名 + 通知、金额不一致拒绝、重复回调幂等、
 * 回调晚于关单的补偿分支、退款流程中到达）、退款回调（成功释放名额 + 通知、
 * 重复回调幂等、失败回调回退、名额释放失败兜底）
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 */
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    static {
        // releaseEnrollment 的 LambdaUpdateWrapper 会立即解析实体列，需注册 TableInfo
        MybatisPlusTestRegistry.register(ActivityEnrollmentEntity.class);
    }

    @Mock
    private ObjectProvider<PaymentChannel> channelProvider;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private ActivityOrderManager orderManager;
    @Mock
    private ActivityRefundManager refundManager;
    @Mock
    private ActivityEnrollmentService enrollmentService;
    @Mock
    private ActivityEnrollmentManager enrollmentManager;
    @Mock
    private ActivityEnrollNumDao activityEnrollNumDao;
    @Mock
    private ActivityManager activityManager;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private PaymentService service;

    /** 让事务模板真正执行传入的消费逻辑 */
    private void runTransactionNow() {
        doAnswer(invocation -> {
            Consumer<TransactionStatus> consumer = invocation.getArgument(0);
            consumer.accept(mock(TransactionStatus.class));
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
    }

    private ActivityOrderEntity order(Long id, Long userId, OrderStatus status, int amountFen) {
        ActivityOrderEntity order = new ActivityOrderEntity();
        order.setId(id);
        order.setOrderNo("AO202609260001");
        order.setActivityId(7L);
        order.setUserId(userId);
        order.setAmountFen(amountFen);
        order.setStatus(status);
        return order;
    }

    private ActivityRefundEntity refundingRefund() {
        ActivityRefundEntity refund = new ActivityRefundEntity();
        refund.setId(1L);
        refund.setRefundNo("AR202609260001");
        refund.setOrderId(9L);
        refund.setActivityId(7L);
        refund.setUserId(20L);
        refund.setAmountFen(150);
        refund.setStatus(RefundStatus.REFUNDING);
        return refund;
    }

    private ActivityEntity activity() {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(7L);
        activity.setTitle("羽毛球比赛");
        return activity;
    }

    private PayNotifyResult successPayNotify(Integer amountFen) {
        PayNotifyResult result = new PayNotifyResult();
        result.setChannel(PayChannelEnum.MOCK);
        result.setOrderNo("AO202609260001");
        result.setChannelOrderNo("MOCKPAYAO202609260001");
        result.setAmountFen(amountFen);
        result.setSuccess(true);
        return result;
    }

    private RefundNotifyResult refundNotify(boolean success, LocalDateTime refundTime) {
        RefundNotifyResult result = new RefundNotifyResult();
        result.setChannel(PayChannelEnum.MOCK);
        result.setRefundNo("AR202609260001");
        result.setChannelRefundNo("MOCKRFAR202609260001");
        result.setSuccess(success);
        result.setRefundTime(refundTime);
        return result;
    }

    // ---------------------------------- 支付回调 handlePayNotify ----------------------------------

    @Test
    void handlePayNotify_whenChannelResultFail_ignore() {
        PayNotifyResult notify = successPayNotify(150);
        notify.setSuccess(false);
        notify.setFailReason("余额不足");

        service.handlePayNotify(notify);

        verify(orderManager, never()).getByOrderNo(any());
    }

    @Test
    void handlePayNotify_whenOrderNoMissing_ignore() {
        PayNotifyResult notify = successPayNotify(150);
        notify.setOrderNo(null);

        service.handlePayNotify(notify);

        verify(orderManager, never()).getByOrderNo(any());
    }

    @Test
    void handlePayNotify_whenOrderNotExist_ignore() {
        when(orderManager.getByOrderNo("AO202609260001")).thenReturn(null);

        service.handlePayNotify(successPayNotify(150));

        verify(orderManager, never()).markPaidCas(any(), any(), any(), any());
    }

    @Test
    void handlePayNotify_whenAmountMismatch_reject() {
        when(orderManager.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, OrderStatus.WAIT_PAY, 150));

        // 回调金额 100 ≠ 订单金额 150：拒绝入账
        service.handlePayNotify(successPayNotify(100));

        verify(orderManager, never()).markPaidCas(any(), any(), any(), any());
        verify(enrollmentService, never()).saveEnrollmentRecord(any(), any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    @Test
    void handlePayNotify_whenAmountMissing_reject() {
        when(orderManager.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, OrderStatus.WAIT_PAY, 150));

        service.handlePayNotify(successPayNotify(null));

        verify(orderManager, never()).markPaidCas(any(), any(), any(), any());
        verify(enrollmentService, never()).saveEnrollmentRecord(any(), any());
    }

    @Test
    void handlePayNotify_success_marksPaidSavesEnrollmentAndNotifies() {
        when(orderManager.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, OrderStatus.WAIT_PAY, 150));
        runTransactionNow();
        when(orderManager.markPaidCas(eq(9L), eq("MOCKPAYAO202609260001"), eq(PayChannelEnum.MOCK), any()))
                .thenReturn(true);
        when(activityManager.getById(7L)).thenReturn(activity());

        service.handlePayNotify(successPayNotify(150));

        // CAS 成功 → 写报名记录（支付成功后才写，对签到/审核零侵入）
        verify(enrollmentService).saveEnrollmentRecord(7L, 20L);
        ArgumentCaptor<MessageTemplateSendForm> formCaptor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService).sendTemplateMessage(formCaptor.capture());
        MessageTemplateSendForm form = formCaptor.getValue();
        assertEquals(MessageTemplateEnum.ACTIVITY_ORDER_PAID, form.getMessageTemplateEnum());
        assertEquals(UserTypeEnum.PORTAL_USER, form.getReceiverUserType());
        assertEquals(20L, form.getReceiverUserId());
        assertEquals(7L, form.getDataId());
        assertEquals("羽毛球比赛", form.getContentParam().get("activityTitle"));
    }

    @Test
    void handlePayNotify_success_whenActivityMissing_stillNotifies() {
        when(orderManager.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, OrderStatus.WAIT_PAY, 150));
        runTransactionNow();
        when(orderManager.markPaidCas(eq(9L), any(), any(), any())).thenReturn(true);
        when(activityManager.getById(7L)).thenReturn(null);

        assertDoesNotThrow(() -> service.handlePayNotify(successPayNotify(150)));

        ArgumentCaptor<MessageTemplateSendForm> formCaptor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService).sendTemplateMessage(formCaptor.capture());
        assertEquals("", formCaptor.getValue().getContentParam().get("activityTitle"));
    }

    @Test
    void handlePayNotify_whenRepeatedCallback_idempotent() {
        when(orderManager.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, OrderStatus.WAIT_PAY, 150));
        runTransactionNow();
        // CAS 未成功，且重读后发现已支付 → 首次回调已完成处理
        when(orderManager.markPaidCas(eq(9L), any(), any(), any())).thenReturn(false);
        when(orderManager.getById(9L)).thenReturn(order(9L, 20L, OrderStatus.PAID, 150));

        assertDoesNotThrow(() -> service.handlePayNotify(successPayNotify(150)));

        verify(orderManager).getById(9L);
        verify(enrollmentService, never()).saveEnrollmentRecord(any(), any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    @Test
    void handlePayNotify_whenCallbackAfterClose_compensationBranchNoSideEffects() {
        when(orderManager.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, OrderStatus.WAIT_PAY, 150));
        runTransactionNow();
        // 竞态：关单任务先赢，但渠道资金已入账 → 记录补偿日志，不做状态回改
        when(orderManager.markPaidCas(eq(9L), any(), any(), any())).thenReturn(false);
        when(orderManager.getById(9L)).thenReturn(order(9L, 20L, OrderStatus.CLOSED, 150));

        assertDoesNotThrow(() -> service.handlePayNotify(successPayNotify(150)));

        verify(orderManager).getById(9L);
        verify(enrollmentService, never()).saveEnrollmentRecord(any(), any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    @Test
    void handlePayNotify_whenOrderInRefundProcess_skip() {
        when(orderManager.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, OrderStatus.WAIT_PAY, 150));
        runTransactionNow();
        when(orderManager.markPaidCas(eq(9L), any(), any(), any())).thenReturn(false);
        when(orderManager.getById(9L)).thenReturn(order(9L, 20L, OrderStatus.REFUNDING, 150));

        assertDoesNotThrow(() -> service.handlePayNotify(successPayNotify(150)));

        verify(orderManager).getById(9L);
        verify(enrollmentService, never()).saveEnrollmentRecord(any(), any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    // ---------------------------------- 退款回调 handleRefundNotify ----------------------------------

    @Test
    void handleRefundNotify_whenRefundNoMissing_ignore() {
        RefundNotifyResult notify = refundNotify(true, LocalDateTime.now());
        notify.setRefundNo(null);

        service.handleRefundNotify(notify);

        verify(refundManager, never()).getByRefundNo(any());
    }

    @Test
    void handleRefundNotify_whenRefundNotExist_ignore() {
        when(refundManager.getByRefundNo("AR202609260001")).thenReturn(null);

        service.handleRefundNotify(refundNotify(true, LocalDateTime.now()));

        verify(orderManager, never()).getById(any());
    }

    @Test
    void handleRefundNotify_whenOrderNotExist_ignore() {
        when(refundManager.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderManager.getById(9L)).thenReturn(null);

        service.handleRefundNotify(refundNotify(true, LocalDateTime.now()));

        verify(orderManager, never()).markRefundedCas(any());
    }

    @Test
    void handleRefundNotify_success_marksRefundedReleasesEnrollmentAndNotifies() {
        when(refundManager.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderManager.getById(9L)).thenReturn(order(9L, 20L, OrderStatus.REFUNDING, 150));
        runTransactionNow();
        LocalDateTime refundTime = LocalDateTime.now();
        when(refundManager.markSuccessCas(eq(1L), eq("MOCKRFAR202609260001"), any())).thenReturn(true);
        when(orderManager.markRefundedCas(9L)).thenReturn(true);
        when(activityEnrollNumDao.decreaseEnrollNum(7L)).thenReturn(true);
        when(activityManager.getById(7L)).thenReturn(activity());

        service.handleRefundNotify(refundNotify(true, refundTime));

        verify(refundManager).markSuccessCas(eq(1L), eq("MOCKRFAR202609260001"), any());
        verify(orderManager).markRefundedCas(9L);
        // 逻辑删除报名记录 + 释放名额
        verify(enrollmentManager).update(any(LambdaUpdateWrapper.class));
        verify(activityEnrollNumDao).decreaseEnrollNum(7L);
        ArgumentCaptor<MessageTemplateSendForm> formCaptor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService).sendTemplateMessage(formCaptor.capture());
        MessageTemplateSendForm form = formCaptor.getValue();
        assertEquals(MessageTemplateEnum.ACTIVITY_REFUND_SUCCESS, form.getMessageTemplateEnum());
        assertEquals(20L, form.getReceiverUserId());
        assertEquals("1.50", form.getContentParam().get("amount"));
    }

    @Test
    void handleRefundNotify_success_withNullRefundTime_defaultsToNow() {
        when(refundManager.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderManager.getById(9L)).thenReturn(order(9L, 20L, OrderStatus.REFUNDING, 150));
        runTransactionNow();
        when(refundManager.markSuccessCas(eq(1L), any(), any(LocalDateTime.class))).thenReturn(true);
        when(orderManager.markRefundedCas(9L)).thenReturn(true);
        when(activityEnrollNumDao.decreaseEnrollNum(7L)).thenReturn(true);
        when(activityManager.getById(7L)).thenReturn(activity());

        // 渠道回调未带完成时间：回退用当前时间落库
        assertDoesNotThrow(() -> service.handleRefundNotify(refundNotify(true, null)));

        verify(refundManager).markSuccessCas(eq(1L), any(), any(LocalDateTime.class));
    }

    @Test
    void handleRefundNotify_whenRepeatedCallback_idempotent() {
        when(refundManager.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderManager.getById(9L)).thenReturn(order(9L, 20L, OrderStatus.REFUNDING, 150));
        runTransactionNow();
        // 退款单已终态：重复回调不再释放名额、不重复通知
        when(refundManager.markSuccessCas(eq(1L), any(), any())).thenReturn(false);

        service.handleRefundNotify(refundNotify(true, LocalDateTime.now()));

        verify(orderManager, never()).markRefundedCas(any());
        verify(enrollmentManager, never()).update(any(LambdaUpdateWrapper.class));
        verify(activityEnrollNumDao, never()).decreaseEnrollNum(any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    @Test
    void handleRefundNotify_success_whenReleaseNumFails_stillNotifies() {
        when(refundManager.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderManager.getById(9L)).thenReturn(order(9L, 20L, OrderStatus.REFUNDING, 150));
        runTransactionNow();
        when(refundManager.markSuccessCas(eq(1L), any(), any())).thenReturn(true);
        when(orderManager.markRefundedCas(9L)).thenReturn(true);
        // 名额释放失败仅告警，不阻断到账流程
        when(activityEnrollNumDao.decreaseEnrollNum(7L)).thenReturn(false);
        when(activityManager.getById(7L)).thenReturn(activity());

        assertDoesNotThrow(() -> service.handleRefundNotify(refundNotify(true, LocalDateTime.now())));

        verify(messageService).sendTemplateMessage(any());
    }

    @Test
    void handleRefundNotify_fail_revertsToRefundFailedWithoutRelease() {
        when(refundManager.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderManager.getById(9L)).thenReturn(order(9L, 20L, OrderStatus.REFUNDING, 150));
        runTransactionNow();
        when(refundManager.markFailedCas(1L)).thenReturn(true);
        when(orderManager.markRefundFailedCas(9L)).thenReturn(true);

        service.handleRefundNotify(refundNotify(false, null));

        verify(refundManager).markFailedCas(1L);
        verify(orderManager).markRefundFailedCas(9L);
        // 失败不释放名额、不通知到账
        verify(activityEnrollNumDao, never()).decreaseEnrollNum(any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    @Test
    void handleRefundNotify_fail_whenOrderCasFails_stillHandled() {
        when(refundManager.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderManager.getById(9L)).thenReturn(order(9L, 20L, OrderStatus.REFUNDING, 150));
        runTransactionNow();
        when(refundManager.markFailedCas(1L)).thenReturn(true);
        when(orderManager.markRefundFailedCas(9L)).thenReturn(false);

        assertDoesNotThrow(() -> service.handleRefundNotify(refundNotify(false, null)));

        verify(refundManager).markFailedCas(1L);
    }
}
