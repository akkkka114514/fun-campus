package com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动能报名的部落 更新表单
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:18:01
 * @Copyright akkkka114514
 */

@Data
public class ActivityCanEnrollTribeUpdateForm {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "id 不能为空")
    private Long id;

}