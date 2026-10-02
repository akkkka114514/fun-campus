package com.akkkka.module.support.pay.port;

/**
 * 退款单视图（业务退款记录在支付域眼中的投影）
 * <p>
 * 依赖倒置：支付模块不直接依赖业务退款实体，业务侧实体实现本接口即可
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
public interface PayRefundView {

    /**
     * 退款单主键
     */
    Long getId();

    /**
     * 退款单号（渠道侧对账标识）
     */
    String getRefundNo();

    /**
     * 关联订单id（回调时反查订单）
     */
    Long getOrderId();
}
