package com.akkkka.module.support.pay.port;

import java.time.LocalDateTime;

import com.akkkka.module.support.pay.constant.PayChannelEnum;

/**
 * 支付订单端口（支付模块 → 业务模块的能力反向接口）
 * <p>
 * 支付模块需要读写业务订单，但订单表与订单状态机归业务模块所有：
 * 业务侧提供实现类（PaymentOrderPortImpl）把订单 Manager 的能力暴露为端口方法，
 * CAS 方法保留原有"条件更新返回影响行数"语义（幂等核心），由支付模块判定竞态
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
public interface PaymentOrderPort {

    /**
     * 按订单号查询（支付回调使用）
     */
    PayOrderView getByOrderNo(String orderNo);

    /**
     * 按主键查询（退款回调反查订单、CAS 失败后读最新状态使用）
     */
    PayOrderView getById(Long orderId);

    /**
     * CAS：待支付 → 已支付（支付回调幂等的核心）
     *
     * @return 是否由本次调用完成状态流转（false=已被回调/关单先行处理）
     */
    boolean markPaidCas(Long orderId, String channelOrderNo, PayChannelEnum payChannel, LocalDateTime payTime);

    /**
     * CAS：退款中 → 已退款（退款回调成功）
     */
    boolean markRefundedCas(Long orderId);

    /**
     * CAS：退款中 → 退款失败（渠道受理失败/回调失败）
     */
    boolean markRefundFailedCas(Long orderId);
}
