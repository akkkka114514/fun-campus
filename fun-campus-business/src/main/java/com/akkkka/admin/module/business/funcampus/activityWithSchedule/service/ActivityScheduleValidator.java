package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityScheduleUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.alibaba.cola.statemachine.builder.On;
import lombok.AllArgsConstructor;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * author:akkkka114514
 * create at 2026-03-26 08:37
 */
@Service
@AllArgsConstructor
public class ActivityScheduleValidator {
    private final ActivityScheduleManager scheduleManager;
    private final ActivityManager activityManager;
    public void validateActivityScheduleOrder(ActivityScheduleEntity schedule, boolean needSignOut){
        //报名开始时间《报名结束时间《活动开始时间《活动结束时间《签到开始时间《签到结束时间
        if(!(
                schedule.getEnrollStartTime().isBefore(schedule.getEnrollEndTime())
                &&schedule.getEnrollEndTime().isBefore(schedule.getActivityStartTime())
                &&schedule.getActivityStartTime().isBefore(schedule.getActivityEndTime())
                &&schedule.getActivityEndTime().isBefore(schedule.getSigninStartTime())
                &&schedule.getSigninStartTime().isBefore(schedule.getSigninEndTime())
        )
        ){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动时间不按顺序");
        }
        //如果选了需要签退
        if(needSignOut){
            if(schedule.getSignoutStartTime()==null||schedule.getSignoutEndTime()==null){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"选了需要签退但是签退时间为空");
            }
        }

        if (!(schedule.getSignoutEndTime().isAfter(schedule.getSignoutStartTime())
                &&schedule.getSignoutStartTime().isAfter(schedule.getSigninEndTime()))){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动时间不按顺序");
        }
    }



    public void validateUpdate(ActivityEntity activity,ActivityScheduleEntity schedule){
        if(Objects.isNull(schedule)){
            return;
        }
        ActivityScheduleEntity dbSchedule = scheduleManager.getById(schedule.getActivityId());
        ActivityEntity dbActivity = activityManager.getById(activity.getId());
        if(Objects.isNull(schedule.getEnrollStartTime())){
            schedule.setEnrollStartTime(dbSchedule.getEnrollStartTime());
        }
        if(Objects.isNull(schedule.getEnrollEndTime())){
            schedule.setEnrollEndTime(dbSchedule.getEnrollEndTime());
        }
        if(Objects.isNull(schedule.getActivityStartTime())){
            schedule.setActivityStartTime(dbSchedule.getActivityStartTime());
        }
        if(Objects.isNull(schedule.getActivityEndTime())){
            schedule.setActivityEndTime(dbSchedule.getActivityEndTime());
        }
        if(Objects.isNull(schedule.getSigninStartTime())){
            schedule.setSigninStartTime(dbSchedule.getSigninStartTime());
        }
        if(Objects.isNull(schedule.getSigninEndTime())){
            schedule.setSigninEndTime(dbSchedule.getSigninEndTime());
        }

        boolean formNeedSignOut = Objects.nonNull(activity.getNeedSignOut());
        boolean dbNeedSignOut = dbActivity.getNeedSignOut();
        boolean formSignOutStartTimeEmpty = Objects.isNull(schedule.getSignoutStartTime());
        boolean formSignOutEndTimeEmpty = Objects.isNull(schedule.getSignoutEndTime());
        //如果填了formNeedSignOut，必须sign out start time和 end time都填
        if(formNeedSignOut && (formSignOutStartTimeEmpty || formSignOutEndTimeEmpty)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }

        if(!formNeedSignOut && dbNeedSignOut){
            if(!formSignOutStartTimeEmpty){
                schedule.setSignoutStartTime(dbSchedule.getSignoutStartTime());
            }
            if(!formSignOutEndTimeEmpty){
                schedule.setSignoutEndTime(dbSchedule.getSignoutEndTime());
            }
        }
//        if(!formNeedSignOut && !dbNeedSignOut){
//            不用动
//        }
    }
}
