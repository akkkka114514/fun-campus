package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewAction;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.manager.BackendUserManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * author:akkkka114514
 * create at 2026-03-02 15:31
 * 不能单例复用
 */
@Component
public class ActivityReviewLogValidator {
    @Resource
    private ActivityReviewLogManager reviewLogManager;
    @Resource
    private ActivityManager activityManager;
    @Resource
    private BackendUserManager backendUserManager;
    @Resource
    private PortalUserManager portalUserManager;

    public void validate(ActivityReviewLogAddForm addForm){
        validateActivityId(addForm);
        validateActivityReviewStage(addForm);
        validateReviewerId(addForm);
        validateReviewAction(addForm);
    }

    private void validateActivityId(ActivityReviewLogAddForm addForm){
        ActivityEntity activity = activityManager.getById(addForm.getActivityId());
        if(activity==null || activity.getDeletedFlag()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"activityId对应的活动不存在或已删除");
        }
    }

    private void validateActivityReviewStage(ActivityReviewLogAddForm addForm){
        LambdaQueryWrapper<ActivityReviewLogEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ActivityReviewLogEntity::getActivityId,addForm.getActivityId())
                .eq(ActivityReviewLogEntity::getDeletedFlag,false)
                .eq(ActivityReviewLogEntity::getReviewStage,addForm.getReviewStage())
                .select(ActivityReviewLogEntity::getAction);

        ActivityReviewLogEntity reviewLog = reviewLogManager.getOne(qw);
        if(reviewLog==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,
                    "不存在activityId为"+addForm.getActivityId()+"且审核阶段为"+addForm.getReviewStage()+"且未删除的活动");
        }
        if(reviewLog.getAction()!=null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"该审核阶段已结束");
        }
    }

    private void validateReviewerId(ActivityReviewLogAddForm addForm){
        //检验当前阶段审核人
        BackendUserEntity backendUser = backendUserManager.getById(addForm.getReviewerId());
        if(backendUser==null||backendUser.getDeletedFlag()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"后台用户不存在或已删除");
        }
        if(backendUser.getDisabledFlag()){
            throw new BusinessException(UserErrorCode.USER_STATUS_ERROR,"后台用户已封禁");
        }
        if(!backendUser.getCanReview()){
            throw new BusinessException(UserErrorCode.NO_PERMISSION,"当前后台用户没有审核权限");
        }
        validateReviewerName(addForm,backendUser);
        //只有完结审核才能把指定下一个审核员空着
        if(addForm.getNextReviewerId()==null && !Objects.equals(addForm.getReviewStage(), ActivityReviewStage.END_REVIEW)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"只有完结审核才能把指定下一个审核员空着");
        }
        if(addForm.getNextReviewerId()!=null){
            //检验指定的下一阶段审核人
            if(!Objects.equals(addForm.getNextReviewerId(), addForm.getReviewerId())){
                BackendUserEntity backendUser2 = backendUserManager.getById(addForm.getReviewerId());
                if(backendUser2==null||backendUser.getDeletedFlag()){
                    throw new BusinessException(UserErrorCode.PARAM_ERROR,"后台用户不存在或已删除");
                }
                if(backendUser2.getDisabledFlag()){
                    throw new BusinessException(UserErrorCode.USER_STATUS_ERROR,"后台用户已封禁");
                }
                if(!backendUser2.getCanReview()){
                    throw new BusinessException(UserErrorCode.NO_PERMISSION,"当前后台用户没有审核权限");
                }
            }
        }

    }
    private void validateReviewerName(ActivityReviewLogAddForm addForm,BackendUserEntity backendUser){
        if(!addForm.getReviewerName().equals(backendUser.getUsername())){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"后台用户用户名与输入用户名不一致");
        }
    }

    private void validateReviewAction(ActivityReviewLogAddForm addForm){
        if(addForm.getReviewStage().equals(ActivityReviewStage.CHECK)){
            if(!addForm.getAction().equals(ActivityReviewAction.SUGGEST)){
                throw new BusinessException(UserErrorCode.NO_PERMISSION,"活动审阅人不具有建议之外的权限");
            }
        }else{
            if(addForm.getAction().equals(ActivityReviewAction.SUGGEST)){
                throw new BusinessException(UserErrorCode.NO_PERMISSION,"活动审核人不具有建议权限");
            }
        }
    }

}
