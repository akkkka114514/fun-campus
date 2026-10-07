package com.akkkka.module.support.pay.channel;

import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

/**
 * 渠道熔断器（按渠道维度各持一个，进程内轻量实现）
 * <p>
 * 三态流转：CLOSED --最近 N 次调用失败率超阈值--> OPEN --等待窗口结束--> HALF_OPEN
 * --探测成功够数--> CLOSED / --任一探测失败--> OPEN；
 * OPEN 期间调用直接抛 ChannelCircuitOpenException 快速失败，不再让请求线程干等渠道超时，
 * 等待窗口结束仅放行少量探测请求，避免恢复瞬间全量流量再次打挂渠道
 * <p>
 * 失败判断是"异常即失败"：渠道调用超时、连接拒绝等异常均计入窗口，
 * 若调用方有"业务失败但不抛异常"的返回约定需自行在外层转换
 *
 * @Author akkkka114514
 * @Date 2026-10-07
 * @Copyright akkkka114514
 */
@Slf4j
public class ChannelCircuitBreaker {

    /** 滑动窗口大小：评估最近 N 次调用结果 */
    private static final int DEFAULT_WINDOW_SIZE = 10;

    /** 窗口内样本达到该数量才评估失败率（防 1/1=100% 直接熔断） */
    private static final int DEFAULT_MIN_SAMPLES = 5;

    /** 失败率阈值（%），达到即打开 */
    private static final int DEFAULT_FAILURE_RATE_THRESHOLD = 50;

    /** 打开态等待时长（毫秒），到期放行少量探测请求 */
    private static final long DEFAULT_OPEN_WAIT_MILLIS = 30_000L;

    /** 半开态关闭所需的成功探测数 */
    private static final int DEFAULT_HALF_OPEN_SUCCESS_NEEDED = 3;

    private final String channelName;

    private final int windowSize;

    private final int minSamples;

    private final int failureRateThreshold;

    private final long openWaitMillis;

    private final int halfOpenSuccessNeeded;

    /** 滑动窗口：环形数组记录最近 N 次调用结果（true=成功） */
    private final boolean[] window;

    private int windowIndex = 0;

    private int windowCount = 0;

    private volatile State state = State.CLOSED;

    private long openTimestamp = 0L;

    /** 半开态已放行的探测请求数（限制并发探测，关闭/重新熔断后重置） */
    private int halfOpenInFlight = 0;

    private int halfOpenSuccess = 0;

    public ChannelCircuitBreaker(String channelName) {
        this(channelName, DEFAULT_WINDOW_SIZE, DEFAULT_MIN_SAMPLES, DEFAULT_FAILURE_RATE_THRESHOLD,
                DEFAULT_OPEN_WAIT_MILLIS, DEFAULT_HALF_OPEN_SUCCESS_NEEDED);
    }

    public ChannelCircuitBreaker(String channelName, int windowSize, int minSamples, int failureRateThreshold,
                                 long openWaitMillis, int halfOpenSuccessNeeded) {
        this.channelName = channelName;
        this.windowSize = windowSize;
        this.minSamples = minSamples;
        this.failureRateThreshold = failureRateThreshold;
        this.openWaitMillis = openWaitMillis;
        this.halfOpenSuccessNeeded = halfOpenSuccessNeeded;
        this.window = new boolean[windowSize];
    }

    /**
     * 受保护执行：熔断打开时快速失败，否则放行并记录结果
     * <p>
     * 方法级 synchronized：调用频率低（单笔订单只触发一次渠道调用），
     * 保证状态机流转与窗口统计的原子性，无性能压力
     *
     * @param operation 操作名（日志用，如 prepay/queryPay/refund）
     * @param action    实际渠道调用
     */
    public synchronized <T> T call(String operation, Supplier<T> action) {
        enterCall(operation);
        boolean success = false;
        try {
            T result = action.get();
            success = true;
            return result;
        } finally {
            recordResult(success);
        }
    }

    /**
     * 判断是否放行：打开态等待窗口结束后转半开放行探测；
     * 拒绝时抛 ChannelCircuitOpenException（被拒绝的请求不计入窗口样本）
     */
    private void enterCall(String operation) {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - openTimestamp < openWaitMillis) {
                throw new ChannelCircuitOpenException(channelName + "渠道熔断中，拒绝调用：" + operation);
            }
            // 等待窗口结束：进入半开，重置探测计数，放行少量探测请求
            state = State.HALF_OPEN;
            halfOpenInFlight = 0;
            halfOpenSuccess = 0;
        }
        if (state == State.HALF_OPEN) {
            if (halfOpenInFlight >= halfOpenSuccessNeeded) {
                throw new ChannelCircuitOpenException(channelName + "渠道半开探测中，拒绝调用：" + operation);
            }
            halfOpenInFlight++;
        }
    }

    /**
     * 记录调用结果：半开态探测全成功则关闭（重置窗口），任一失败立即重新熔断；
     * 闭合态结果写入滑动窗口，失败率达标则打开
     */
    private void recordResult(boolean success) {
        if (state == State.HALF_OPEN) {
            if (success && ++halfOpenSuccess >= halfOpenSuccessNeeded) {
                resetWindow();
                state = State.CLOSED;
                log.info("渠道[{}]半开探测全部通过，熔断关闭", channelName);
            } else if (!success) {
                openTimestamp = System.currentTimeMillis();
                state = State.OPEN;
                log.error("渠道[{}]半开探测失败，重新熔断{}ms", channelName, openWaitMillis);
            }
            return;
        }
        // 已在途探测返回时状态可能已被其他探测改为 OPEN：不再统计，等待下轮半开
        if (state != State.CLOSED) {
            return;
        }
        window[windowIndex] = success;
        windowIndex = (windowIndex + 1) % windowSize;
        if (windowCount < windowSize) {
            windowCount++;
        }
        if (windowCount < minSamples) {
            return;
        }
        int failures = 0;
        for (int i = 0; i < windowCount; i++) {
            if (!window[i]) {
                failures++;
            }
        }
        int failureRate = failures * 100 / windowCount;
        if (failureRate >= failureRateThreshold) {
            openTimestamp = System.currentTimeMillis();
            state = State.OPEN;
            log.error("渠道[{}]最近{}次调用失败率{}%达到阈值，熔断打开{}ms",
                    channelName, windowCount, failureRate, openWaitMillis);
        }
    }

    private void resetWindow() {
        windowIndex = 0;
        windowCount = 0;
        halfOpenInFlight = 0;
        halfOpenSuccess = 0;
    }

    private enum State { CLOSED, OPEN, HALF_OPEN }
}
