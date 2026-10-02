package com.akkkka.admin.module.business.funcampus.payment.channel;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityRefundEntity;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayNotifyResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayPrepayResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayQueryResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundChannelResult;
import com.akkkka.admin.module.business.funcampus.payment.service.PaymentNotifyHandler;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mock 支付渠道 单元测试（预支付单幂等 / 回调验签 / 渠道簿校验 / 商户号一致性 / 退款原商户 / 主动查单）
 * <p>
 * 覆盖：prepay 返回带 prepayId 的收银台链接且幂等；验签失败、渠道无预支付单、
 * 渠道侧未支付的成功回调均置 verifyFailed 拒绝；回调商户号与预支付单不一致（含缺失）同样拒绝；
 * 渠道簿已支付时回调通过并带渠道流水号；退款商户号与原收款不一致时拒绝受理；
 * 查单三态（渠道无此单/未支付/已支付）
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 */
@ExtendWith(MockitoExtension.class)
public class MockPaymentChannelTest {

    @Mock
    private ObjectProvider<PaymentNotifyHandler> notifyHandlerProvider;
    @Mock
    private TaskScheduler mockPayCallbackScheduler;

    private MockChannelLedger ledger;
    private MockPaySigner signer;
    private MockPaymentChannel channel;

    @BeforeEach
    void setUp() {
        ledger = new MockChannelLedger();
        signer = new MockPaySigner();
        ReflectionTestUtils.setField(signer, "signSecret", "test-secret");
        ReflectionTestUtils.setField(signer, "timestampToleranceSeconds", 300L);
        channel = new MockPaymentChannel(notifyHandlerProvider, ledger, signer, mockPayCallbackScheduler);
        ReflectionTestUtils.setField(channel, "strictPrepayCheck", true);
    }

    private ActivityOrderEntity order() {
        ActivityOrderEntity order = new ActivityOrderEntity();
        order.setOrderNo("AO1");
        order.setActivityId(7L);
        order.setUserId(20L);
        order.setAmountFen(100);
        order.setPayeeAccountNo("MCH1000001");
        return order;
    }

    private Map<String, String> callbackParams(String orderNo, String payStatus) {
        return callbackParams(orderNo, payStatus, "MCH1000001");
    }

    private Map<String, String> callbackParams(String orderNo, String payStatus, String payeeAccountNo) {
        Map<String, String> params = new HashMap<>();
        params.put("orderNo", orderNo);
        params.put("amountFen", "100");
        params.put("payStatus", payStatus);
        if (payeeAccountNo != null) {
            params.put("payeeAccountNo", payeeAccountNo);
        }
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("sign", signer.sign(params));
        return params;
    }

    private String extractPrepayId(PayPrepayResult prepay) {
        String url = prepay.getCashierUrl();
        return url.substring(url.indexOf('=') + 1);
    }

    // ---------------------------------- prepay ----------------------------------

    @Test
    void prepay_whenCalledRepeatedly_returnsSamePrepayId() {
        PayPrepayResult first = channel.prepay(order());
        PayPrepayResult second = channel.prepay(order());

        assertTrue(first.getCashierUrl().startsWith("/portal/payment/mock/cashier?prepayId="));
        assertEquals(first.getCashierUrl(), second.getCashierUrl());
    }

    // ---------------------------------- parsePayCallback ----------------------------------

    @Test
    void parsePayCallback_whenSignTampered_verifyFailed() {
        Map<String, String> params = callbackParams("AO1", "SUCCESS");
        // 签名后篡改金额：验签必须失败
        params.put("amountFen", "1");

        PayNotifyResult result = channel.parsePayCallback(params);

        assertTrue(result.isVerifyFailed());
        assertEquals("签名校验失败", result.getFailReason());
    }

    @Test
    void parsePayCallback_whenPrepayNotFound_verifyFailed() {
        // 合法签名但从未 prepay：渠道不存在该订单的预支付单
        PayNotifyResult result = channel.parsePayCallback(callbackParams("AO-NEVER-PREPAY", "SUCCESS"));

        assertTrue(result.isVerifyFailed());
        assertEquals("渠道不存在该订单的预支付单", result.getFailReason());
    }

    @Test
    void parsePayCallback_whenPrepayNotPaid_verifyFailed() {
        channel.prepay(order());

        // 渠道侧未支付（用户未在收银台完成支付）："支付成功"回调来源不可信
        PayNotifyResult result = channel.parsePayCallback(callbackParams("AO1", "SUCCESS"));

        assertTrue(result.isVerifyFailed());
        assertEquals("渠道侧预支付单未支付，支付成功回调不可信", result.getFailReason());
    }

    @Test
    void parsePayCallback_whenPrepayPaid_passesWithChannelOrderNo() {
        String prepayId = extractPrepayId(channel.prepay(order()));
        ledger.markPaid(prepayId);

        PayNotifyResult result = channel.parsePayCallback(callbackParams("AO1", "SUCCESS"));

        assertFalse(result.isVerifyFailed());
        assertTrue(result.isSuccess());
        assertEquals("MOCKPAYAO1", result.getChannelOrderNo());
        assertEquals(100, result.getAmountFen().intValue());
    }

    @Test
    void parsePayCallback_whenPayFail_passesWithoutPrepayPaidCheck() {
        // 支付失败通知不要求渠道簿已支付（仅记录留痕、不改订单状态）
        PayNotifyResult result = channel.parsePayCallback(callbackParams("AO1", "FAIL"));

        assertFalse(result.isVerifyFailed());
        assertFalse(result.isSuccess());
    }

    @Test
    void parsePayCallback_whenPayeeMismatch_verifyFailed() {
        String prepayId = extractPrepayId(channel.prepay(order()));
        ledger.markPaid(prepayId);

        // 合法签名但商户号被换成另一主体（串户伪造）：与渠道簿预支付单比对后拒绝
        PayNotifyResult result = channel.parsePayCallback(callbackParams("AO1", "SUCCESS", "MCH2000001"));

        assertTrue(result.isVerifyFailed());
        assertEquals("回调商户号与预支付单不一致", result.getFailReason());
    }

    @Test
    void parsePayCallback_whenPayeeMissing_verifyFailed() {
        String prepayId = extractPrepayId(channel.prepay(order()));
        ledger.markPaid(prepayId);

        // 缺商户号的成功回调（真实渠道回调必带 mch_id）：同样拒绝
        PayNotifyResult result = channel.parsePayCallback(callbackParams("AO1", "SUCCESS", null));

        assertTrue(result.isVerifyFailed());
        assertEquals("回调商户号与预支付单不一致", result.getFailReason());
    }

    // ---------------------------------- refund ----------------------------------

    @Test
    void refund_whenPayeeMismatch_rejected() {
        String prepayId = extractPrepayId(channel.prepay(order()));
        ledger.markPaid(prepayId);

        // 退款商户号与渠道簿收款商户号不一致：渠道拒绝原路退回
        ActivityOrderEntity mismatched = order();
        mismatched.setPayeeAccountNo("MCH2000001");
        ActivityRefundEntity refund = new ActivityRefundEntity();
        refund.setRefundNo("RF1");

        RefundChannelResult result = channel.refund(mismatched, refund);

        assertFalse(result.isAccepted());
        assertEquals("退款商户号与收款商户号不一致，渠道拒绝原路退回", result.getFailReason());
    }

    @Test
    void refund_whenPayeeConsistent_accepted() {
        String prepayId = extractPrepayId(channel.prepay(order()));
        ledger.markPaid(prepayId);
        ActivityRefundEntity refund = new ActivityRefundEntity();
        refund.setRefundNo("RF1");

        RefundChannelResult result = channel.refund(order(), refund);

        assertTrue(result.isAccepted());
        assertEquals("MOCKRFRF1", result.getChannelRefundNo());
    }

    @Test
    void refund_whenLedgerMissing_acceptedByDegrade() {
        // 渠道簿无记录（开发环境服务重启后清空）：降级放行，不阻塞退款
        ActivityRefundEntity refund = new ActivityRefundEntity();
        refund.setRefundNo("RF1");

        RefundChannelResult result = channel.refund(order(), refund);

        assertTrue(result.isAccepted());
    }

    // ---------------------------------- queryPay ----------------------------------

    @Test
    void queryPay_whenNoPrepay_notPaid() {
        PayQueryResult result = channel.queryPay(order());

        assertFalse(result.isPaid());
        assertEquals("渠道无此单", result.getStatusDesc());
    }

    @Test
    void queryPay_whenPrepayUnpaid_notPaid() {
        channel.prepay(order());

        PayQueryResult result = channel.queryPay(order());

        assertFalse(result.isPaid());
        assertEquals("预支付单未支付", result.getStatusDesc());
    }

    @Test
    void queryPay_whenPaid_returnsChannelInfo() {
        String prepayId = extractPrepayId(channel.prepay(order()));
        ledger.markPaid(prepayId);

        PayQueryResult result = channel.queryPay(order());

        assertTrue(result.isPaid());
        assertEquals("MOCKPAYAO1", result.getChannelOrderNo());
        assertEquals(100, result.getAmountFen().intValue());
        assertNotNull(result.getPayTime());
        assertEquals("已支付", result.getStatusDesc());
    }
}
