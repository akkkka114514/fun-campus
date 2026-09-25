package com.akkkka.admin.module.business.funcampus.activityAttachment.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2026-04-05 20:57
 */
@Data
@TableName("activity_attachment")
public class ActivityAttachmentEntity {
    @TableId
    private Long id;
    private Long activityId;
    private String fileKey;
    private Boolean deletedFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
