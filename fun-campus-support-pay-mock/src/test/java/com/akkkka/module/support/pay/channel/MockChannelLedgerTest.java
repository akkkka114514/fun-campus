package com.akkkka.module.support.pay.channel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mock 渠道订单簿 单元测试
 * <p>
 * 覆盖：预支付单幂等（同订单重复 prepay 返回同一单）、收款商户号落簿、不同订单独立、渠道侧标记已支付（幂等）
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 */
public class MockChannelLedgerTest {

    private final MockChannelLedger ledger = new MockChannelLedger();

    @Test
    void createPrepay_whenSameOrderNo_returnsSamePrepayOrder() {
        MockChannelLedger.MockPrepayOrder first = ledger.createPrepay("AO1", 100, "MCH1000001");
        MockChannelLedger.MockPrepayOrder second = ledger.createPrepay("AO1", 100, "MCH1000001");

        // 同订单重复 prepay 幂等：返回同一预支付单（真实渠道语义）
        assertSame(first, second);
        assertNotNull(ledger.getByPrepayId(first.getPrepayId()));
        assertNull(first.getPayTime());
        assertNull(first.getChannelOrderNo());
        // 收款商户号同步落簿，后续回调需与之匹配
        assertEquals("MCH1000001", first.getPayeeAccountNo());
    }

    @Test
    void createPrepay_whenDifferentOrderNo_returnsDifferentPrepayId() {
        assertNotEquals(ledger.createPrepay("AO1", 100, "MCH1000001").getPrepayId(),
                ledger.createPrepay("AO2", 100, "MCH1000001").getPrepayId());
    }

    @Test
    void markPaid_whenFirstTime_marksAndGeneratesChannelOrderNo() {
        MockChannelLedger.MockPrepayOrder prepay = ledger.createPrepay("AO1", 100, "MCH1000001");

        assertTrue(ledger.markPaid(prepay.getPrepayId()));
        assertNotNull(prepay.getPayTime());
        assertEquals("MOCKPAYAO1", prepay.getChannelOrderNo());
        // 重复受理幂等
        assertFalse(ledger.markPaid(prepay.getPrepayId()));
    }

    @Test
    void markPaid_whenPrepayNotExist_returnsFalse() {
        assertFalse(ledger.markPaid("MOCKPRE_NOT_EXIST"));
        assertNull(ledger.getByOrderNo("AO1"));
    }
}
