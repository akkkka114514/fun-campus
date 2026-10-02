package com.akkkka.module.support.pay.constant;

/**
 * 订单支付/退款留痕动作（跨模块语义枚举）
 * <p>
 * 业务侧 OrderLogAction 不由支付模块依赖：支付模块发出语义动作，
 * 由 PaymentBusinessPort 实现类映射回业务侧枚举后落库
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
public enum PayLogAction {

    /**
     * 渠道支付失败回调
     */
    PAY_FAILED,

    /**
     * 支付成功
     */
    PAY_SUCCESS,

    /**
     * 支付回调晚于关单（需人工退款补偿）
     */
    PAY_AFTER_CLOSE,

    /**
     * 退款到账
     */
    REFUND_SUCCESS,

    /**
     * 渠道退款失败回调
     */
    REFUND_FAILED
}
