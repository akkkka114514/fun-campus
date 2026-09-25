package com.akkkka.admin.module.business.funcampus.activityComment.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动评论 实体类
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */

@Data
@TableName("activity_comment")
public class ActivityCommentEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评论用户id
     */
    private Long userId;

    /**
     * 被评论的活动id
     */
    private Long activityId;

    /**
     * 被回复的评论id，null即为最顶层评论
     */
    private Long toCommentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 根评论id，为null即为顶层评论
     */
    private Long rootId;

    /**
     * 是否已删除
     */
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
