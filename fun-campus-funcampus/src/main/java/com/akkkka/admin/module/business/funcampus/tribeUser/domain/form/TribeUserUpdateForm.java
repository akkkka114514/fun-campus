package com.akkkka.admin.module.business.funcampus.tribeUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 参与部落的用户 更新表单
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:42:50
 * @Copyright akkkka114514
 */

@Data
public class TribeUserUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

}