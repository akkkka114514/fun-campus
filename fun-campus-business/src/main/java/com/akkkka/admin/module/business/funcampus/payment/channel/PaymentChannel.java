package com.akkkka.admin.module.business.funcampus.payment.channel;

import java.util.Map;

import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityRefundEntity;
import com.akkkka.admin.module.business.funcampus.payment.constant.PayChannelEnum;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayNotifyResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayPrepayResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundChannelResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundNotifyResult;

/**
 * 支付渠道抽象
 * <p>
 * 后续接入真实渠道（支付宝沙箱/微信支付v3）时仅需新增实现类并注册为 Bean，
 * 业务代码零改动；开发环境使用 MockPaymentChannel 模拟完整异步回调链路
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
public interface PaymentChannel {

    /**
     * 渠道标识
     */
    PayChannelEnum channel();

    /**
     * 预下单：返回拉起支付所需参数（mock 返回模拟收银台链接）
     */
    PayPrepayResult prepay(ActivityOrderEntity order);

    /**
     * 发起退款：受理成功不代表到账，最终结果以渠道异步退款回调为准
     */
    RefundChannelResult refund(ActivityOrderEntity order, ActivityRefundEntity refund);

    /**
     * 验签并解析支付回调参数（mock 渠道不做验签）
     */
    PayNotifyResult parsePayCallback(Map<String, String> params);

    /**
     * 验签并解析退款回调参数（mock 渠道不做验签）
     */
    RefundNotifyResult parseRefundCallback(Map<String, String> params);
}
