package com.akkkka.admin.module.business.funcampus.activityComment.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 活动评论热度 实体类
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */

@Data
@TableName("activity_comment_hot")
public class ActivityCommentHotEntity {

    /**
     * 评论id
     */
    @TableId(type = IdType.AUTO)
    private Long commentId;

    /**
     * 评论热度值
     */
    private Long commentHot;

}
