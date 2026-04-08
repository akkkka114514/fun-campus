package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import lombok.AllArgsConstructor;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import org.springframework.stereotype.Service;

/**
 * author:akkkka114514
 * create at 2026-03-26 08:28
 */
@Service
@AllArgsConstructor
public class ActivityValidator {
    private final ActivityManager activityManager;

    public ActivityEntity validateActivityId(Long activityId){
        ActivityEntity activityEntity = activityManager.getById(activityId);
        //检查活动是否存在，是否已删除
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        return activityEntity;
    }

}
