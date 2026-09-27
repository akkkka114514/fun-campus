package com.akkkka.admin.module.business.funcampus.activityOrder.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单操作记录类型（留痕动作）
 * <p>
 * 数据库 tinyint 映射，必须使用 @EnumValue（否则走 name-based 映射出问题）
 *
 * author:akkkka114514
 * create at 2026-09-27
 */
@Getter
@AllArgsConstructor
public enum OrderLogAction {

    CREATE(1, "创建订单"),
    PAY_SUCCESS(2, "支付成功"),
    PAY_FAILED(3, "支付失败回调"),
    PAY_AFTER_CLOSE(4, "支付回调晚于关单"),
    CLOSE(5, "关闭订单"),
    REFUND_APPLY(6, "发起退款申请"),
    REFUND_SUCCESS(7, "退款成功"),
    REFUND_FAILED(8, "退款失败"),
    REFUND_RETRY(9, "管理端重试退款");

    @EnumValue
    private final int code;

    private final String label;

    public static OrderLogAction fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderLogAction action : values()) {
            if (action.code == code) {
                return action;
            }
        }
        return null;
    }
}
