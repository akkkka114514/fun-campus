package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form;

import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogUpdateForm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * author:akkkka114514
 * create at 2025-09-18 16:31
 */
@Data
public class ActivityWithScheduleUpdateForm{
    @Nullable
    private ActivityUpdateForm activityUpdateForm;
    @Nullable
    private ActivityScheduleUpdateForm activityScheduleUpdateForm;
    /**
     * 能报名的年级的id
     */
    @Schema(description = "能报名的年级的id")
    @Nullable
    private List<@Min(value = 0L, message = "能报名的年级的id不能为负数") Long> canEnrollGradeIdList;

    //能报名的学院的id
    @Schema(description = "能报名的学院的id")
    @Nullable
    private List<@Min(value = 0L,message = "能报名的学院的id不能为负数") Long> canEnrollCollegeIdList;
    /**
     * 能报名的部落的id
     */
    @Schema(description = "能报名的部落的id")
    @Nullable
    private List<@Min(value = 0L, message = "能报名的部落的id不能为负数") Long> canEnrollTribeIdList;

    @Schema(description = "活动签到管理员")
    @Nullable
    private List<@Min(value = 0L,message = "活动签到管理员id不能为负数")Long> activitySigninManagerIdList;
}
