package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2026-04-15 20:05
 */
@Data
public class ActivityReviewStateMachineContext {
    private ActivityWithScheduleAddForm activityWithScheduleAddForm;
    private ActivityWithScheduleUpdateForm activityWithScheduleUpdateForm;
}
