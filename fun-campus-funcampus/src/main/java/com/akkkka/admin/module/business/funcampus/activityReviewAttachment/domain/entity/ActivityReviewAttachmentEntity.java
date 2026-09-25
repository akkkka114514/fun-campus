package com.akkkka.admin.module.business.funcampus.activityReviewAttachment.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2026-04-06 14:12
 */
@Data
@TableName("activity_review_attachment")
public class ActivityReviewAttachmentEntity {
    @TableId
    private Long id;
    private Long reviewLogId;
    private String fileKey;
    private Boolean deletedFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
