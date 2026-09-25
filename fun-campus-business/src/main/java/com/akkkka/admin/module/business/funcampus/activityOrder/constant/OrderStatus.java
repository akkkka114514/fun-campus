package com.akkkka.admin.module.business.funcampus.activityOrder.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 报名订单状态
 * <p>
 * 流转路径：
 * 待支付(0) → 超时/取消 → 已关闭(2)；
 * 待支付(0) → 支付成功 → 已支付(1) → 申请退款 → 退款中(3) → 已退款(4)；
 * 退款中(3) → 渠道失败 → 退款失败(5)（支持重新申请）
 * <p>
 * 数据库 tinyint 映射，必须使用 @EnumValue（否则走 name-based 映射出问题）
 *
 * author:akkkka114514
 * create at 2026-09-24
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {

    WAIT_PAY(0, "待支付"),
    PAID(1, "已支付"),
    CLOSED(2, "已关闭"),
    REFUNDING(3, "退款中"),
    REFUNDED(4, "已退款"),
    REFUND_FAILED(5, "退款失败");

    @EnumValue
    private final int code;

    private final String label;

    public static OrderStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
