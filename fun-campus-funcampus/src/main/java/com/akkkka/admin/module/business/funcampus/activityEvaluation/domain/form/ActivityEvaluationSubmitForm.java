package com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 活动评价 提交表单（门户）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class ActivityEvaluationSubmitForm {

    @Schema(description = "活动id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动id不能为空")
    private Long activityId;

    @Schema(description = "评分：1-5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1分")
    @Max(value = 5, message = "评分最高5分")
    private Integer score;

    @Schema(description = "评价内容")
    @Size(max = 1000, message = "评价内容最多1000字符")
    private String content;

}
