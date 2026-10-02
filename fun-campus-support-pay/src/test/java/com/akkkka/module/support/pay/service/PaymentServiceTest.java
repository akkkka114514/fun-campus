package com.akkkka.module.support.pay.service;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import com.akkkka.module.support.pay.channel.PaymentChannel;
import com.akkkka.module.support.pay.constant.PayChannelEnum;
import com.akkkka.module.support.pay.constant.PayLogAction;
import com.akkkka.module.support.pay.domain.dto.PayNotifyResult;
import com.akkkka.module.support.pay.domain.dto.RefundNotifyResult;
import com.akkkka.module.support.pay.port.PaymentBusinessPort;
import com.akkkka.module.support.pay.port.PaymentOrderPort;
import com.akkkka.module.support.pay.port.PaymentRefundPort;
import com.akkkka.module.support.pay.port.StubOrderView;
import com.akkkka.module.support.pay.port.StubRefundView;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.module.support.message.constant.MessageTemplateEnum;
import com.akkkka.module.support.message.domain.MessageTemplateSendForm;
import com.akkkka.module.support.message.service.MessageService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 支付服务 单元测试（回调统一入口）
 * <p>
 * 覆盖：支付回调（成功落报名 + 通知、金额不一致拒绝、重复回调幂等、
 * 回调晚于关单的补偿分支、退款流程中到达）、退款回调（成功释放名额 + 通知、
 * 重复回调幂等、失败回调回退、订单 CAS 失败仍完成到账流程）
 * <p>
 * 依赖倒置后业务协作全部走端口 mock：订单状态语义由 PayOrderView（StubOrderView 驱动），
 * 报名写入/名额释放/活动标题/留痕均为 PaymentBusinessPort 的行为验证
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 */
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private ObjectProvider<PaymentChannel> channelProvider;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private PaymentOrderPort orderPort;
    @Mock
    private PaymentRefundPort refundPort;
    @Mock
    private PaymentBusinessPort businessPort;
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

    private StubOrderView order(Long id, Long userId, int amountFen) {
        StubOrderView order = new StubOrderView();
        order.setId(id);
        order.setOrderNo("AO202609260001");
        order.setActivityId(7L);
        order.setUserId(userId);
        order.setAmountFen(amountFen);
        return order;
    }

    /** CAS 失败后重读：订单已支付（重复回调幂等场景） */
    private StubOrderView paidOrder() {
        StubOrderView order = order(9L, 20L, 150);
        order.setPaid(true);
        order.setStatusText("已支付");
        return order;
    }

    /** CAS 失败后重读：订单已关闭（关单任务先赢的竞态场景） */
    private StubOrderView closedOrder() {
        StubOrderView order = order(9L, 20L, 150);
        order.setClosed(true);
        order.setStatusText("已关闭");
        return order;
    }

    /** CAS 失败后重读：订单已进入退款流程 */
    private StubOrderView refundingOrder() {
        StubOrderView order = order(9L, 20L, 150);
        order.setInRefundFlow(true);
        order.setStatusText("退款中");
        return order;
    }

    private StubRefundView refundingRefund() {
        StubRefundView refund = new StubRefundView();
        refund.setId(1L);
        refund.setRefundNo("AR202609260001");
        refund.setOrderId(9L);
        return refund;
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
    void handlePayNotify_whenChannelResultFail_ignoreStateChangeAndRecordsLog() {
        when(orderPort.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, 150));
        PayNotifyResult notify = successPayNotify(150);
        notify.setSuccess(false);
        notify.setFailReason("余额不足");

        service.handlePayNotify(notify);

        // 失败回调：不改订单状态、不写报名，仅追加留痕
        verify(orderPort, never()).markPaidCas(any(), any(), any(), any());
        verify(businessPort, never()).saveEnrollmentRecord(any(), any());
        verify(businessPort).recordSystemLog(eq(9L), eq("AO202609260001"),
                eq(PayLogAction.PAY_FAILED), anyString());
    }

    @Test
    void handlePayNotify_whenOrderNoMissing_ignore() {
        PayNotifyResult notify = successPayNotify(150);
        notify.setOrderNo(null);

        service.handlePayNotify(notify);

        verify(orderPort, never()).getByOrderNo(any());
    }

    @Test
    void handlePayNotify_whenOrderNotExist_ignore() {
        when(orderPort.getByOrderNo("AO202609260001")).thenReturn(null);

        service.handlePayNotify(successPayNotify(150));

        verify(orderPort, never()).markPaidCas(any(), any(), any(), any());
    }

    @Test
    void handlePayNotify_whenAmountMismatch_reject() {
        when(orderPort.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, 150));

        // 回调金额 100 ≠ 订单金额 150：拒绝入账
        service.handlePayNotify(successPayNotify(100));

        verify(orderPort, never()).markPaidCas(any(), any(), any(), any());
        verify(businessPort, never()).saveEnrollmentRecord(any(), any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    @Test
    void handlePayNotify_whenAmountMissing_reject() {
        when(orderPort.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, 150));

        service.handlePayNotify(successPayNotify(null));

        verify(orderPort, never()).markPaidCas(any(), any(), any(), any());
        verify(businessPort, never()).saveEnrollmentRecord(any(), any());
    }

    @Test
    void handlePayNotify_success_marksPaidSavesEnrollmentAndNotifies() {
        when(orderPort.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        when(orderPort.markPaidCas(eq(9L), eq("MOCKPAYAO202609260001"), eq(PayChannelEnum.MOCK), any()))
                .thenReturn(true);
        when(businessPort.getActivityTitle(7L)).thenReturn("羽毛球比赛");

        service.handlePayNotify(successPayNotify(150));

        // CAS 成功 → 写报名记录（支付成功后才写，对签到/审核零侵入）
        verify(businessPort).saveEnrollmentRecord(7L, 20L);
        ArgumentCaptor<MessageTemplateSendForm> formCaptor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService).sendTemplateMessage(formCaptor.capture());
        MessageTemplateSendForm form = formCaptor.getValue();
        assertEquals(MessageTemplateEnum.ACTIVITY_ORDER_PAID, form.getMessageTemplateEnum());
        assertEquals(UserTypeEnum.PORTAL_USER, form.getReceiverUserType());
        assertEquals(20L, form.getReceiverUserId());
        assertEquals(7L, form.getDataId());
        assertEquals("羽毛球比赛", form.getContentParam().get("activityTitle"));
        verify(businessPort).recordSystemLog(eq(9L), eq("AO202609260001"),
                eq(PayLogAction.PAY_SUCCESS), anyString());
    }

    @Test
    void handlePayNotify_success_whenActivityTitleMissing_stillNotifies() {
        when(orderPort.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        when(orderPort.markPaidCas(eq(9L), any(), any(), any())).thenReturn(true);
        // 端口实现约定：活动不存在时返回空串（站内信仍照常发送）
        when(businessPort.getActivityTitle(7L)).thenReturn("");

        assertDoesNotThrow(() -> service.handlePayNotify(successPayNotify(150)));

        ArgumentCaptor<MessageTemplateSendForm> formCaptor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService).sendTemplateMessage(formCaptor.capture());
        assertEquals("", formCaptor.getValue().getContentParam().get("activityTitle"));
    }

    @Test
    void handlePayNotify_whenRepeatedCallback_idempotent() {
        when(orderPort.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        // CAS 未成功，且重读后发现已支付 → 首次回调已完成处理
        when(orderPort.markPaidCas(eq(9L), any(), any(), any())).thenReturn(false);
        when(orderPort.getById(9L)).thenReturn(paidOrder());

        assertDoesNotThrow(() -> service.handlePayNotify(successPayNotify(150)));

        verify(orderPort).getById(9L);
        verify(businessPort, never()).saveEnrollmentRecord(any(), any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    @Test
    void handlePayNotify_whenCallbackAfterClose_compensationBranchNoSideEffects() {
        when(orderPort.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        // 竞态：关单任务先赢，但渠道资金已入账 → 记录补偿日志，不做状态回改
        when(orderPort.markPaidCas(eq(9L), any(), any(), any())).thenReturn(false);
        when(orderPort.getById(9L)).thenReturn(closedOrder());

        assertDoesNotThrow(() -> service.handlePayNotify(successPayNotify(150)));

        verify(orderPort).getById(9L);
        verify(businessPort, never()).saveEnrollmentRecord(any(), any());
        verify(messageService, never()).sendTemplateMessage(any());
        verify(businessPort).recordSystemLog(eq(9L), eq("AO202609260001"),
                eq(PayLogAction.PAY_AFTER_CLOSE), anyString());
    }

    @Test
    void handlePayNotify_whenOrderInRefundProcess_skip() {
        when(orderPort.getByOrderNo("AO202609260001")).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        when(orderPort.markPaidCas(eq(9L), any(), any(), any())).thenReturn(false);
        when(orderPort.getById(9L)).thenReturn(refundingOrder());

        assertDoesNotThrow(() -> service.handlePayNotify(successPayNotify(150)));

        verify(orderPort).getById(9L);
        verify(businessPort, never()).saveEnrollmentRecord(any(), any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    // ---------------------------------- 退款回调 handleRefundNotify ----------------------------------

    @Test
    void handleRefundNotify_whenRefundNoMissing_ignore() {
        RefundNotifyResult notify = refundNotify(true, LocalDateTime.now());
        notify.setRefundNo(null);

        service.handleRefundNotify(notify);

        verify(refundPort, never()).getByRefundNo(any());
    }

    @Test
    void handleRefundNotify_whenRefundNotExist_ignore() {
        when(refundPort.getByRefundNo("AR202609260001")).thenReturn(null);

        service.handleRefundNotify(refundNotify(true, LocalDateTime.now()));

        verify(orderPort, never()).getById(any());
    }

    @Test
    void handleRefundNotify_whenOrderNotExist_ignore() {
        when(refundPort.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderPort.getById(9L)).thenReturn(null);

        service.handleRefundNotify(refundNotify(true, LocalDateTime.now()));

        verify(orderPort, never()).markRefundedCas(any());
    }

    @Test
    void handleRefundNotify_success_marksRefundedReleasesEnrollmentAndNotifies() {
        when(refundPort.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderPort.getById(9L)).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        LocalDateTime refundTime = LocalDateTime.now();
        when(refundPort.markRefundSuccessCas(eq(1L), eq("MOCKRFAR202609260001"), any())).thenReturn(true);
        when(orderPort.markRefundedCas(9L)).thenReturn(true);
        when(businessPort.getActivityTitle(7L)).thenReturn("羽毛球比赛");

        service.handleRefundNotify(refundNotify(true, refundTime));

        verify(refundPort).markRefundSuccessCas(eq(1L), eq("MOCKRFAR202609260001"), any());
        verify(orderPort).markRefundedCas(9L);
        // 逻辑删除报名记录 + 释放名额（已下沉到业务侧实现，支付模块只发端口调用）
        verify(businessPort).releaseEnrollment(7L, 20L);
        ArgumentCaptor<MessageTemplateSendForm> formCaptor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService).sendTemplateMessage(formCaptor.capture());
        MessageTemplateSendForm form = formCaptor.getValue();
        assertEquals(MessageTemplateEnum.ACTIVITY_REFUND_SUCCESS, form.getMessageTemplateEnum());
        assertEquals(20L, form.getReceiverUserId());
        assertEquals("1.50", form.getContentParam().get("amount"));
    }

    @Test
    void handleRefundNotify_success_withNullRefundTime_defaultsToNow() {
        when(refundPort.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderPort.getById(9L)).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        when(refundPort.markRefundSuccessCas(eq(1L), any(), any(LocalDateTime.class))).thenReturn(true);
        when(orderPort.markRefundedCas(9L)).thenReturn(true);
        when(businessPort.getActivityTitle(7L)).thenReturn("羽毛球比赛");

        // 渠道回调未带完成时间：回退用当前时间落库
        assertDoesNotThrow(() -> service.handleRefundNotify(refundNotify(true, null)));

        verify(refundPort).markRefundSuccessCas(eq(1L), any(), any(LocalDateTime.class));
    }

    @Test
    void handleRefundNotify_whenRepeatedCallback_idempotent() {
        when(refundPort.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderPort.getById(9L)).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        // 退款单已终态：重复回调不再释放名额、不重复通知
        when(refundPort.markRefundSuccessCas(eq(1L), any(), any())).thenReturn(false);

        service.handleRefundNotify(refundNotify(true, LocalDateTime.now()));

        verify(orderPort, never()).markRefundedCas(any());
        verify(businessPort, never()).releaseEnrollment(any(), any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    @Test
    void handleRefundNotify_success_whenOrderCasFails_stillNotifies() {
        when(refundPort.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderPort.getById(9L)).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        when(refundPort.markRefundSuccessCas(eq(1L), any(), any())).thenReturn(true);
        // 订单状态非退款中仅告警，不阻断退款单终态、名额释放与到账通知
        when(orderPort.markRefundedCas(9L)).thenReturn(false);
        when(businessPort.getActivityTitle(7L)).thenReturn("羽毛球比赛");

        assertDoesNotThrow(() -> service.handleRefundNotify(refundNotify(true, LocalDateTime.now())));

        verify(businessPort).releaseEnrollment(7L, 20L);
        verify(messageService).sendTemplateMessage(any());
        verify(businessPort).recordSystemLog(eq(9L), eq("AO202609260001"),
                eq(PayLogAction.REFUND_SUCCESS), anyString());
    }

    @Test
    void handleRefundNotify_fail_revertsToRefundFailedWithoutRelease() {
        when(refundPort.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderPort.getById(9L)).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        when(refundPort.markRefundFailedCas(1L)).thenReturn(true);
        when(orderPort.markRefundFailedCas(9L)).thenReturn(true);

        service.handleRefundNotify(refundNotify(false, null));

        verify(refundPort).markRefundFailedCas(1L);
        verify(orderPort).markRefundFailedCas(9L);
        // 失败不释放名额、不通知到账，仅追加留痕
        verify(businessPort, never()).releaseEnrollment(any(), any());
        verify(messageService, never()).sendTemplateMessage(any());
        verify(businessPort).recordSystemLog(eq(9L), eq("AO202609260001"),
                eq(PayLogAction.REFUND_FAILED), anyString());
    }

    @Test
    void handleRefundNotify_fail_whenOrderCasFails_stillHandled() {
        when(refundPort.getByRefundNo("AR202609260001")).thenReturn(refundingRefund());
        when(orderPort.getById(9L)).thenReturn(order(9L, 20L, 150));
        runTransactionNow();
        when(refundPort.markRefundFailedCas(1L)).thenReturn(true);
        when(orderPort.markRefundFailedCas(9L)).thenReturn(false);

        assertDoesNotThrow(() -> service.handleRefundNotify(refundNotify(false, null)));

        verify(refundPort).markRefundFailedCas(1L);
    }
}
