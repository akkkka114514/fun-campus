package com.akkkka.module.support.pay.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收款主体类型（收款账户归属：学校/学院/组织）
 * <p>
 * 活动的收款方解析优先级：组织 → 学院 → 学校（组织最具体，兼容历史数据中两者同时存在的情况）；
 * 主体未开户时不允许收费（不能把钱收错主体）
 * <p>
 * 数据库 tinyint 映射，必须使用 @EnumValue
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Getter
@AllArgsConstructor
public enum PayeeOwnerType {

    SCHOOL(1, "学校"),
    COLLEGE(2, "学院"),
    ORGANIZATION(3, "组织");

    @EnumValue
    private final int code;

    private final String label;

    public static PayeeOwnerType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PayeeOwnerType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
