package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动时间表 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-09-06 15:54:07
 * @Copyright akkkka114514
 */

@Data
public class ActivityScheduleVO {
    @Schema(description = "报名开始时间")
    private LocalDateTime enrollStartTime;

    @Schema(description = "报名结束时间")
    private LocalDateTime enrollEndTime;

    @Schema(description = "活动开始时间")
    private LocalDateTime activityStartTime;

    @Schema(description = "活动结束时间")
    private LocalDateTime activityEndTime;

    @Schema(description = "签到开始时间")
    private LocalDateTime signinStartTime;

    @Schema(description = "签到结束时间")
    private LocalDateTime signinEndTime;

    @Schema(description = "签退开始时间")
    private LocalDateTime signoutStartTime;

    @Schema(description = "签退结束时间")
    private LocalDateTime signoutEndTime;

}
