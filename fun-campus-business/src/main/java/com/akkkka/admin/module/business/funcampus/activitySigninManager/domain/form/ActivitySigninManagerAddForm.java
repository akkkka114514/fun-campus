package com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动签到管理员 新建表单
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:10:19
 * @Copyright akkkka114514
 */

@Data
public class ActivitySigninManagerAddForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

    @Schema(description = "活动主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动主键 不能为空")
    private Long activityId;

    @Schema(description = "活动签到员主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动签到员主键 不能为空")
    private Long portalUserId;

    @Schema(description = "是否已删除", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否已删除 不能为空")
    private Boolean deletedFlag;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "创建时间 不能为空")
    private LocalDateTime createTime;

    @Schema(description = "修改时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "修改时间 不能为空")
    private LocalDateTime updateTime;

    @Schema(description = "签到员用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "签到员用户名 不能为空")
    private String username;

}