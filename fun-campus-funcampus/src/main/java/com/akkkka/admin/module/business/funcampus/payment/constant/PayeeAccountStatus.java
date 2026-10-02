package com.akkkka.admin.module.business.funcampus.payment.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收款账户状态
 * <p>
 * 停用后该主体举办的付费活动一律拒绝下单（模拟渠道侧商户号被冻结/关停）
 * <p>
 * 数据库 tinyint 映射，必须使用 @EnumValue
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Getter
@AllArgsConstructor
public enum PayeeAccountStatus {

    DISABLED(0, "停用"),
    ENABLED(1, "启用");

    @EnumValue
    private final int code;

    private final String label;

    public static PayeeAccountStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PayeeAccountStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
