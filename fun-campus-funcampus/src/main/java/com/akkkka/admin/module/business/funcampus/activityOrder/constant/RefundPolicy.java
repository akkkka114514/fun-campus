package com.akkkka.admin.module.business.funcampus.activityOrder.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动退款规则
 * <p>
 * 配置在 activity.refund_policy，控制用户主动退款申请的时间窗口；
 * 系统退款（报名审核未通过、活动取消）不受此限制
 *
 * author:akkkka114514
 * create at 2026-09-24
 */
@Getter
@AllArgsConstructor
public enum RefundPolicy {

    BEFORE_ENROLL_END(1, "报名截止前可退"),
    BEFORE_ACTIVITY_START(2, "活动开始前可退"),
    NOT_REFUNDABLE(3, "不可退款");

    @EnumValue
    private final int code;

    private final String label;

    public static RefundPolicy fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RefundPolicy policy : values()) {
            if (policy.code == code) {
                return policy;
            }
        }
        return null;
    }
}
