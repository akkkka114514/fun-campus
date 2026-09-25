package com.akkkka.admin.module.business.funcampus.payment.service;

import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayNotifyResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundNotifyResult;

/**
 * 支付/退款回调统一处理入口
 * <p>
 * 各渠道回调解析（验签）后收敛到这里处理：CAS 幂等 → 写报名记录 / 释放名额；
 * 抽成接口是为了让渠道实现类（如 MockPaymentChannel 模拟异步通知）可以反向投递回调，
 * 而不与 PaymentService 形成构造依赖环
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
public interface PaymentNotifyHandler {

    /**
     * 处理支付回调
     */
    void handlePayNotify(PayNotifyResult notifyResult);

    /**
     * 处理退款回调
     */
    void handleRefundNotify(RefundNotifyResult notifyResult);
}
