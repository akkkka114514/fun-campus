package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动报名关系 更新表单
 *
 * @Author akkkka114514
 * @Date 2025-10-02 13:54:42
 * @Copyright akkkka114514
 */

@Data
public class ActivityEnrollmentUpdateForm {

    @Schema(description = "活动主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动主键 不能为空")
    private Long activityId;

    @Schema(description = "用户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户主键 不能为空")
    private Long userId;

    @Schema(description = "是否已签到，1-》是，0-》否", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否已签到，1-》是，0-》否 不能为空")
    private Boolean signInStatus;

    @Schema(description = "是否删除，1-》是，0-》否", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否删除，1-》是，0-》否 不能为空")
    private Boolean deletedFlag;
}