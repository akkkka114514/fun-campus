package com.akkkka.module.support.pay.port;

import lombok.Data;

/**
 * 测试用退款单视图桩（模拟业务实体实现 PayRefundView 后的行为）
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 */
@Data
public class StubRefundView implements PayRefundView {

    private Long id;

    private String refundNo;

    private Long orderId;
}
