package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2026-04-18 14:25
 */
@Data
public class ActivityScheduleUpdateForm extends ActivityScheduleAddForm{
    private Long activityId;

    public static ActivityScheduleEntity convert(ActivityScheduleUpdateForm updateForm){
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(updateForm.getActivityId());
        schedule.setEnrollStartTime(updateForm.getEnrollStartTime());
        schedule.setEnrollEndTime(updateForm.getEnrollEndTime());
        schedule.setActivityStartTime(updateForm.getActivityStartTime());
        schedule.setActivityEndTime(updateForm.getActivityEndTime());
        schedule.setSigninStartTime(updateForm.getSigninStartTime());
        schedule.setSigninEndTime(updateForm.getSigninEndTime());
        schedule.setSignoutStartTime(updateForm.getSignoutStartTime());
        schedule.setSignoutEndTime(updateForm.getSignoutEndTime());
        schedule.setCreateTime(null);
        schedule.setUpdateTime(LocalDateTime.now());
        schedule.setDeletedFlag(false);

        return schedule;
    }
}
