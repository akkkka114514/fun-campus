package com.akkkka.module.support.pay.channel;

import java.util.Map;

import com.akkkka.module.support.pay.constant.PayChannelEnum;
import com.akkkka.module.support.pay.domain.dto.PayNotifyResult;
import com.akkkka.module.support.pay.domain.dto.PayPrepayResult;
import com.akkkka.module.support.pay.domain.dto.PayQueryResult;
import com.akkkka.module.support.pay.domain.dto.RefundChannelResult;
import com.akkkka.module.support.pay.domain.dto.RefundNotifyResult;
import com.akkkka.module.support.pay.port.PayOrderView;
import com.akkkka.module.support.pay.port.PayRefundView;

/**
 * 支付渠道抽象
 * <p>
 * 后续接入真实渠道（支付宝沙箱/微信支付v3）时仅需新增实现类并注册为 Bean，
 * 业务代码零改动；开发环境使用 MockPaymentChannel 模拟完整异步回调链路
 * <p>
 * 订单/退款单参数用 PayOrderView / PayRefundView 端口视图，
 * 业务实体实现接口后可直接传入（依赖倒置，渠道侧不依赖业务实体类）
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
    PayPrepayResult prepay(PayOrderView order);

    /**
     * 发起退款：受理成功不代表到账，最终结果以渠道异步退款回调为准
     */
    RefundChannelResult refund(PayOrderView order, PayRefundView refund);

    /**
     * 主动查单：向渠道查询订单真实支付结果
     * <p>
     * 超时关单任务在关单前调用（回调可能丢失）：
     * 渠道已支付 → 走回调同一入账路径补偿；确认未支付 → 才允许关单，
     * 避免"资金已入账但订单被关闭"的不一致
     */
    PayQueryResult queryPay(PayOrderView order);

    /**
     * 验签并解析支付回调参数；验签失败（或渠道侧校验不通过）时置 verifyFailed=true，
     * 由回调入口直接拒绝，不进入业务处理
     */
    PayNotifyResult parsePayCallback(Map<String, String> params);

    /**
     * 验签并解析退款回调参数；验签失败（或报文缺关键字段）时置 verifyFailed=true，
     * 由回调入口直接拒绝，不进入业务处理
     */
    RefundNotifyResult parseRefundCallback(Map<String, String> params);
}
