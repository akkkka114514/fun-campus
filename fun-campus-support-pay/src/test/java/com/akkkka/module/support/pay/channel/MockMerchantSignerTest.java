package com.akkkka.module.support.pay.channel;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mock 商户请求签名器 单元测试
 * <p>
 * 覆盖：合法签名通过、参数篡改拒绝（操作名篡改防跨接口重放）、签名缺失拒绝、
 * 时间戳超窗（重放）拒绝、时间戳缺失拒绝
 *
 * @Author akkkka114514
 * @Date 2026-10-09
 */
public class MockMerchantSignerTest {

    private MockMerchantSigner signer;

    @BeforeEach
    void setUp() {
        signer = new MockMerchantSigner();
        ReflectionTestUtils.setField(signer, "merchantSignSecret", "test-merchant-secret");
        ReflectionTestUtils.setField(signer, "timestampToleranceSeconds", 300L);
    }

    private Map<String, String> params() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("operation", "prepay");
        params.put("orderNo", "AO1");
        params.put("amountFen", "100");
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return params;
    }

    @Test
    void verify_whenSignValid_passes() {
        Map<String, String> params = params();
        params.put("sign", signer.sign(params));

        assertNull(signer.verify(params));
    }

    @Test
    void verify_whenAmountTamperedAfterSign_fails() {
        Map<String, String> params = params();
        params.put("sign", signer.sign(params));
        // 签名后篡改金额（伪造请求的典型攻击面）
        params.put("amountFen", "1");

        assertEquals("签名校验失败", signer.verify(params));
    }

    @Test
    void verify_whenOperationTamperedAfterSign_fails() {
        Map<String, String> params = params();
        params.put("sign", signer.sign(params));
        // 签名后把操作名改成 refund：operation 参与签名，跨接口重放必须被拒绝
        params.put("operation", "refund");

        assertEquals("签名校验失败", signer.verify(params));
    }

    @Test
    void verify_whenSignMissing_fails() {
        assertEquals("签名缺失", signer.verify(params()));
    }

    @Test
    void verify_whenTimestampStale_fails() {
        Map<String, String> params = params();
        // 10 分钟前的时间戳：超出 300 秒容忍窗口，视为重放
        params.put("timestamp", String.valueOf(System.currentTimeMillis() - 600_000));
        params.put("sign", signer.sign(params));

        assertTrue(signer.verify(params).contains("时间戳超出容忍窗口"));
    }

    @Test
    void verify_whenTimestampMissing_fails() {
        Map<String, String> params = params();
        params.remove("timestamp");
        params.put("sign", signer.sign(params));

        assertEquals("时间戳缺失", signer.verify(params));
    }
}
