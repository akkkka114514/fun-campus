package com.akkkka.admin.module.business.funcampus.activityOrder.job;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderLogAction;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityOrderLogService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.payment.constant.PayChannelEnum;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayNotifyResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayQueryResult;
import com.akkkka.admin.module.business.funcampus.payment.service.PaymentService;
import com.akkkka.module.support.message.constant.MessageTemplateEnum;
import com.akkkka.module.support.message.domain.MessageTemplateSendForm;
import com.akkkka.module.support.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
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
 * 待支付订单超时处理任务 单元测试
 * <p>
 * 覆盖：无超时订单短路、正常关单（CAS 关单 + 释放名额 + 通知）、
 * 查单发现渠道已支付时补偿入账不关单、查单异常时跳过不关单、
 * 与支付回调竞态（CAS 失败跳过）、单条异常隔离、名额释放失败兜底
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 */
@ExtendWith(MockitoExtension.class)
public class ActivityOrderTimeoutJobTest {

    @Mock
    private ActivityOrderManager orderManager;
    @Mock
    private ActivityEnrollNumDao activityEnrollNumDao;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private ActivityManager activityManager;
    @Mock
    private MessageService messageService;
    @Mock
    private ActivityOrderLogService orderLogService;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ActivityOrderTimeoutJob job;

    /** 让事务模板真正执行传入的消费逻辑 */
    private void runTransactionNow() {
        doAnswer(invocation -> {
            Consumer<TransactionStatus> consumer = invocation.getArgument(0);
            consumer.accept(mock(TransactionStatus.class));
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
    }

    /**
     * 默认 stub：渠道查单返回「未支付」走关单流程，个别用例可覆盖
     */
    @BeforeEach
    void setUp() {
        PayQueryResult notPaid = new PayQueryResult();
        notPaid.setChannel(PayChannelEnum.MOCK);
        notPaid.setPaid(false);
        notPaid.setStatusDesc("预支付单未支付");
        lenient().when(paymentService.queryPay(any())).thenReturn(notPaid);
    }

    private ActivityOrderEntity timeoutOrder(Long id, String orderNo) {
        ActivityOrderEntity order = new ActivityOrderEntity();
        order.setId(id);
        order.setOrderNo(orderNo);
        order.setActivityId(7L);
        order.setUserId(20L);
        order.setAmountFen(150);
        order.setStatus(OrderStatus.WAIT_PAY);
        order.setExpireTime(LocalDateTime.now().minusMinutes(1));
        return order;
    }

    @Test
    void run_whenNoTimeoutOrders_returnNoopHint() {
        when(orderManager.listTimeoutOrders(any(LocalDateTime.class), eq(1000))).thenReturn(List.of());

        assertEquals("没有超时未支付订单，无需处理", job.run(null));
    }

    @Test
    void run_closesAllTimeoutOrders() {
        ActivityOrderEntity first = timeoutOrder(1L, "AO1");
        ActivityOrderEntity second = timeoutOrder(2L, "AO2");
        when(orderManager.listTimeoutOrders(any(LocalDateTime.class), eq(1000))).thenReturn(List.of(first, second));
        runTransactionNow();
        when(orderManager.closeIfWaitPayCas(eq(1L), any())).thenReturn(true);
        when(orderManager.closeIfWaitPayCas(eq(2L), any())).thenReturn(true);
        when(activityEnrollNumDao.decreaseEnrollNum(7L)).thenReturn(true);
        ActivityEntity activity = new ActivityEntity();
        activity.setId(7L);
        activity.setTitle("羽毛球比赛");
        when(activityManager.getById(7L)).thenReturn(activity);

        String result = job.run(null);

        assertEquals("超时订单处理完成：查单补偿0单，关闭2单，跳过0单，失败0单", result);
        verify(activityEnrollNumDao, times(2)).decreaseEnrollNum(7L);
        ArgumentCaptor<MessageTemplateSendForm> formCaptor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService, times(2)).sendTemplateMessage(formCaptor.capture());
        MessageTemplateSendForm form = formCaptor.getAllValues().get(0);
        assertEquals(MessageTemplateEnum.ACTIVITY_ORDER_TIMEOUT, form.getMessageTemplateEnum());
        assertEquals(20L, form.getReceiverUserId());
        assertEquals("羽毛球比赛", form.getContentParam().get("activityTitle"));
    }

    @Test
    void run_whenOrderAlreadyHandled_skippedWithoutRelease() {
        when(orderManager.listTimeoutOrders(any(LocalDateTime.class), eq(1000)))
                .thenReturn(List.of(timeoutOrder(1L, "AO1")));
        runTransactionNow();
        // 支付回调/用户取消抢先处理：CAS 失败直接跳过
        when(orderManager.closeIfWaitPayCas(eq(1L), any())).thenReturn(false);

        String result = job.run(null);

        assertEquals("超时订单处理完成：查单补偿0单，关闭0单，跳过1单，失败0单", result);
        verify(activityEnrollNumDao, never()).decreaseEnrollNum(any());
        verify(messageService, never()).sendTemplateMessage(any());
    }

    @Test
    void run_whenOneOrderFails_isolatesAndContinues() {
        when(orderManager.listTimeoutOrders(any(LocalDateTime.class), eq(1000)))
                .thenReturn(List.of(timeoutOrder(1L, "AO1"), timeoutOrder(2L, "AO2")));
        runTransactionNow();
        when(orderManager.closeIfWaitPayCas(eq(1L), any())).thenThrow(new RuntimeException("数据库连接异常"));
        when(orderManager.closeIfWaitPayCas(eq(2L), any())).thenReturn(true);
        when(activityEnrollNumDao.decreaseEnrollNum(7L)).thenReturn(true);
        when(activityManager.getById(7L)).thenReturn(null);

        String result = job.run(null);

        // 第一条失败不影响第二条，下一轮再重试
        assertTrue(result.contains("关闭1单"));
        assertTrue(result.contains("失败1单"));
        verify(messageService, times(1)).sendTemplateMessage(any());
    }

    @Test
    void run_whenDecreaseEnrollNumFails_stillCountedClosed() {
        when(orderManager.listTimeoutOrders(any(LocalDateTime.class), eq(1000)))
                .thenReturn(List.of(timeoutOrder(1L, "AO1")));
        runTransactionNow();
        when(orderManager.closeIfWaitPayCas(eq(1L), any())).thenReturn(true);
        // 名额释放失败仅告警（可能已被其他链路释放），订单仍视为关闭成功
        when(activityEnrollNumDao.decreaseEnrollNum(7L)).thenReturn(false);
        when(activityManager.getById(7L)).thenReturn(null);

        String result = job.run(null);

        assertEquals("超时订单处理完成：查单补偿0单，关闭1单，跳过0单，失败0单", result);
    }

    @Test
    void run_whenQueryPayReportsPaid_compensatesWithoutClosing() {
        ActivityOrderEntity order = timeoutOrder(1L, "AO1");
        when(orderManager.listTimeoutOrders(any(LocalDateTime.class), eq(1000))).thenReturn(List.of(order));
        PayQueryResult paid = new PayQueryResult();
        paid.setChannel(PayChannelEnum.MOCK);
        paid.setOrderNo("AO1");
        paid.setPaid(true);
        paid.setChannelOrderNo("MOCKPAYAO1");
        paid.setAmountFen(150);
        paid.setPayTime(LocalDateTime.now());
        when(paymentService.queryPay(order)).thenReturn(paid);

        String result = job.run(null);

        // 回调丢失：查单发现渠道已支付 → 补偿入账，不再关单
        assertEquals("超时订单处理完成：查单补偿1单，关闭0单，跳过0单，失败0单", result);
        verify(orderManager, never()).closeIfWaitPayCas(any(), any());
        verify(activityEnrollNumDao, never()).decreaseEnrollNum(any());
        ArgumentCaptor<PayNotifyResult> notifyCaptor = ArgumentCaptor.forClass(PayNotifyResult.class);
        verify(paymentService).handlePayNotify(notifyCaptor.capture());
        PayNotifyResult notify = notifyCaptor.getValue();
        assertTrue(notify.isSuccess());
        assertEquals("AO1", notify.getOrderNo());
        assertEquals("MOCKPAYAO1", notify.getChannelOrderNo());
        assertEquals(150, notify.getAmountFen().intValue());
        verify(orderLogService).recordSystem(eq(order), eq(OrderLogAction.QUERY_COMPENSATE), anyString());
    }

    @Test
    void run_whenQueryPayFails_skipsWithoutClosing() {
        when(orderManager.listTimeoutOrders(any(LocalDateTime.class), eq(1000)))
                .thenReturn(List.of(timeoutOrder(1L, "AO1")));
        when(paymentService.queryPay(any())).thenThrow(new RuntimeException("渠道查单超时"));

        String result = job.run(null);

        // 查单失败不敢贸然关单（资金可能已入账），跳过留待下一轮
        assertEquals("超时订单处理完成：查单补偿0单，关闭0单，跳过0单，失败1单", result);
        verify(orderManager, never()).closeIfWaitPayCas(any(), any());
        verify(paymentService, never()).handlePayNotify(any());
    }
}
