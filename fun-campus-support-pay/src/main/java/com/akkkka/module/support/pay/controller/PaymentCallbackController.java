package com.akkkka.module.support.pay.controller;

import java.util.Map;

import com.akkkka.module.support.pay.channel.PaymentChannel;
import com.akkkka.module.support.pay.constant.PayChannelEnum;
import com.akkkka.module.support.pay.domain.dto.PayNotifyResult;
import com.akkkka.module.support.pay.domain.dto.RefundNotifyResult;
import com.akkkka.module.support.pay.service.PaymentService;
import com.akkkka.common.annoation.NoNeedLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付回调 Controller（渠道异步通知统一入口）
 * <p>
 * - mock 渠道与真实渠道收敛到同一入口：POST /portal/payment/callback/{channel}；
 *   渠道报文解析与验签由各自 PaymentChannel 实现完成；
 * - 验签失败（防伪造/防重放/渠道无有效支付）直接应答 FAIL，不进入业务处理；
 * - 回调由渠道服务器直接访问，无需登录（@NoNeedLogin）；
 * - 处理逻辑全部幂等（订单/退款单 CAS），应答 SUCCESS 避免渠道重复通知
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@Slf4j
@RestController
@Tag(name = "支付回调")
@RequestMapping("portal/payment")
public class PaymentCallbackController {

    @Resource
    private PaymentService paymentService;

    @Operation(summary = "支付回调统一入口 @author akkkka114514")
    @PostMapping("/callback/{channel}")
    @NoNeedLogin
    public String payCallback(@PathVariable String channel, @RequestParam Map<String, String> params) {
        PayChannelEnum channelEnum = PayChannelEnum.fromChannelCode(channel);
        if (channelEnum == null) {
            return "FAIL: unknown channel";
        }
        PaymentChannel paymentChannel = paymentService.getChannel(channelEnum);
        PayNotifyResult notifyResult = paymentChannel.parsePayCallback(params);
        if (notifyResult.isVerifyFailed()) {
            // 验签失败/时间戳超窗/渠道侧无有效支付：拒绝入账，应答非 SUCCESS（渠道会按策略重投/转对账）
            log.warn("支付回调被拒绝：channel:{}，原因:{}，params:{}", channel, notifyResult.getFailReason(), params);
            return "FAIL";
        }
        paymentService.handlePayNotify(notifyResult);
        // 已受理（内部幂等，重复通知无害），统一应答 SUCCESS
        return "SUCCESS";
    }

    @Operation(summary = "退款回调统一入口 @author akkkka114514")
    @PostMapping("/callback/{channel}/refund")
    @NoNeedLogin
    public String refundCallback(@PathVariable String channel, @RequestParam Map<String, String> params) {
        PayChannelEnum channelEnum = PayChannelEnum.fromChannelCode(channel);
        if (channelEnum == null) {
            return "FAIL: unknown channel";
        }
        PaymentChannel paymentChannel = paymentService.getChannel(channelEnum);
        RefundNotifyResult notifyResult = paymentChannel.parseRefundCallback(params);
        if (notifyResult.isVerifyFailed()) {
            // 验签失败/时间戳超窗/报文缺单号：拒绝处理，应答非 SUCCESS（渠道会按策略重投/转对账）
            log.warn("退款回调被拒绝：channel:{}，原因:{}，params:{}", channel, notifyResult.getFailReason(), params);
            return "FAIL";
        }
        paymentService.handleRefundNotify(notifyResult);
        return "SUCCESS";
    }
}
