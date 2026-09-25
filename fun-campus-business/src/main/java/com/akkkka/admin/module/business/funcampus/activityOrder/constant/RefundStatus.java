package com.akkkka.admin.module.business.funcampus.activityOrder.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 退款单状态
 *
 * author:akkkka114514
 * create at 2026-09-24
 */
@Getter
@AllArgsConstructor
public enum RefundStatus {

    REFUNDING(0, "退款中"),
    SUCCESS(1, "成功"),
    FAILED(2, "失败");

    @EnumValue
    private final int code;

    private final String label;

    public static RefundStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RefundStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
