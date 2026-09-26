package com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动报名关系 业务键表单（活动id+用户id，用于管理后台批量删除）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class ActivityEnrollmentKeyForm {

    @Schema(description = "活动主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动主键 不能为空")
    private Long activityId;

    @Schema(description = "用户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户主键 不能为空")
    private Long userId;

}
