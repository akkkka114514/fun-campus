package com.akkkka.admin.module.business.funcampus.tribe.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 部落加入申请审核状态
 * <p>
 * 流转路径：待审核(0) → 通过(1) / 驳回(2)，终态不可再次流转
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Getter
@AllArgsConstructor
public enum TribeApplicationStatus {

    WAIT_REVIEW(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已驳回");

    @EnumValue
    private final int code;

    private final String label;

    public static TribeApplicationStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TribeApplicationStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
