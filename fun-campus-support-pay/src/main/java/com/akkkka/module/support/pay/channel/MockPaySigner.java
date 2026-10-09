package com.akkkka.module.support.pay.channel;

import java.util.Map;

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
        return HmacSignUtil.hmacSha256Hex(params, signSecret);
    }

    /**
     * 验签（含时间戳窗口校验）
     *
     * @return null=通过；否则返回失败原因
     */
    public String verify(Map<String, String> params) {
        return HmacSignUtil.verify(params, signSecret, timestampToleranceSeconds);
    }
}
