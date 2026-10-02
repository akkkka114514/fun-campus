package com.akkkka.module.support.pay.port;

import java.time.LocalDateTime;

/**
 * 退款单端口（支付模块 → 业务模块的能力反向接口）
 * <p>
 * 退款单表与退款状态机归业务模块所有；CAS 方法保留原有条件更新语义，
 * 重复回调时靠影响行数为 0 实现幂等
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
public interface PaymentRefundPort {

    /**
     * 按退款单号查询（退款回调使用）
     */
    PayRefundView getByRefundNo(String refundNo);

    /**
     * CAS：退款中 → 成功（退款成功回调）
     *
     * @return 是否由本次调用完成状态流转（false=重复回调，幂等返回）
     */
    boolean markRefundSuccessCas(Long refundId, String channelRefundNo, LocalDateTime refundTime);

    /**
     * CAS：退款中 → 失败（渠道受理失败/回调失败）
     */
    boolean markRefundFailedCas(Long refundId);
}
