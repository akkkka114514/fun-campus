package com.akkkka.admin.module.business.funcampus.creditApplication.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学分认定申请 编辑表单（门户，仅待审核可编辑）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CreditApplicationUpdateForm extends CreditApplicationApplyForm {

    @Schema(description = "申请id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "申请id不能为空")
    private Long id;

}
