package com.akkkka.admin.module.business.funcampus.organizationInfo.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 各学校组织信息 更新表单
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:24:05
 * @Copyright akkkka114514
 */

@Data
public class OrganizationInfoUpdateForm {

    @Schema(description = "组织id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "组织id 不能为空")
    private Long id;

}