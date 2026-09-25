package com.akkkka.admin.module.business.funcampus.payment.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付渠道
 * <p>
 * channelCode 为回调 URL 中使用的渠道标识（POST /portal/payment/callback/{channel}）；
 * code 为数据库 activity_order.pay_channel 存储值
 *
 * author:akkkka114514
 * create at 2026-09-24
 */
@Getter
@AllArgsConstructor
public enum PayChannelEnum {

    WECHAT(1, "wechat", "微信"),
    ALIPAY(2, "alipay", "支付宝"),
    MOCK(3, "mock", "Mock模拟渠道");

    @EnumValue
    private final int code;

    /**
     * 渠道标识：回调 URL 路径参数与前端渠道选择使用
     */
    private final String channelCode;

    private final String label;

    public static PayChannelEnum fromChannelCode(String channelCode) {
        if (channelCode == null) {
            return null;
        }
        for (PayChannelEnum channel : values()) {
            if (channel.channelCode.equalsIgnoreCase(channelCode)) {
                return channel;
            }
        }
        return null;
    }

    public static PayChannelEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PayChannelEnum channel : values()) {
            if (channel.code == code) {
                return channel;
            }
        }
        return null;
    }
}
