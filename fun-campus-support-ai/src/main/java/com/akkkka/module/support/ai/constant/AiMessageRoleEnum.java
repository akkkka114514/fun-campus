package com.akkkka.module.support.ai.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 消息角色
 * <p>
 * code 与表 ai_message.role 的存储值一一对应：
 * 1-用户提问；2-AI 助手回复。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Getter
@AllArgsConstructor
public enum AiMessageRoleEnum {

    USER(1, "用户"),
    ASSISTANT(2, "助手");

    /**
     * 数据库存储值（tinyint）
     */
    @EnumValue
    private final int code;

    /**
     * 展示名称
     */
    private final String label;

    /**
     * 按 code 反查枚举；无匹配返回 null
     */
    public static AiMessageRoleEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AiMessageRoleEnum role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        return null;
    }
}
