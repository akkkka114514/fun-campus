package com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动评价 分页查询表单（按活动查评价列表，门户活动详情页）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityEvaluationQueryForm extends PageParam {

    @Schema(description = "活动id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动id不能为空")
    private Long activityId;

}
