package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form;

import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import jdk.jfr.BooleanFlag;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
* author:akkkka114514
* create at 2025-09-18 16:33
*/
@Data
@ToString
public class ActivityWithScheduleAddForm {
    private ActivityAddForm activityAddForm;
    private ActivityScheduleAddForm activityScheduleAddForm;
    private ActivityReviewLogAddForm reviewLogAddForm;

    /**
     * 能报名的年级的id
     */
    @Schema(description = "能报名的年级的id")
    private List<@Min(value = 0L, message = "能报名的年级的id不能为负数") Long> canEnrollGradeIdList;

    //能报名的学院的id
    @Schema(description = "能报名的学院的id")
    private List<@Min(value = 0L,message = "能报名的学院的id不能为负数") Long> canEnrollCollegeIdList;
    /**
     * 能报名的部落的id
     */
    @Schema(description = "能报名的部落的id")
    private List<@Min(value = 0L, message = "能报名的部落的id不能为负数") Long> canEnrollTribeIdList;

    @Schema(description = "活动签到管理员")
    private List<@Min(value = 0L,message = "活动签到管理员id不能为负数")Long> activitySigninManagerIdList;
}
