package com.akkkka.admin.module.business.funcampus.tribe.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部落加入申请 表单（门户）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class TribeApplicationApplyForm {

    @Schema(description = "部落id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部落id 不能为空")
    private Long tribeId;

    @Schema(description = "申请理由（加入理由）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申请理由不能为空")
    @Size(max = 500, message = "申请理由最多500字符")
    private String reason;

}
