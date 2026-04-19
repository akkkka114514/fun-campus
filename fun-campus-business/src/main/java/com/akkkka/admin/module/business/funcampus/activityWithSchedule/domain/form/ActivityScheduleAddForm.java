package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2026-04-18 14:14
 */
@Data
public class ActivityScheduleAddForm {
    @Schema(description = "报名开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "报名开始时间 不能为空")
    @Future
    private LocalDateTime enrollStartTime;

    @Schema(description = "报名结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "报名结束时间 不能为空")
    @Future
    private LocalDateTime enrollEndTime;

    @Schema(description = "活动开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动开始时间 不能为空")
    @Future
    private LocalDateTime activityStartTime;

    @Schema(description = "活动结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动结束时间 不能为空")
    @Future
    private LocalDateTime activityEndTime;

    @Schema(description = "签到开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "签到开始时间 不能为空")
    @Future
    private LocalDateTime signinStartTime;

    @Schema(description = "签到结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "签到结束时间 不能为空")
    @Future
    private LocalDateTime signinEndTime;
    @Schema(description = "签退开始时间",requiredMode = Schema.RequiredMode.REQUIRED)
    @Future
    //签退开始时间
    private LocalDateTime signoutStartTime;

    //签退结束时间
    @Schema(description = "签退结束时间",requiredMode = Schema.RequiredMode.REQUIRED)
    @Future
    private LocalDateTime signoutEndTime;

}
