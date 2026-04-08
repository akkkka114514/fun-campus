package com.akkkka.admin.module.business.funcampus.portalUser.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.entity.ActivityCanEnrollCollegeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.manager.ActivityCanEnrollCollegeManager;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.manager.ActivityCanEnrollGradeManager;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.entity.ActivityCanEnrollTribeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.manager.ActivityCanEnrollTribeManager;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.entity.TribeUserEntity;
import com.akkkka.admin.module.business.funcampus.tribeUser.manager.TribeUserManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author:akkkka114514
 * create at 2026-03-26 08:49
 */
@Service
@AllArgsConstructor
public class PortalUserValidator {
    private final PortalUserManager portalUserManager;
    private final ActivityCanEnrollCollegeManager canEnrollCollegeManager;
    private final ActivityCanEnrollGradeManager canEnrollGradeManager;
    private final ActivityCanEnrollTribeManager canEnrollTribeManager;
    private final TribeUserManager tribeUserManager;

    public void validatePortalUserId(Long userId){
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if(portalUser==null || portalUser.getDeletedFlag()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"用户不存在或已删除");
        }
    }
    /**
     * 验证用户是否在活动允许的学院范围内
     * @param activityId 活动ID
     * @param portalUser 用户实体信息
     */
    public void validateUserInCollege(Long activityId, PortalUserEntity portalUser){
        LambdaQueryWrapper<ActivityCanEnrollCollegeEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ActivityCanEnrollCollegeEntity::getActivityId,activityId)
                .eq(ActivityCanEnrollCollegeEntity::getDeletedFlag,false);
        List<ActivityCanEnrollCollegeEntity> list = canEnrollCollegeManager.list(lqw);
        if(list==null||list.isEmpty()){
            return;
        }

        if(list
                .stream()
                .noneMatch(
                        e->e.getCanEnrollCollege().equals(portalUser.getCollegeId()
                        )
                )
        ){
            throw new BusinessException(UserErrorCode.NO_PERMISSION,"不是活动指定学院不能报名该活动");
        }
    }

    public void validateUserInGrade(Long activityId, PortalUserEntity portalUser){
        LambdaQueryWrapper<ActivityCanEnrollGradeEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ActivityCanEnrollGradeEntity::getActivityId,activityId)
                .eq(ActivityCanEnrollGradeEntity::getDeletedFlag,false);
        List<ActivityCanEnrollGradeEntity> list = canEnrollGradeManager.list(lqw);
        if(list==null||list.isEmpty()){
            return;
        }
        if(list.stream()
                .noneMatch(
                        e->e.getCanEnrollGrade().equals(portalUser.getGradeId())
                )
        ){
            throw new BusinessException(UserErrorCode.NO_PERMISSION,"不是活动指定年级不能报名该活动");
        }
    }

    public void validateUserInTribe(Long activityId, PortalUserEntity portalUser){
        LambdaQueryWrapper<ActivityCanEnrollTribeEntity> canEnrollTribesQuery=new LambdaQueryWrapper<>();
        canEnrollTribesQuery.eq(ActivityCanEnrollTribeEntity::getActivityId,activityId)
                .eq(ActivityCanEnrollTribeEntity::getDeletedFlag,false);
        List<ActivityCanEnrollTribeEntity> canEnrollTribes = canEnrollTribeManager.list(canEnrollTribesQuery);
        if(canEnrollTribes==null||canEnrollTribes.isEmpty()){
            return;
        }
        LambdaQueryWrapper<TribeUserEntity> tribeUserQuery = new LambdaQueryWrapper<>();
        tribeUserQuery.eq(TribeUserEntity::getPortalUserId,portalUser.getId())
                .eq(TribeUserEntity::getDeletedFlag,false);
        List<TribeUserEntity> tribeUserList = tribeUserManager.list(tribeUserQuery);
        if (tribeUserList==null||tribeUserList.isEmpty()){
            throw new BusinessException(UserErrorCode.NO_PERMISSION,"不属于活动指定的部落不能报名该活动");
        }

        List<Long> canEnrollTribeIds = canEnrollTribes
                .stream()
                .map(ActivityCanEnrollTribeEntity::getCanEnrollTribe).toList();
        if(tribeUserList
                .stream()
                .noneMatch(
                        e->canEnrollTribeIds.contains(e.getTribeId())
                )
        ){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"不属于活动指定的部落不能报名该活动");
        }
    }

    public void validatePortalUserIds(List<Long> userIds){
        List<PortalUserEntity> dbSignOutUsers = portalUserManager.listByIds(userIds);
        if(dbSignOutUsers.size()!=userIds.size()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"用户列表中有用户不存在");
        }
        for(PortalUserEntity signOutUser:dbSignOutUsers){
            if(signOutUser.getDisableFlag()||signOutUser.getDeletedFlag()){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"用户列表中由用户已删除或已封禁");
            }
        }
    }
}
