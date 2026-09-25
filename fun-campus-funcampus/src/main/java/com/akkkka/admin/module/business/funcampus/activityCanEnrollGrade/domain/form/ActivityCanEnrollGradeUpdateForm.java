package com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动能报名的年级 更新表单
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:42:17
 * @Copyright akkkka114514
 */

@Data
public class ActivityCanEnrollGradeUpdateForm {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "id 不能为空")
    private Long id;

}