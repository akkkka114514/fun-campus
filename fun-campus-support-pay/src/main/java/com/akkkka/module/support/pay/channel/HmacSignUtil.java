package com.akkkka.module.support.pay.channel;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 渠道签名公共算法（HMAC-SHA256）
 * <p>
 * 收敛签名串规范与摘要计算，供两个签名器共用：
 * - 渠道签名器 MockPaySigner（渠道回调、渠道应答）；
 * - 商户签名器 MockMerchantSigner（平台出站请求）；
 * - 签名串：参数名升序拼接 k=v&k=v（排除 sign 本身与空值）；摘要：HMAC-SHA256 十六进制小写
 *
 * @Author akkkka114514
 * @Date 2026-10-09
 * @Copyright akkkka114514
 */
final class HmacSignUtil {

    private HmacSignUtil() {
    }

    /**
     * 规范签名串：参数名升序，排除 sign 与空值
     */
    static String canonicalString(Map<String, String> params) {
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

    /**
     * 按规范签名串计算 HMAC-SHA256（十六进制小写）
     */
    static String hmacSha256Hex(Map<String, String> params, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(canonicalString(params).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("HMAC-SHA256 签名计算失败", e);
        }
    }

    /**
     * 通用验签（含时间戳窗口校验）
     *
     * @param params           待验签参数（含 sign 与 timestamp）
     * @param secret           密钥
     * @param toleranceSeconds 时间戳容忍窗口（秒）
     * @return null=通过；否则返回失败原因
     */
    static String verify(Map<String, String> params, String secret, long toleranceSeconds) {
        String sign = params.get("sign");
        if (sign == null || sign.isBlank()) {
            return "签名缺失";
        }
        if (!sign.equalsIgnoreCase(hmacSha256Hex(params, secret))) {
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
        if (driftMillis > toleranceSeconds * 1000) {
            return "时间戳超出容忍窗口（疑似重放）";
        }
        return null;
    }
}
