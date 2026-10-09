package com.akkkka.module.support.pay.mocktest;

import com.akkkka.module.support.pay.port.PayRefundView;

import lombok.Data;

/**
 * 测试用退款单视图桩（模拟业务实体实现 PayRefundView 后的行为）
 * <p>
 * 本副本服务 pay-mock 模块测试（模块拆分后不依赖核心模块的测试类）
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
