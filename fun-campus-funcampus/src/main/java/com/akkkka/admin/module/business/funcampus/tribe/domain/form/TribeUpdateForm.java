package com.akkkka.admin.module.business.funcampus.tribe.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 部落 更新表单
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@Data
public class TribeUpdateForm {

    @Schema(description = "部落id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部落id 不能为空")
    private Long id;

}