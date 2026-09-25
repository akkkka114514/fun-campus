package com.akkkka.admin.module.business.funcampus.activityEnrollment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author:akkkka114514
 * create at 2026-03-26 08:27
 */
@Service
@AllArgsConstructor
public class ActivityEnrollmentValidator {
    private final ActivityEnrollmentManager enrollmentManager;
    public void validateEnrollmentDuplicate(Long activityId,Long userId){
        // 检查用户是否已经报名过该活动
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId);
        ActivityEnrollmentEntity enrollment=enrollmentManager.getOne(queryWrapper);
        if(enrollment!=null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"请勿重复报名");
        }
    }
    //用于签到时查看是否报名过该活动
    public ActivityEnrollmentEntity validateEnrollmentExist(Long activityId,Long userId){
        LambdaQueryWrapper<ActivityEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEnrollmentEntity::getActivityId, activityId)
                .eq(ActivityEnrollmentEntity::getUserId, userId)
                .eq(ActivityEnrollmentEntity::getDeletedFlag, false);
        ActivityEnrollmentEntity enrollmentEntity = enrollmentManager.getOne(queryWrapper);
        if(enrollmentEntity == null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"签到要求已报名该活动");
        }
        return enrollmentEntity;
    }

    public void validateEnrollmentsExist(Long activityId,List<Long> userIds){
        LambdaQueryWrapper<ActivityEnrollmentEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ActivityEnrollmentEntity::getActivityId,activityId)
            .eq(ActivityEnrollmentEntity::getDeletedFlag,false)
            .in(ActivityEnrollmentEntity::getUserId,userIds)
            .select(ActivityEnrollmentEntity::getUserId);
        List<ActivityEnrollmentEntity> list = enrollmentManager.list(qw);
        if(list.size()!=userIds.size()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"签到要求已报名该活动");
        }
    }
}
