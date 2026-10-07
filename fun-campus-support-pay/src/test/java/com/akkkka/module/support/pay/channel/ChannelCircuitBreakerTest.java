package com.akkkka.module.support.pay.channel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * 渠道熔断器状态机单元测试
 * <p>
 * 覆盖：闭合态放行与结果透传、窗口失败率达标打开并快速失败（拒绝调用不执行实际动作）、
 * 等待窗口结束半开探测全部成功关闭、半开探测失败重新打开
 *
 * @Author akkkka114514
 * @Date 2026-10-07
 * @Copyright akkkka114514
 */
public class ChannelCircuitBreakerTest {

    /** 缩短参数的测试实例：窗口 4、最少样本 3、失败率 50%、等待 200ms、半开需成功 2 次 */
    private ChannelCircuitBreaker breaker() {
        return new ChannelCircuitBreaker("测试渠道", 4, 3, 50, 200L, 2);
    }

    private void callFail(ChannelCircuitBreaker breaker) {
        assertThrows(IllegalStateException.class, () -> breaker.call("test", () -> {
            throw new IllegalStateException("模拟渠道调用异常");
        }));
    }

    private void callOk(ChannelCircuitBreaker breaker) {
        breaker.call("test", () -> "ok");
    }

    /** 让熔断打开：2 失败 + 1 成功 = 66%，达到 50% 阈值 */
    private void openBreaker(ChannelCircuitBreaker breaker) {
        callFail(breaker);
        callFail(breaker);
        callOk(breaker);
    }

    @Test
    void closed_whenUnderThreshold_passesAndKeepsClosed() {
        ChannelCircuitBreaker breaker = breaker();
        // 窗口内 1 失败（33% < 50%）：保持闭合，调用照常放行并透传结果
        callFail(breaker);
        callOk(breaker);
        callOk(breaker);
        assertEquals("ok", breaker.call("test", () -> "ok"));
    }

    @Test
    void closed_whenFailureRateReached_opensAndFastFails() {
        ChannelCircuitBreaker breaker = breaker();
        openBreaker(breaker);

        // 打开期间快速失败：拒绝调用不执行实际动作（若执行到会抛 IllegalStateException 导致断言失败）
        assertThrows(ChannelCircuitOpenException.class, () -> breaker.call("test", () -> {
            throw new IllegalStateException("熔断打开期间不应执行渠道调用");
        }));
    }

    @Test
    void open_afterWaitHalfOpenProbesAllPass_closes() throws InterruptedException {
        ChannelCircuitBreaker breaker = breaker();
        openBreaker(breaker);
        assertThrows(ChannelCircuitOpenException.class, () -> breaker.call("test", () -> "ok"));

        // 等待窗口结束：半开放行探测，2 个探测全部成功则关闭
        Thread.sleep(250L);
        callOk(breaker);
        callOk(breaker);

        // 已关闭：恢复正常放行
        assertEquals("ok", breaker.call("test", () -> "ok"));
    }

    @Test
    void halfOpen_whenProbeFails_opensAgain() throws InterruptedException {
        ChannelCircuitBreaker breaker = breaker();
        openBreaker(breaker);

        // 等待窗口结束，半开探测失败：立即重新熔断，按新等待窗口快速失败
        Thread.sleep(250L);
        callFail(breaker);
        assertThrows(ChannelCircuitOpenException.class, () -> breaker.call("test", () -> "ok"));
    }
}
