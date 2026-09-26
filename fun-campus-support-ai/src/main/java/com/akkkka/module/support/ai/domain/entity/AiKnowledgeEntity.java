package com.akkkka.module.support.ai.domain.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * AI 知识库（FAQ）
 * <p>
 * 每行一条 FAQ；embedding 为该条目 content 的向量（JSON 数组字符串），
 * 由 AiRagService 懒加载时生成并回写缓存，避免重复调用 embedding 模型。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Data
@TableName("ai_knowledge")
public class AiKnowledgeEntity {

    /**
     * 知识id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题（问题）
     */
    private String title;

    /**
     * 内容（答案）
     */
    private String content;

    /**
     * 分类
     */
    private String category;

    /**
     * 状态：1-启用 0-停用
     */
    private Integer status;

    /**
     * 向量（JSON 数组字符串，懒加载时生成回写）
     */
    private String embedding;

    /**
     * 向量模型名（模型更换后据此重建缓存）
     */
    private String embeddingModel;

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
