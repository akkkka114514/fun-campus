package com.akkkka.module.support.pay.channel;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.akkkka.module.support.pay.constant.PayChannelEnum;
import com.akkkka.module.support.pay.domain.dto.PayNotifyResult;
import com.akkkka.module.support.pay.domain.dto.PayPrepayResult;
import com.akkkka.module.support.pay.domain.dto.PayQueryResult;
import com.akkkka.module.support.pay.domain.dto.RefundChannelResult;
import com.akkkka.module.support.pay.domain.dto.RefundNotifyResult;
import com.akkkka.module.support.pay.port.PayOrderView;
import com.akkkka.module.support.pay.port.PayRefundView;

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
 * - 出站签名：平台发起 prepay / queryPay / refund 时请求报文携带商户签名（模拟真实渠道下商户
 *   私钥签名请求），渠道受理前验签、非法请求拒绝受理；渠道对应答签名、商户侧验签后采信。
 *   真实场景商户签名与应答验签位于平台侧 HTTP 客户端，本类作为渠道模拟器将协议流程一并演绎；
 * - 渠道侧校验：渠道簿无预支付单、或预支付单未支付的「支付成功」回调一律拒绝；
 *   回调商户号与预支付单不一致同样拒绝（防串户伪造）；
 * - 查单：支持主动查单（超时关单任务关单前确认使用）；
 * - 退款：受理成功后由调度线程延时 HTTP 投递退款回调（与支付回调同一投递机制：带签名、
 *   未获成功应答按配置重投，模拟渠道稍后通知），不使用 Thread.sleep；
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

    private final MockChannelCallbackClient callbackClient;

    private final MockChannelLedger channelLedger;

    private final MockPaySigner paySigner;

    private final MockMerchantSigner merchantSigner;

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

    public MockPaymentChannel(MockChannelCallbackClient callbackClient,
                              MockChannelLedger channelLedger,
                              MockPaySigner paySigner,
                              MockMerchantSigner merchantSigner) {
        this.callbackClient = callbackClient;
        this.channelLedger = channelLedger;
        this.paySigner = paySigner;
        this.merchantSigner = merchantSigner;
    }

    @Override
    public PayChannelEnum channel() {
        return PayChannelEnum.MOCK;
    }

    @Override
    public PayPrepayResult prepay(PayOrderView order) {
        // 商户签名：构造请求报文并以商户密钥签名（模拟平台侧行为，真实场景签名随 HTTP 请求发往渠道）
        Map<String, String> request = merchantSignedRequest("prepay",
                fields("orderNo", order.getOrderNo(),
                        "amountFen", String.valueOf(order.getAmountFen()),
                        "payeeAccountNo", order.getPayeeAccountNo()));
        return handlePrepayRequest(order, request);
    }

    /**
     * 渠道受理预下单请求：先验商户请求签名（非法请求拒绝受理），再生成（或复用）预支付单
     * <p>
     * package-private 供测试注入伪造请求验证拒绝路径；真实场景请求来自网络，必须验签通过才进入受理
     */
    PayPrepayResult handlePrepayRequest(PayOrderView order, Map<String, String> request) {
        // 渠道验签：非法请求（伪造/篡改/重放）拒绝受理
        assertMerchantRequestValid(request);

        // 渠道受理：生成（或复用）预支付单，同订单重复 prepay 返回同一 prepay_id；
        // 收款商户号（活动归属解析而来）同步落渠道簿，后续回调必须与之匹配
        MockChannelLedger.MockPrepayOrder prepay = channelLedger.createPrepay(
                order.getOrderNo(), order.getAmountFen(), order.getPayeeAccountNo());
        PayPrepayResult result = new PayPrepayResult();
        result.setChannel(PayChannelEnum.MOCK);
        result.setCashierUrl(MOCK_CASHIER_URL + prepay.getPrepayId());

        // 应答回程：渠道对应答签名，模拟商户客户端验签（校验通过才采信应答）
        assertChannelResponseValid(channelSignedResponse("prepay",
                fields("orderNo", order.getOrderNo(), "prepayId", prepay.getPrepayId())));
        return result;
    }

    @Override
    public RefundChannelResult refund(PayOrderView order, PayRefundView refund) {
        // 商户签名：构造请求报文并以商户密钥签名（模拟平台侧行为）
        Map<String, String> request = merchantSignedRequest("refund",
                fields("orderNo", order.getOrderNo(), "refundNo", refund.getRefundNo()));
        return handleRefundRequest(order, refund, request);
    }

    /**
     * 渠道受理退款请求：先验商户请求签名（非法请求拒绝受理），再做原路退回相关校验并受理
     * <p>
     * package-private 供测试注入伪造请求验证拒绝路径
     */
    RefundChannelResult handleRefundRequest(PayOrderView order, PayRefundView refund, Map<String, String> request) {
        // 渠道验签：非法请求拒绝受理（与本类「商户号不一致拒绝受理」同一形态返回 accepted=false）
        String requestVerifyError = merchantSigner.verify(request);
        if (requestVerifyError != null) {
            log.warn("渠道拒绝受理非法退款请求（商户请求验签失败）：orderNo:{}，refundNo:{}，原因:{}",
                    order.getOrderNo(), refund.getRefundNo(), requestVerifyError);
            RefundChannelResult rejected = new RefundChannelResult();
            rejected.setAccepted(false);
            rejected.setFailReason("渠道拒绝受理：商户请求验签失败（" + requestVerifyError + "）");
            return rejected;
        }

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

        // 应答回程：渠道对应答签名，模拟商户客户端验签（校验通过才采信应答）
        assertChannelResponseValid(channelSignedResponse("refund",
                fields("refundNo", refund.getRefundNo(),
                        "accepted", String.valueOf(result.isAccepted()),
                        "channelRefundNo", result.getChannelRefundNo())));

        // 模拟渠道异步退款通知：调度线程延时 HTTP 投递（带签名 + 未获成功应答按配置重投），
        // 与支付回调同一机制；不阻塞业务请求线程
        callbackClient.deliverRefundNotify(refund.getRefundNo(), true, result.getChannelRefundNo(), callbackDelaySeconds);
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
        // 商户签名：构造请求报文并以商户密钥签名（模拟平台侧行为）
        Map<String, String> request = merchantSignedRequest("queryPay", fields("orderNo", order.getOrderNo()));
        return handleQueryRequest(order, request);
    }

    /**
     * 渠道受理查单请求：先验商户请求签名（非法请求拒绝受理），再以渠道簿为准返回渠道侧支付状态
     * <p>
     * package-private 供测试注入伪造请求验证拒绝路径
     */
    PayQueryResult handleQueryRequest(PayOrderView order, Map<String, String> request) {
        // 渠道验签：非法请求拒绝受理（拒绝形态为异常而非 paid=false——验签失败绝不能
        // 被关单任务误读为「渠道未支付」而放行关单）
        assertMerchantRequestValid(request);

        // 渠道受理：以渠道簿为准返回渠道侧支付状态
        PayQueryResult result = new PayQueryResult();
        result.setChannel(PayChannelEnum.MOCK);
        result.setOrderNo(order.getOrderNo());
        MockChannelLedger.MockPrepayOrder prepay = channelLedger.getByOrderNo(order.getOrderNo());
        if (prepay == null) {
            result.setPaid(false);
            result.setStatusDesc("渠道无此单");
        } else if (prepay.getPayTime() == null) {
            result.setPaid(false);
            result.setStatusDesc("预支付单未支付");
        } else {
            result.setPaid(true);
            result.setChannelOrderNo(prepay.getChannelOrderNo());
            result.setAmountFen(prepay.getAmountFen());
            result.setPayTime(prepay.getPayTime());
            result.setStatusDesc("已支付");
        }

        // 应答回程：渠道对应答签名，模拟商户客户端验签（校验通过才采信应答）
        assertChannelResponseValid(channelSignedResponse("queryPay",
                fields("orderNo", result.getOrderNo(),
                        "paid", String.valueOf(result.isPaid()),
                        "amountFen", result.getAmountFen() == null ? null : String.valueOf(result.getAmountFen()),
                        "statusDesc", result.getStatusDesc())));
        return result;
    }

    /**
     * 验签并解析退款回调
     * 参数：refundNo、refundStatus=SUCCESS/FAIL、channelRefundNo、timestamp、sign
     * <p>
     * 与支付回调同一套防护：HMAC 验签 + 时间戳窗口（防伪造/防重放）
     */
    @Override
    public RefundNotifyResult parseRefundCallback(Map<String, String> params) {
        RefundNotifyResult result = new RefundNotifyResult();
        result.setChannel(PayChannelEnum.MOCK);

        // ① 验签 + 时间戳窗口
        String verifyError = paySigner.verify(params);
        if (verifyError != null) {
            result.setVerifyFailed(true);
            result.setFailReason(verifyError);
            return result;
        }

        // ② 报文完整性：退款回调必须携带退款单号
        String refundNo = params.get("refundNo");
        if (refundNo == null || refundNo.isBlank()) {
            result.setVerifyFailed(true);
            result.setFailReason("退款回调缺少退款单号");
            return result;
        }
        result.setRefundNo(refundNo);

        boolean success = "SUCCESS".equalsIgnoreCase(params.get("refundStatus"));
        result.setSuccess(success);
        if (success) {
            String channelRefundNo = params.get("channelRefundNo");
            result.setChannelRefundNo(channelRefundNo == null || channelRefundNo.isBlank()
                    ? "MOCKRF" + refundNo
                    : channelRefundNo);
            result.setRefundTime(LocalDateTime.now());
        } else {
            result.setFailReason("模拟渠道退款失败");
        }
        return result;
    }

    // ---------------------------------- 出站签名协议（商户签名 ↔ 渠道验签 / 渠道签名 ↔ 商户验签） ----------------------------------

    /**
     * 商户签名请求报文（模拟平台侧行为）：附加操作名与时间戳后以商户密钥签名
     * <p>
     * 附加 operation 字段使签名与具体接口绑定，防止把 prepay 的合法请求重放到 refund 等接口
     */
    private Map<String, String> merchantSignedRequest(String operation, Map<String, String> fields) {
        Map<String, String> request = new LinkedHashMap<>(fields);
        request.put("operation", operation);
        request.put("timestamp", String.valueOf(System.currentTimeMillis()));
        request.put("sign", merchantSigner.sign(request));
        return request;
    }

    /**
     * 渠道验商户请求签名（渠道侧行为）：非法请求拒绝受理
     * <p>
     * 预下单/查单无「拒绝受理」承载字段，以异常表达渠道受理失败（真实渠道返回错误应答）；
     * 退款接口以 RefundChannelResult.accepted=false 表达（见 handleRefundRequest）
     */
    private void assertMerchantRequestValid(Map<String, String> request) {
        String verifyError = merchantSigner.verify(request);
        if (verifyError != null) {
            throw new IllegalStateException("渠道拒绝受理：商户请求验签失败（" + verifyError + "）");
        }
    }

    /**
     * 渠道签名应答报文（渠道侧行为）：附加操作名与时间戳后以渠道密钥签名
     */
    private Map<String, String> channelSignedResponse(String operation, Map<String, String> fields) {
        Map<String, String> response = new LinkedHashMap<>(fields);
        response.put("operation", operation);
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("sign", paySigner.sign(response));
        return response;
    }

    /**
     * 商户验渠道应答签名（模拟平台客户端行为）：校验通过才采信应答，防应答在回程被篡改
     */
    private void assertChannelResponseValid(Map<String, String> response) {
        String verifyError = paySigner.verify(response);
        if (verifyError != null) {
            throw new IllegalStateException("渠道应答验签失败：" + verifyError);
        }
    }

    /**
     * 构造签名字段（键值对可变参数，跳过空值）
     */
    private static Map<String, String> fields(String... keyValues) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            if (keyValues[i + 1] != null) {
                fields.put(keyValues[i], keyValues[i + 1]);
            }
        }
        return fields;
    }
}
