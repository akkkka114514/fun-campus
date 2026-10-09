package com.akkkka.module.support.pay.channel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.akkkka.module.support.pay.domain.dto.PayNotifyResult;
import com.akkkka.module.support.pay.domain.dto.PayPrepayResult;
import com.akkkka.module.support.pay.domain.dto.PayQueryResult;
import com.akkkka.module.support.pay.domain.dto.RefundChannelResult;
import com.akkkka.module.support.pay.domain.dto.RefundNotifyResult;
import com.akkkka.module.support.pay.mocktest.StubOrderView;
import com.akkkka.module.support.pay.mocktest.StubRefundView;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

/**
 * Mock 支付渠道 单元测试（预支付单幂等 / 出站商户请求签名 / 支付回调验签 / 退款回调验签 / 渠道簿校验 / 商户号一致性 / 退款原商户 / 主动查单）
 * <p>
 * 覆盖：prepay 返回带 prepayId 的收银台链接且幂等；出站请求（下单/查单/退款）携带商户签名，
 * 篡改/缺失时渠道拒绝受理（退款以 accepted=false 拒绝）；验签失败、渠道无预支付单、
 * 渠道侧未支付的成功回调均置 verifyFailed 拒绝；回调商户号与预支付单不一致（含缺失）同样拒绝；
 * 渠道簿已支付时回调通过并带渠道流水号；退款商户号与原收款不一致时拒绝受理，
 * 受理成功后投递签名退款回调；退款回调与支付同一套验签（篡改/缺单号拒绝，合法签名解析通过）；
 * 查单三态（渠道无此单/未支付/已支付）
 * <p>
 * 依赖倒置后订单/退款单以端口视图（StubOrderView / StubRefundView）注入，不依赖业务实体
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 */
@ExtendWith(MockitoExtension.class)
public class MockPaymentChannelTest {

    @Mock
    private MockChannelCallbackClient callbackClient;

    private MockChannelLedger ledger;
    private MockPaySigner signer;
    private MockMerchantSigner merchantSigner;
    private MockPaymentChannel channel;

    @BeforeEach
    void setUp() {
        ledger = new MockChannelLedger();
        signer = new MockPaySigner();
        ReflectionTestUtils.setField(signer, "signSecret", "test-secret");
        ReflectionTestUtils.setField(signer, "timestampToleranceSeconds", 300L);
        merchantSigner = new MockMerchantSigner();
        ReflectionTestUtils.setField(merchantSigner, "merchantSignSecret", "test-merchant-secret");
        ReflectionTestUtils.setField(merchantSigner, "timestampToleranceSeconds", 300L);
        channel = new MockPaymentChannel(callbackClient, ledger, signer, merchantSigner);
        ReflectionTestUtils.setField(channel, "strictPrepayCheck", true);
    }

    private StubOrderView order() {
        StubOrderView order = new StubOrderView();
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

    private Map<String, String> refundCallbackParams(String refundNo, String refundStatus) {
        Map<String, String> params = new HashMap<>();
        params.put("refundNo", refundNo);
        params.put("refundStatus", refundStatus);
        params.put("channelRefundNo", "MOCKRF" + refundNo);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("sign", signer.sign(params));
        return params;
    }

    // ---------------------------------- prepay ----------------------------------

    @Test
    void prepay_whenCalledRepeatedly_returnsSamePrepayId() {
        PayPrepayResult first = channel.prepay(order());
        PayPrepayResult second = channel.prepay(order());

        assertTrue(first.getCashierUrl().startsWith("/portal/payment/mock/cashier?prepayId="));
        assertEquals(first.getCashierUrl(), second.getCashierUrl());
    }

    // ---------------------------------- 出站商户请求签名（渠道受理前验签） ----------------------------------

    private Map<String, String> merchantRequest(String operation) {
        Map<String, String> request = new LinkedHashMap<>();
        request.put("orderNo", "AO1");
        request.put("amountFen", "100");
        request.put("payeeAccountNo", "MCH1000001");
        request.put("operation", operation);
        request.put("timestamp", String.valueOf(System.currentTimeMillis()));
        request.put("sign", merchantSigner.sign(request));
        return request;
    }

    @Test
    void handlePrepayRequest_whenMerchantSignValid_accepted() {
        // 合法商户签名请求：渠道验签通过后受理，返回带 prepayId 的收银台链接
        PayPrepayResult result = channel.handlePrepayRequest(order(), merchantRequest("prepay"));

        assertTrue(result.getCashierUrl().startsWith("/portal/payment/mock/cashier?prepayId="));
    }

    @Test
    void handlePrepayRequest_whenMerchantSignTampered_rejected() {
        Map<String, String> request = merchantRequest("prepay");
        // 签名后篡改金额（伪造下单请求的典型攻击面）：渠道验签失败拒绝受理
        request.put("amountFen", "1");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> channel.handlePrepayRequest(order(), request));
        assertTrue(ex.getMessage().contains("商户请求验签失败"));
    }

    @Test
    void handlePrepayRequest_whenMerchantSignMissing_rejected() {
        Map<String, String> request = merchantRequest("prepay");
        request.remove("sign");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> channel.handlePrepayRequest(order(), request));
        assertTrue(ex.getMessage().contains("签名缺失"));
    }

    @Test
    void handleQueryRequest_whenMerchantSignTampered_rejected() {
        Map<String, String> request = merchantRequest("queryPay");
        // 签名后把订单号篡改指向另一笔订单：查单验签失败拒绝受理（不得误报 paid=false）
        request.put("orderNo", "AO-FORGED");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> channel.handleQueryRequest(order(), request));
        assertTrue(ex.getMessage().contains("商户请求验签失败"));
    }

    @Test
    void handleRefundRequest_whenMerchantSignTampered_rejected() {
        StubRefundView refund = new StubRefundView();
        refund.setRefundNo("RF1");
        Map<String, String> request = merchantRequest("refund");
        // 签名后篡改退款单号：退款验签失败以 accepted=false 拒绝受理
        request.put("refundNo", "RF-FORGED");

        RefundChannelResult result = channel.handleRefundRequest(order(), refund, request);

        assertFalse(result.isAccepted());
        assertTrue(result.getFailReason().contains("商户请求验签失败"));
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
        StubOrderView mismatched = order();
        mismatched.setPayeeAccountNo("MCH2000001");
        StubRefundView refund = new StubRefundView();
        refund.setRefundNo("RF1");

        RefundChannelResult result = channel.refund(mismatched, refund);

        assertFalse(result.isAccepted());
        assertEquals("退款商户号与收款商户号不一致，渠道拒绝原路退回", result.getFailReason());
    }

    @Test
    void refund_whenPayeeConsistent_accepted() {
        String prepayId = extractPrepayId(channel.prepay(order()));
        ledger.markPaid(prepayId);
        StubRefundView refund = new StubRefundView();
        refund.setRefundNo("RF1");

        RefundChannelResult result = channel.refund(order(), refund);

        assertTrue(result.isAccepted());
        assertEquals("MOCKRFRF1", result.getChannelRefundNo());
        // 受理成功后由渠道调度线程签名 HTTP 投递退款回调（与支付回调同一投递机制）
        verify(callbackClient).deliverRefundNotify("RF1", true, "MOCKRFRF1", 0L);
    }

    @Test
    void refund_whenLedgerMissing_acceptedByDegrade() {
        // 渠道簿无记录（开发环境服务重启后清空）：降级放行，不阻塞退款
        StubRefundView refund = new StubRefundView();
        refund.setRefundNo("RF1");

        RefundChannelResult result = channel.refund(order(), refund);

        assertTrue(result.isAccepted());
        verify(callbackClient).deliverRefundNotify("RF1", true, "MOCKRFRF1", 0L);
    }

    // ---------------------------------- parseRefundCallback ----------------------------------

    @Test
    void parseRefundCallback_whenSignTampered_verifyFailed() {
        Map<String, String> params = refundCallbackParams("RF1", "SUCCESS");
        // 签名后篡改退款状态（伪造退款结果）：验签必须失败
        params.put("refundStatus", "FAIL");

        RefundNotifyResult result = channel.parseRefundCallback(params);

        assertTrue(result.isVerifyFailed());
        assertEquals("签名校验失败", result.getFailReason());
    }

    @Test
    void parseRefundCallback_whenRefundNoMissing_verifyFailed() {
        Map<String, String> params = refundCallbackParams("RF1", "SUCCESS");
        params.remove("refundNo");
        params.put("sign", signer.sign(params));

        RefundNotifyResult result = channel.parseRefundCallback(params);

        assertTrue(result.isVerifyFailed());
        assertEquals("退款回调缺少退款单号", result.getFailReason());
    }

    @Test
    void parseRefundCallback_whenSuccess_passesWithChannelRefundNo() {
        RefundNotifyResult result = channel.parseRefundCallback(refundCallbackParams("RF1", "SUCCESS"));

        assertFalse(result.isVerifyFailed());
        assertTrue(result.isSuccess());
        assertEquals("MOCKRFRF1", result.getChannelRefundNo());
        assertNotNull(result.getRefundTime());
    }

    @Test
    void parseRefundCallback_whenFail_passes() {
        // 合法签名的失败退款通知：解析通过（退款单标失败，订单回退退款失败）
        RefundNotifyResult result = channel.parseRefundCallback(refundCallbackParams("RF1", "FAIL"));

        assertFalse(result.isVerifyFailed());
        assertFalse(result.isSuccess());
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
