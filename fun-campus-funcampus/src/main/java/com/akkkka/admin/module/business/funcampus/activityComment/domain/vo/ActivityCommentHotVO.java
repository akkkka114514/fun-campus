package com.akkkka.admin.module.business.funcampus.activityComment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 热门评论 VO
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */
@Data
public class ActivityCommentHotVO {

    @Schema(description = "评论id")
    private Long commentId;

    @Schema(description = "评论热度值")
    private Long commentHot;

    @Schema(description = "评论用户id")
    private Long userId;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
