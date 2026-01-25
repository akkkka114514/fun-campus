package net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动分类 更新表单
 *
 * @Author akkkka114514
 * @Date 2026-01-10 19:46:44
 * @Copyright akkkka114514
 */

@Data
public class ActivityCategoryUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

    @Schema(description = "活动类型名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "活动类型名称 不能为空")
    private String name;

}