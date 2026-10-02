package com.akkkka.admin.module.business.funcampus.payment.channel;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Mock 渠道订单簿（渠道侧预支付单存储）
 * <p>
 * 模拟真实渠道的「预支付单」体系：
 * - prepay 时渠道侧生成 prepay_id 并落簿；同一订单重复 prepay 返回同一预支付单（与真实渠道语义一致）；
 * - 用户在收银台完成支付后，渠道簿标记已支付并生成渠道流水号；
 * - 回调与查单都以渠道簿为准：没有预支付单、或预支付单未支付的订单，
 *   渠道侧不可能给出「支付成功」的结论（防伪造的第二道闸）
 * <p>
 * 仅 mock 渠道注册；内存存储、重启即清空（开发环境语义）
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Component
@ConditionalOnProperty(name = "pay.channel", havingValue = "mock")
public class MockChannelLedger {

    private static final String PREPAY_ID_PREFIX = "MOCKPRE";

    private static final String CHANNEL_ORDER_NO_PREFIX = "MOCKPAY";

    private final Map<String, MockPrepayOrder> prepayById = new ConcurrentHashMap<>();

    private final Map<String, MockPrepayOrder> prepayByOrderNo = new ConcurrentHashMap<>();

    /**
     * 创建/获取渠道预支付单：同订单重复 prepay 幂等返回同一预支付单
     */
    public MockPrepayOrder createPrepay(String orderNo, Integer amountFen) {
        return prepayByOrderNo.computeIfAbsent(orderNo, key -> {
            MockPrepayOrder prepay = new MockPrepayOrder();
            prepay.setPrepayId(PREPAY_ID_PREFIX + UUID.randomUUID().toString().replace("-", ""));
            prepay.setOrderNo(key);
            prepay.setAmountFen(amountFen);
            prepay.setCreateTime(LocalDateTime.now());
            prepayById.put(prepay.getPrepayId(), prepay);
            return prepay;
        });
    }

    public MockPrepayOrder getByPrepayId(String prepayId) {
        return prepayId == null ? null : prepayById.get(prepayId);
    }

    public MockPrepayOrder getByOrderNo(String orderNo) {
        return orderNo == null ? null : prepayByOrderNo.get(orderNo);
    }

    /**
     * 渠道侧标记已支付（收银台受理支付成功时调用）
     *
     * @return false=预支付单不存在或已支付（重复受理幂等）
     */
    public boolean markPaid(String prepayId) {
        MockPrepayOrder prepay = getByPrepayId(prepayId);
        if (prepay == null) {
            return false;
        }
        synchronized (prepay) {
            if (prepay.getPayTime() != null) {
                return false;
            }
            prepay.setPayTime(LocalDateTime.now());
            prepay.setChannelOrderNo(CHANNEL_ORDER_NO_PREFIX + prepay.getOrderNo());
            return true;
        }
    }

    /**
     * 渠道侧预支付单
     */
    @Data
    public static class MockPrepayOrder {

        /** 渠道预支付单号（prepay 时生成，与订单一一对应） */
        private String prepayId;

        /** 商户订单号 */
        private String orderNo;

        /** 订单金额快照（分） */
        private Integer amountFen;

        /** 预支付单创建时间 */
        private LocalDateTime createTime;

        /** 渠道侧支付时间（null=未支付） */
        private volatile LocalDateTime payTime;

        /** 渠道流水号（支付后生成） */
        private volatile String channelOrderNo;
    }
}
