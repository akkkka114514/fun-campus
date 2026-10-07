package com.akkkka.module.support.pay.channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.akkkka.module.support.pay.constant.PayChannelEnum;

/**
 * 渠道出站调用守卫：按渠道维度持有熔断器并统一执行受保护调用
 * <p>
 * 仅覆盖出站调用（prepay/queryPay/refund）；入站回调解析（parsePayCallback /
 * parseRefundCallback）为本地验签逻辑，不经此层，避免渠道熔断误伤回调接收。
 * 按渠道各持一个熔断器：单渠道故障不影响其他渠道（未来多渠道路由时直接复用）
 *
 * @Author akkkka114514
 * @Date 2026-10-07
 * @Copyright akkkka114514
 */
@Component
public class ChannelCallGuard {

    /** 按渠道维度各持一个熔断器 */
    private final Map<PayChannelEnum, ChannelCircuitBreaker> breakers = new ConcurrentHashMap<>();

    /**
     * 受熔断保护的渠道调用
     *
     * @param channelEnum 渠道
     * @param operation   操作名（日志用）
     * @param action      实际渠道调用
     */
    public <T> T call(PayChannelEnum channelEnum, String operation, Supplier<T> action) {
        return breakers.computeIfAbsent(channelEnum, ch -> new ChannelCircuitBreaker(ch.getLabel()))
                .call(operation, action);
    }
}
