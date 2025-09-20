package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2025-09-18 16:31
 */
@Data
public class ActivityWithScheduleUpdateForm extends ActivityWithScheduleAddForm{

    @Schema(description = "活动id")
    @NotNull(message = "活动id不能为空")
    private Long id;
}
