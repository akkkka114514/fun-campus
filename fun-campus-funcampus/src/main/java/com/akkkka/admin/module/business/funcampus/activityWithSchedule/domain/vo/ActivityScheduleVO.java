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
    private LocalDateTime enrollStartTime;
    private LocalDateTime enrollEndTime;
    private LocalDateTime activityStartTime;
    private LocalDateTime activityEndTime;
    private LocalDateTime signinStartTime;
    private LocalDateTime signinEndTime;
    private LocalDateTime signoutStartTime;
    private LocalDateTime signoutEndTime;

}
