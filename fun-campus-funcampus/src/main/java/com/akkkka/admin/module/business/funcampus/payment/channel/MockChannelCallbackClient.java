package com.akkkka.admin.module.business.funcampus.payment.channel;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Mock 渠道回调投递器（模拟渠道服务器向平台投递异步通知）
 * <p>
 * 真实渠道的回调由渠道服务器发起（而非用户浏览器直接提交），未收到平台成功应答会按策略重投；
 * 本类用调度线程 + HTTP 自调模拟该行为，让链路与真实渠道完全对齐：
 * - 投递目标：平台回调统一入口 POST /portal/payment/callback/mock（带 HMAC 签名与时间戳，
 *   并携带渠道簿记录的收款商户号，平台侧校验防串户）；
 * - 首次投递延时 pay.mock.callback-delay-seconds；应答非 SUCCESS 时按 pay.mock.retry-delay-seconds
 *   间隔重投，总投递次数 pay.mock.deliver-times，多次失败后转人工对账（日志告警）；
 * - 每次投递重新生成时间戳与签名（与真实渠道「重试即重新签名」一致）
 * <p>
 * 仅 mock 渠道注册；生产环境不存在
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "pay.channel", havingValue = "mock")
public class MockChannelCallbackClient {

    private static final String CALLBACK_PATH = "/portal/payment/callback/mock";

    /**
     * 平台回调入口基地址（默认本机服务端口，模拟渠道服务器访问平台）
     */
    @Value("${pay.mock.platform-base-url:http://127.0.0.1:${server.port:1024}}")
    private String platformBaseUrl;

    /**
     * 渠道异步通知总投递次数（>1 时未收到成功应答会重投）
     */
    @Value("${pay.mock.deliver-times:1}")
    private int deliverTimes;

    /**
     * 重投间隔（秒）
     */
    @Value("${pay.mock.retry-delay-seconds:5}")
    private int retryDelaySeconds;

    private final MockPaySigner paySigner;

    private final TaskScheduler mockPayCallbackScheduler;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public MockChannelCallbackClient(MockPaySigner paySigner,
                                     @Qualifier("mockPayCallbackScheduler") TaskScheduler mockPayCallbackScheduler) {
        this.paySigner = paySigner;
        this.mockPayCallbackScheduler = mockPayCallbackScheduler;
    }

    /**
     * 投递支付回调
     *
     * @param orderNo         商户订单号
     * @param amountFen       金额（分）
     * @param success         支付是否成功（构造 payStatus=SUCCESS/FAIL 通知）
     * @param channelOrderNo  渠道流水号（支付成功时有值）
     * @param payeeAccountNo  收款商户号（预支付单记录，与真实渠道回调必带 mch_id 一致）
     * @param firstDelaySeconds 首次投递延时（秒）
     */
    public void deliverPayNotify(String orderNo, Integer amountFen, boolean success,
                                 String channelOrderNo, String payeeAccountNo, long firstDelaySeconds) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("orderNo", orderNo);
        params.put("amountFen", String.valueOf(amountFen));
        params.put("payStatus", success ? "SUCCESS" : "FAIL");
        if (channelOrderNo != null) {
            params.put("channelOrderNo", channelOrderNo);
        }
        if (payeeAccountNo != null) {
            params.put("payeeAccountNo", payeeAccountNo);
        }
        long delay = Math.max(firstDelaySeconds, 0);
        mockPayCallbackScheduler.schedule(() -> attemptDeliver(1, params), Instant.now().plusSeconds(delay));
        log.info("Mock渠道回调已排队投递：orderNo:{}，payStatus:{}，首次延时:{}秒，最大投递:{}次",
                orderNo, params.get("payStatus"), delay, deliverTimes);
    }

    /**
     * 执行第 attempt 次投递：应答非 SUCCESS 视为投递失败，按配置重投
     */
    private void attemptDeliver(int attempt, Map<String, String> bizParams) {
        Map<String, String> params = new LinkedHashMap<>(bizParams);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("sign", paySigner.sign(params));

        String ack = null;
        String error = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(platformBaseUrl + CALLBACK_PATH))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(toFormBody(params), StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ack = response.body();
        } catch (Exception e) {
            error = e.getMessage();
        }

        if (isAckSuccess(ack)) {
            log.info("Mock渠道回调投递成功：第{}次，orderNo:{}，应答:{}", attempt, params.get("orderNo"), ack.trim());
            return;
        }
        log.warn("Mock渠道回调投递未获成功应答：第{}次，orderNo:{}，应答:{}，异常:{}",
                attempt, params.get("orderNo"), ack, error);
        if (attempt < deliverTimes) {
            mockPayCallbackScheduler.schedule(() -> attemptDeliver(attempt + 1, bizParams),
                    Instant.now().plusSeconds(Math.max(retryDelaySeconds, 0)));
        } else {
            // 真实渠道多次重投失败后转日终对账/人工处理，这里同样保留明确告警
            log.error("Mock渠道回调投递最终失败，需对账补偿：orderNo:{}，共投递{}次", params.get("orderNo"), attempt);
        }
    }

    /**
     * 平台应答判定：平台回调入口成功受理时应答 SUCCESS，非 SUCCESS 视为失败（渠道需重投）
     */
    static boolean isAckSuccess(String ack) {
        return ack != null && ack.trim().toUpperCase().contains("SUCCESS");
    }

    /**
     * form 编码
     */
    private String toFormBody(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        params.forEach((key, value) -> {
            if (builder.length() > 0) {
                builder.append('&');
            }
            builder.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        });
        return builder.toString();
    }
}
