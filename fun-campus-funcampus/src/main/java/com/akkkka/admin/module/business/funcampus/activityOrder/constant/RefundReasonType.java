package com.akkkka.admin.module.business.funcampus.activityOrder.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 退款原因类型
 *
 * author:akkkka114514
 * create at 2026-09-24
 */
@Getter
@AllArgsConstructor
public enum RefundReasonType {

    USER_APPLY(1, "用户申请"),
    ACTIVITY_CANCEL(2, "活动取消"),
    ENROLL_REVIEW_REJECT(3, "报名审核未通过"),
    OTHER(4, "其它");

    @EnumValue
    private final int code;

    private final String label;

    public static RefundReasonType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RefundReasonType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
