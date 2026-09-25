package com.akkkka.admin.module.business.funcampus.activityComment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动评论 VO
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */
@Data
public class ActivityCommentVO {

    @Schema(description = "评论id")
    private Long id;

    @Schema(description = "评论用户id")
    private Long userId;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "被回复的评论id")
    private Long toCommentId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "根评论id")
    private Long rootId;

    @Schema(description = "评论热度")
    private Long commentHot;

    @Schema(description = "子评论列表")
    private List<ActivityCommentVO> children;

}
