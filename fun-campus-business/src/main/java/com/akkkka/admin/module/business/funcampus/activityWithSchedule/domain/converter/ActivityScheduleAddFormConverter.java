package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.converter;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityScheduleAddForm;

import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2026-04-19 14:36
 */
public class ActivityScheduleAddFormConverter {
    public static ActivityScheduleEntity convert(ActivityScheduleAddForm addForm){
        ActivityScheduleEntity schedule=new ActivityScheduleEntity();
        schedule.setEnrollStartTime(addForm.getEnrollStartTime());
        schedule.setEnrollEndTime(addForm.getEnrollEndTime());
        schedule.setActivityStartTime(addForm.getActivityStartTime());
        schedule.setActivityEndTime(addForm.getActivityEndTime());
        schedule.setSigninStartTime(addForm.getSigninStartTime());
        schedule.setSigninEndTime(addForm.getSigninEndTime());
        schedule.setSignoutStartTime(addForm.getSignoutStartTime());
        schedule.setSignoutEndTime(addForm.getSignoutEndTime());
        schedule.setCreateTime(LocalDateTime.now());
        schedule.setUpdateTime(LocalDateTime.now());
        schedule.setDeletedFlag(false);

        return schedule;
    }
}
