package com.akkkka.admin.module.business.funcampus.activityOrder.port;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityRefundManager;
import com.akkkka.module.support.pay.port.PayRefundView;
import com.akkkka.module.support.pay.port.PaymentRefundPort;

import lombok.AllArgsConstructor;

/**
 * 退款单端口实现（支付模块 → 退款 Manager 的桥）
 * <p>
 * 退款单表与退款状态机归业务模块所有；CAS 方法保留「条件更新返回影响行数」语义，
 * 支付模块据此判定重复回调并保证幂等
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Component
@AllArgsConstructor
public class PaymentRefundPortImpl implements PaymentRefundPort {

    private final ActivityRefundManager refundManager;

    @Override
    public PayRefundView getByRefundNo(String refundNo) {
        return refundManager.getByRefundNo(refundNo);
    }

    @Override
    public boolean markRefundSuccessCas(Long refundId, String channelRefundNo, LocalDateTime refundTime) {
        return refundManager.markSuccessCas(refundId, channelRefundNo, refundTime);
    }

    @Override
    public boolean markRefundFailedCas(Long refundId) {
        return refundManager.markFailedCas(refundId);
    }
}
