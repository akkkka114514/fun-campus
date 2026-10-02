package com.akkkka.module.support.pay.channel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.akkkka.module.support.pay.constant.PayChannelEnum;
import com.akkkka.module.support.pay.domain.dto.PayNotifyResult;
import com.akkkka.module.support.pay.domain.dto.PayPrepayResult;
import com.akkkka.module.support.pay.domain.dto.PayQueryResult;
import com.akkkka.module.support.pay.domain.dto.RefundChannelResult;
import com.akkkka.module.support.pay.domain.dto.RefundNotifyResult;
import com.akkkka.module.support.pay.port.PayOrderView;
import com.akkkka.module.support.pay.port.PayRefundView;
import com.akkkka.module.support.pay.service.PaymentNotifyHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Mock 支付渠道（仅 pay.channel=mock 时注册，生产环境不存在）
 * <p>
 * 按真实渠道的行为模型仿真，而不是简单的「点按钮改状态」：
 * - 预支付单：prepay 时渠道侧生成 prepay_id 并落簿（含收款商户号，即 mch_id 语义；
 *   同订单重复 prepay 幂等返回同一预支付单）；
 * - 异步回调：用户收银台点击后由渠道受理，调度线程异步投递回调（延时/重投由 MockChannelCallbackClient 负责），
 *   与真实渠道收敛到同一处理入口 POST /portal/payment/callback/mock；
 * - 验签：回调必须携带 HMAC-SHA256 签名与时间戳（防伪造、防重放）；
 * - 渠道侧校验：渠道簿无预支付单、或预支付单未支付的「支付成功」回调一律拒绝；
 *   回调商户号与预支付单不一致同样拒绝（防串户伪造）；
 * - 查单：支持主动查单（超时关单任务关单前确认使用）；
 * - 退款：受理成功后由调度线程延时投递退款回调（模拟渠道稍后通知），不使用 Thread.sleep；
 *   退款商户号必须与原收款商户一致（渠道簿可对照时，与真实渠道「原路退回」一致）
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "pay.channel", havingValue = "mock")
public class MockPaymentChannel implements PaymentChannel {

    private static final String MOCK_CASHIER_URL = "/portal/payment/mock/cashier?prepayId=";

    /** 无预支付单时的渠道流水兜底前缀（strict-prepay-check=false 的直连回调场景） */
    private static final String CHANNEL_ORDER_NO_FALLBACK_PREFIX = "MOCKPAY";

    private final ObjectProvider<PaymentNotifyHandler> notifyHandlerProvider;

    private final MockChannelLedger channelLedger;

    private final MockPaySigner paySigner;

    private final TaskScheduler mockPayCallbackScheduler;

    /**
     * 模拟渠道回调延时（秒）：0 表示立即投递，模拟异步通知到达的不确定性
     */
    @Value("${pay.mock.callback-delay-seconds:0}")
    private long callbackDelaySeconds;

    /**
     * 严格校验预支付单：渠道簿无预支付单的「支付成功」回调直接拒绝
     */
    @Value("${pay.mock.strict-prepay-check:true}")
    private boolean strictPrepayCheck;

    public MockPaymentChannel(ObjectProvider<PaymentNotifyHandler> notifyHandlerProvider,
                              MockChannelLedger channelLedger,
                              MockPaySigner paySigner,
                              @Qualifier("mockPayCallbackScheduler") TaskScheduler mockPayCallbackScheduler) {
        this.notifyHandlerProvider = notifyHandlerProvider;
        this.channelLedger = channelLedger;
        this.paySigner = paySigner;
        this.mockPayCallbackScheduler = mockPayCallbackScheduler;
    }

    @Override
    public PayChannelEnum channel() {
        return PayChannelEnum.MOCK;
    }

    @Override
    public PayPrepayResult prepay(PayOrderView order) {
        // 渠道侧真实下单：生成（或复用）预支付单，同订单重复 prepay 返回同一 prepay_id；
        // 收款商户号（活动归属解析而来）同步落渠道簿，后续回调必须与之匹配
        MockChannelLedger.MockPrepayOrder prepay = channelLedger.createPrepay(
                order.getOrderNo(), order.getAmountFen(), order.getPayeeAccountNo());
        PayPrepayResult result = new PayPrepayResult();
        result.setChannel(PayChannelEnum.MOCK);
        result.setCashierUrl(MOCK_CASHIER_URL + prepay.getPrepayId());
        return result;
    }

    @Override
    public RefundChannelResult refund(PayOrderView order, PayRefundView refund) {
        // 原路退回约束：渠道簿存在该订单记录时，退款商户号必须与收款商户号一致（防串户退款）；
        // 渠道簿为内存存储、重启即清空，开发环境查不到记录时降级放行，避免退款被环境限制卡死
        MockChannelLedger.MockPrepayOrder prepay = channelLedger.getByOrderNo(order.getOrderNo());
        if (prepay == null) {
            log.warn("渠道簿无该订单支付记录（重启/归档），跳过原商户校验：orderNo:{}", order.getOrderNo());
        } else if (prepay.getPayeeAccountNo() != null && order.getPayeeAccountNo() != null
                && !prepay.getPayeeAccountNo().equals(order.getPayeeAccountNo())) {
            log.warn("退款商户号与收款商户号不一致，渠道拒绝原路退回：orderNo:{}，收款商户号:{}，退款商户号:{}",
                    order.getOrderNo(), prepay.getPayeeAccountNo(), order.getPayeeAccountNo());
            RefundChannelResult rejected = new RefundChannelResult();
            rejected.setAccepted(false);
            rejected.setFailReason("退款商户号与收款商户号不一致，渠道拒绝原路退回");
            return rejected;
        }
        RefundChannelResult result = new RefundChannelResult();
        result.setAccepted(true);
        result.setChannelRefundNo("MOCKRF" + refund.getRefundNo());

        // 模拟渠道异步退款通知：调度线程延时投递，不阻塞业务请求线程
        RefundNotifyResult notifyResult = new RefundNotifyResult();
        notifyResult.setChannel(PayChannelEnum.MOCK);
        notifyResult.setRefundNo(refund.getRefundNo());
        notifyResult.setChannelRefundNo(result.getChannelRefundNo());
        notifyResult.setSuccess(true);
        notifyResult.setRefundTime(LocalDateTime.now());

        Runnable deliver = () -> {
            try {
                notifyHandlerProvider.getObject().handleRefundNotify(notifyResult);
            } catch (Exception e) {
                log.error("Mock退款回调投递处理失败，refundNo:{}", refund.getRefundNo(), e);
            }
        };
        if (callbackDelaySeconds <= 0) {
            // 立即投递：仍交由调度线程执行，不阻塞业务请求线程
            mockPayCallbackScheduler.schedule(deliver, Instant.now());
        } else {
            mockPayCallbackScheduler.schedule(deliver, Instant.now().plusSeconds(callbackDelaySeconds));
        }
        log.info("Mock渠道受理退款成功，refundNo:{}，channelRefundNo:{}，延时回调:{}秒",
                refund.getRefundNo(), result.getChannelRefundNo(), callbackDelaySeconds);
        return result;
    }

    /**
     * 验签并解析渠道回调
     * 参数：orderNo、amountFen、payStatus=SUCCESS/FAIL、channelOrderNo、payeeAccountNo、timestamp、sign
     * <p>
     * 四道校验：①HMAC 验签+时间戳窗口（防伪造/防重放）；
     * ②渠道簿必须存在该订单的预支付单（未经 prepay 的订单渠道不会产生支付）；
     * ③回调商户号必须与预支付单一致（防串户）；
     * ④「支付成功」回调要求渠道簿已支付（渠道侧未支付的成功回调来源不可信）
     */
    @Override
    public PayNotifyResult parsePayCallback(Map<String, String> params) {
        PayNotifyResult result = new PayNotifyResult();
        result.setChannel(PayChannelEnum.MOCK);

        // ① 验签 + 时间戳窗口
        String verifyError = paySigner.verify(params);
        if (verifyError != null) {
            result.setVerifyFailed(true);
            result.setFailReason(verifyError);
            return result;
        }

        result.setOrderNo(params.get("orderNo"));
        boolean success = "SUCCESS".equalsIgnoreCase(params.get("payStatus"));
        result.setSuccess(success);
        try {
            String amountFen = params.get("amountFen");
            result.setAmountFen(amountFen == null ? null : Integer.valueOf(amountFen));
        } catch (NumberFormatException e) {
            result.setSuccess(false);
            result.setFailReason("金额参数格式错误");
            return result;
        }
        if (!success) {
            result.setFailReason("模拟收银台返回支付失败");
            return result;
        }

        // ② 渠道簿预支付单校验
        MockChannelLedger.MockPrepayOrder prepay = channelLedger.getByOrderNo(result.getOrderNo());
        if (prepay == null) {
            if (strictPrepayCheck) {
                result.setSuccess(false);
                result.setVerifyFailed(true);
                result.setFailReason("渠道不存在该订单的预支付单");
                return result;
            }
            log.warn("渠道簿无预支付单（strict-prepay-check=false 放行）：orderNo:{}", result.getOrderNo());
            result.setChannelOrderNo(CHANNEL_ORDER_NO_FALLBACK_PREFIX + result.getOrderNo());
            return result;
        }
        // ③ 商户号一致性校验（防串户）：回调商户号必须与预支付单一致
        String payeeAccountNo = params.get("payeeAccountNo");
        if (payeeAccountNo == null || !payeeAccountNo.equals(prepay.getPayeeAccountNo())) {
            result.setSuccess(false);
            result.setVerifyFailed(true);
            result.setFailReason("回调商户号与预支付单不一致");
            return result;
        }
        // ④ 渠道侧支付状态校验
        if (prepay.getPayTime() == null) {
            result.setSuccess(false);
            result.setVerifyFailed(true);
            result.setFailReason("渠道侧预支付单未支付，支付成功回调不可信");
            return result;
        }
        result.setChannelOrderNo(prepay.getChannelOrderNo());
        return result;
    }

    /**
     * 主动查单：以渠道簿为准返回渠道侧支付状态（超时关单任务关单前确认使用）
     */
    @Override
    public PayQueryResult queryPay(PayOrderView order) {
        PayQueryResult result = new PayQueryResult();
        result.setChannel(PayChannelEnum.MOCK);
        result.setOrderNo(order.getOrderNo());
        MockChannelLedger.MockPrepayOrder prepay = channelLedger.getByOrderNo(order.getOrderNo());
        if (prepay == null) {
            result.setPaid(false);
            result.setStatusDesc("渠道无此单");
            return result;
        }
        if (prepay.getPayTime() == null) {
            result.setPaid(false);
            result.setStatusDesc("预支付单未支付");
            return result;
        }
        result.setPaid(true);
        result.setChannelOrderNo(prepay.getChannelOrderNo());
        result.setAmountFen(prepay.getAmountFen());
        result.setPayTime(prepay.getPayTime());
        result.setStatusDesc("已支付");
        return result;
    }

    /**
     * 解析模拟退款回调参数（mock 渠道不做验签）
     * 参数：refundNo、refundStatus=SUCCESS/FAIL
     */
    @Override
    public RefundNotifyResult parseRefundCallback(Map<String, String> params) {
        RefundNotifyResult result = new RefundNotifyResult();
        result.setChannel(PayChannelEnum.MOCK);
        result.setRefundNo(params.get("refundNo"));
        boolean success = "SUCCESS".equalsIgnoreCase(params.get("refundStatus"));
        result.setSuccess(success);
        if (success) {
            result.setChannelRefundNo("MOCKRF" + params.get("refundNo"));
            result.setRefundTime(LocalDateTime.now());
        } else {
            result.setFailReason("模拟渠道退款失败");
        }
        return result;
    }
}
