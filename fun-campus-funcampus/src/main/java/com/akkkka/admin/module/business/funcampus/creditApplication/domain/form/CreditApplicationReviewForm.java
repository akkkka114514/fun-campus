package com.akkkka.admin.module.business.funcampus.creditApplication.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 学分认定申请 审核表单（管理端）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class CreditApplicationReviewForm {

    @Schema(description = "申请id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "申请id不能为空")
    private Long id;

    @Schema(description = "是否通过：true-通过 false-驳回", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    @Schema(description = "审核意见/驳回原因")
    @Size(max = 500, message = "审核意见最多500字符")
    private String reviewRemark;

}
