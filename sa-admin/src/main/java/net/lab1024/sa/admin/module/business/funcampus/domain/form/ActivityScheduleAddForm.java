package net.lab1024.sa.admin.module.business.funcampus.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动时间表 新建表单
 *
 * @Author akkkka114514
 * @Date 2025-09-06 15:54:07
 * @Copyright akkkka114514
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

}