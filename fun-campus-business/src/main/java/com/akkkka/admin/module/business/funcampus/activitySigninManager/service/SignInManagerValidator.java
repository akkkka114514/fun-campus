package com.akkkka.admin.module.business.funcampus.activitySigninManager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.manager.ActivitySigninManagerManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import org.springframework.stereotype.Service;

/**
 * author:akkkka114514
 * create at 2026-03-27 14:41
 */
@Service
@AllArgsConstructor
public class SignInManagerValidator {
    private final ActivitySigninManagerManager signinManagerManager;

    public void validateUserPermission(Long userId,Long activityId){
        LambdaQueryWrapper<ActivitySigninManagerEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ActivitySigninManagerEntity::getActivityId,activityId)
                .eq(ActivitySigninManagerEntity::getPortalUserId,userId)
                .eq(ActivitySigninManagerEntity::getDeletedFlag,false);
        ActivitySigninManagerEntity signinManager = signinManagerManager.getOne(qw);
        if(signinManager==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"你不是该活动的签到员");
        }
    }
}
