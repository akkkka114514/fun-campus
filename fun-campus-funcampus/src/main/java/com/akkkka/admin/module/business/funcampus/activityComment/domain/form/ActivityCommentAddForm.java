package com.akkkka.admin.module.business.funcampus.activityComment.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发表评论 表单
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */
@Data
public class ActivityCommentAddForm {

    @Schema(description = "活动id")
    @NotNull(message = "活动id不能为空")
    private Long activityId;

    @Schema(description = "被回复的评论id，null即为顶层评论")
    private Long toCommentId;

    @Schema(description = "评论内容")
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容最多500字")
    private String content;

}
