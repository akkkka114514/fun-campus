package com.akkkka.admin.module.business.funcampus.payment.channel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityRefundEntity;
import com.akkkka.admin.module.business.funcampus.payment.constant.PayChannelEnum;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayNotifyResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.PayPrepayResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundChannelResult;
import com.akkkka.admin.module.business.funcampus.payment.domain.dto.RefundNotifyResult;
import com.akkkka.admin.module.business.funcampus.payment.service.PaymentNotifyHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Mock 支付渠道（仅 pay.channel=mock 时注册，生产环境不存在）
 * <p>
 * 模拟的是「渠道异步回调」链路，而不是服务端同步等待：
 * - 支付：prepay 返回模拟收银台页面链接，用户在收银台点击后经
 *   POST /portal/payment/callback/mock 触发回调，与真实渠道收敛到同一处理入口；
 * - 退款：受理成功后由调度线程延时投递退款回调（模拟渠道稍后通知），不使用 Thread.sleep
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "pay.channel", havingValue = "mock")
public class MockPaymentChannel implements PaymentChannel {

    private static final String MOCK_CASHIER_URL = "/portal/payment/mock/cashier?orderNo=";

    private final ObjectProvider<PaymentNotifyHandler> notifyHandlerProvider;

    private final TaskScheduler mockPayCallbackScheduler;

    /**
     * 模拟渠道回调延时（秒）：0 表示立即投递，模拟异步通知到达的不确定性
     */
    @Value("${pay.mock.callback-delay-seconds:0}")
    private long callbackDelaySeconds;

    public MockPaymentChannel(ObjectProvider<PaymentNotifyHandler> notifyHandlerProvider,
                              @Qualifier("mockPayCallbackScheduler") TaskScheduler mockPayCallbackScheduler) {
        this.notifyHandlerProvider = notifyHandlerProvider;
        this.mockPayCallbackScheduler = mockPayCallbackScheduler;
    }

    @Override
    public PayChannelEnum channel() {
        return PayChannelEnum.MOCK;
    }

    @Override
    public PayPrepayResult prepay(ActivityOrderEntity order) {
        PayPrepayResult result = new PayPrepayResult();
        result.setChannel(PayChannelEnum.MOCK);
        result.setCashierUrl(MOCK_CASHIER_URL + order.getOrderNo());
        return result;
    }

    @Override
    public RefundChannelResult refund(ActivityOrderEntity order, ActivityRefundEntity refund) {
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
     * 解析模拟收银台回调参数（mock 渠道不做验签）
     * 参数：orderNo、amountFen、payStatus=SUCCESS/FAIL
     */
    @Override
    public PayNotifyResult parsePayCallback(Map<String, String> params) {
        PayNotifyResult result = new PayNotifyResult();
        result.setChannel(PayChannelEnum.MOCK);
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
        if (success) {
            result.setChannelOrderNo("MOCKPAY" + params.get("orderNo"));
        } else {
            result.setFailReason("模拟收银台返回支付失败");
        }
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
