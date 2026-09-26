package com.akkkka.module.support.ai.domain.entity;

import java.time.LocalDateTime;

import com.akkkka.module.support.ai.constant.AiMessageRoleEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * AI 消息
 * <p>
 * 记录会话内每条用户提问与助手回复；role 通过 {@link AiMessageRoleEnum}
 * 的 @EnumValue 映射为 tinyint 存储值。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Data
@TableName("ai_message")
public class AiMessageEntity {

    /**
     * 消息id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话id
     */
    private Long conversationId;

    /**
     * 用户id（门户学生）
     */
    private Long userId;

    /**
     * 消息角色：1-用户 2-助手
     */
    private AiMessageRoleEnum role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 是否删除
     */
    private Boolean deletedFlag;
}
