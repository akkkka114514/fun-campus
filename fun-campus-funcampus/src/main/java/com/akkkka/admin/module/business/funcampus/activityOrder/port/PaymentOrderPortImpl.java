package com.akkkka.admin.module.business.funcampus.activityOrder.port;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.module.support.pay.constant.PayChannelEnum;
import com.akkkka.module.support.pay.port.PayOrderView;
import com.akkkka.module.support.pay.port.PaymentOrderPort;

import lombok.AllArgsConstructor;

/**
 * 支付订单端口实现（支付模块 → 订单 Manager 的桥）
 * <p>
 * 订单表与订单状态机归业务模块所有：本类把订单 Manager 的查询与 CAS 能力暴露给支付模块；
 * 返回的实体实现了 PayOrderView，支付模块只消费视图接口与语义判断，
 * 不依赖 ActivityOrderEntity / OrderStatus 等业务类型
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Component
@AllArgsConstructor
public class PaymentOrderPortImpl implements PaymentOrderPort {

    private final ActivityOrderManager orderManager;

    @Override
    public PayOrderView getByOrderNo(String orderNo) {
        return orderManager.getByOrderNo(orderNo);
    }

    @Override
    public PayOrderView getById(Long orderId) {
        return orderManager.getById(orderId);
    }

    @Override
    public boolean markPaidCas(Long orderId, String channelOrderNo, PayChannelEnum payChannel, LocalDateTime payTime) {
        return orderManager.markPaidCas(orderId, channelOrderNo, payChannel, payTime);
    }

    @Override
    public boolean markRefundedCas(Long orderId) {
        return orderManager.markRefundedCas(orderId);
    }

    @Override
    public boolean markRefundFailedCas(Long orderId) {
        return orderManager.markRefundFailedCas(orderId);
    }
}
