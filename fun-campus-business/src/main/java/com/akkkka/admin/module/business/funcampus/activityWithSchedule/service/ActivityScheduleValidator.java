package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import lombok.AllArgsConstructor;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import org.springframework.stereotype.Service;

/**
 * author:akkkka114514
 * create at 2026-03-26 08:37
 */
@Service
@AllArgsConstructor
public class ActivityScheduleValidator {
    private final ActivityScheduleManager scheduleManager;
    public void validateActivityScheduleOrder(ActivityScheduleEntity schedule,boolean needSignOut){
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
}
