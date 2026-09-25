package com.akkkka.admin.module.business.funcampus.activitySigninManager.service;

import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.manager.ActivitySigninManagerManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * author:akkkka114514
 * create at 2026-03-27 14:41
 */
@Service
@AllArgsConstructor
@Slf4j
public class SignInManagerValidator {
    private final ActivitySigninManagerManager signinManagerManager;
    private final PortalUserManager portalUserManager;

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

    public void validateSignInManagerIds(List<Long> ids,Long belongToSchoolId){
        List<PortalUserEntity> filteredPortalUserList=portalUserManager.listByIds(ids)
                .stream()
                .filter(e->!e.getDisableFlag())
                .filter(e->!e.getDeletedFlag())
                .filter(e->e.getSchoolId().equals(belongToSchoolId))
                .toList();
        if(filteredPortalUserList.size()!=ids.size()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签到员id不合法");
        }
    }

}
