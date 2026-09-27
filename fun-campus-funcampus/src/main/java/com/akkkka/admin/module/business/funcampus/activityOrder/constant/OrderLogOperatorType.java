package com.akkkka.admin.module.business.funcampus.activityOrder.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单操作记录的操作人类型
 *
 * author:akkkka114514
 * create at 2026-09-27
 */
@Getter
@AllArgsConstructor
public enum OrderLogOperatorType {

    PORTAL_USER(1, "门户用户"),
    ADMIN_USER(2, "管理端用户"),
    SYSTEM(3, "系统");

    @EnumValue
    private final int code;

    private final String label;

    public static OrderLogOperatorType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderLogOperatorType operatorType : values()) {
            if (operatorType.code == code) {
                return operatorType;
            }
        }
        return null;
    }
}
