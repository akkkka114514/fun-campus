package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
* author:akkkka114514
* create at 2025-09-18 16:33
*/
@Data
public class ActivityWithScheduleAddForm {
    @Schema(description = "活动标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "活动标题 不能为空")
    private String title;

    @Schema(description = "活动地点", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "活动地点 不能为空")
    private String position;

    @Schema(description = "能得到的学分", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "能得到的学分 不能为空")
    @DecimalMin(value = "0", message = "能得到的学分 不能小于0")
    @DecimalMax(value = "100", message = "能得到的学分 不能大于100")
    private BigDecimal scoreCanGet;

    @Schema(description = "报名人数限制", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "报名人数限制 不能为空")
    @Min(value = 0L, message = "报名人数限制 不能小于0")
    @Max(value = 1000L, message = "报名人数限制 不能大于100000")
    private Integer enrollNumLimit;


    @Schema(description = "活动所属学校", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动所属学校 不能为空")
    @Min(value = 0L, message = "活动所属学校 不能小于0")
    private Long activitySchoolId;

    @Schema(description = "活动所属组织", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动所属组织 不能为空")
    @Min(value = 0L, message = "活动所属组织 不能小于0")
    private Long activityOrganizerId;

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
