package com.akkkka.module.support.ai.domain.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * AI 会话
 * <p>
 * 一个学生对应多个会话；title 为会话首条提问的截断摘要，用于侧边栏展示。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Data
@TableName("ai_conversation")
public class AiConversationEntity {

    /**
     * 会话id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id（门户学生）
     */
    private Long userId;

    /**
     * 会话标题（首条提问截断）
     */
    private String title;

    /**
     * 最后一条消息时间（会话列表按此倒序）
     */
    private LocalDateTime lastMessageTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Boolean deletedFlag;
}
