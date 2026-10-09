package com.akkkka.module.support.pay.channel;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Mock 商户请求签名器（HMAC-SHA256）
 * <p>
 * 模拟真实渠道下商户侧对出站请求的签名机制（微信支付 v3 为商户私钥 SHA256withRSA 签名请求）：
 * - 平台向渠道发起 prepay / queryPay / refund 前，以商户身份对请求报文签名（含时间戳，防重放）；
 * - Mock 渠道受理前用同一密钥验签，非法请求（伪造/篡改/重放）拒绝受理；
 * - 与本类配对的渠道签名器 MockPaySigner 密钥分离：商户密钥只用于出站请求，
 *   渠道密钥只用于回调与应答，单向泄露无法伪造反方向报文
 *
 * @Author akkkka114514
 * @Date 2026-10-09
 * @Copyright akkkka114514
 */
@Component
@ConditionalOnProperty(name = "pay.channel", havingValue = "mock")
public class MockMerchantSigner {

    /**
     * 商户请求签名密钥（模拟商户私钥/API 密钥，仅 mock 渠道使用）
     */
    @Value("${pay.mock.merchant-sign-secret:fun-campus-mock-merchant-secret}")
    private String merchantSignSecret;

    /**
     * 请求时间戳容忍窗口（秒）
     */
    @Value("${pay.mock.timestamp-tolerance-seconds:300}")
    private long timestampToleranceSeconds;

    /**
     * 生成商户签名（HMAC-SHA256，十六进制小写）
     */
    public String sign(Map<String, String> params) {
        return HmacSignUtil.hmacSha256Hex(params, merchantSignSecret);
    }

    /**
     * 验签（含时间戳窗口校验）
     *
     * @return null=通过；否则返回失败原因
     */
    public String verify(Map<String, String> params) {
        return HmacSignUtil.verify(params, merchantSignSecret, timestampToleranceSeconds);
    }
}
