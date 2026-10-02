package com.akkkka.admin.module.business.funcampus.payment.channel;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Mock 渠道回调签名器（HMAC-SHA256）
 * <p>
 * 模拟真实渠道的签名机制：
 * - 签名串：按参数名升序拼接 k=v&k=v（排除 sign 本身与空值）；
 * - 回调必须携带毫秒时间戳，超出容忍窗口视为重放攻击拒绝；
 * - 防伪造：不知道密钥无法构造出能通过验签的报文；
 * 后续接入真实渠道（微信/支付宝）时，验签逻辑同样收敛在各自 PaymentChannel 实现中
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Component
@ConditionalOnProperty(name = "pay.channel", havingValue = "mock")
public class MockPaySigner {

    /**
     * 渠道共享密钥（仅 mock 渠道使用）
     */
    @Value("${pay.mock.sign-secret:fun-campus-mock-pay-secret}")
    private String signSecret;

    /**
     * 回调时间戳容忍窗口（秒）
     */
    @Value("${pay.mock.timestamp-tolerance-seconds:300}")
    private long timestampToleranceSeconds;

    /**
     * 生成签名（HMAC-SHA256，十六进制小写）
     */
    public String sign(Map<String, String> params) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(canonicalString(params).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Mock 渠道签名计算失败", e);
        }
    }

    /**
     * 验签（含时间戳窗口校验）
     *
     * @return null=通过；否则返回失败原因
     */
    public String verify(Map<String, String> params) {
        String sign = params.get("sign");
        if (sign == null || sign.isBlank()) {
            return "签名缺失";
        }
        if (!sign.equalsIgnoreCase(sign(params))) {
            return "签名校验失败";
        }
        String timestamp = params.get("timestamp");
        if (timestamp == null || timestamp.isBlank()) {
            return "时间戳缺失";
        }
        long timestampMillis;
        try {
            timestampMillis = Long.parseLong(timestamp.trim());
        } catch (NumberFormatException e) {
            return "时间戳格式错误";
        }
        long driftMillis = Math.abs(System.currentTimeMillis() - timestampMillis);
        if (driftMillis > timestampToleranceSeconds * 1000) {
            return "时间戳超出容忍窗口（疑似重放）";
        }
        return null;
    }

    /**
     * 规范签名串：参数名升序，排除 sign 与空值
     */
    private String canonicalString(Map<String, String> params) {
        TreeMap<String, String> sorted = new TreeMap<>();
        params.forEach((key, value) -> {
            if (key != null && value != null && !"sign".equals(key) && !value.isBlank()) {
                sorted.put(key, value);
            }
        });
        StringBuilder builder = new StringBuilder();
        sorted.forEach((key, value) -> {
            if (builder.length() > 0) {
                builder.append('&');
            }
            builder.append(key).append('=').append(value);
        });
        return builder.toString();
    }
}
