package com.akkkka.admin.module.business.funcampus.collegeInfo.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学院信息 更新表单
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:45:43
 * @Copyright akkkka114514
 */

@Data
public class CollegeInfoUpdateForm {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "id 不能为空")
    private Long id;

}