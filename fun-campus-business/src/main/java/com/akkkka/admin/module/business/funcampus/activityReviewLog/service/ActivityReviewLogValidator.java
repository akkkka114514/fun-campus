package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;
import cn.hutool.core.util.EnumUtil;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * author:akkkka114514
 * create at 2026-03-02 15:31
 * 不能单例复用
 */
@Component
@AllArgsConstructor
public class ActivityReviewLogValidator {
    private ActivityReviewLogManager reviewLogManager;

    public void validate(ActivityReviewLogAddForm addForm) {
        // TODO: implement validation logic
    }

    public ActivityReviewLogEntity validateReviewLogId(Long reviewLogId){
        ActivityReviewLogEntity reviewLog = reviewLogManager.getById(reviewLogId);
        if(reviewLog==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        return reviewLog;
    }

    public void validateCurrentReviewStage(Long activityId,ActivityReviewStage currentReviewStage){
        ActivityReviewLogEntity reviewLog = reviewLogManager.getOne(
                Wrappers.lambdaQuery(ActivityReviewLogEntity.class)
                        .eq(ActivityReviewLogEntity::getActivityId,activityId)
                        .eq(ActivityReviewLogEntity::getDeletedFlag,false)
                        .eq(ActivityReviewLogEntity::getReviewStage,currentReviewStage)
                        .select(ActivityReviewLogEntity::getAction)
        );
        if(reviewLog==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,
                    "不存在activityId为"+activityId+"且审核阶段为"+currentReviewStage+"且未删除的活动");
        }
        if(reviewLog.getAction()!=null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"该审核阶段已结束");
        }
    }

    public void validateNextReviewStage(Long activityId,ActivityReviewStage nextReviewStage){
        ActivityReviewLogEntity nextReviewLog = reviewLogManager.getOne(
                Wrappers.lambdaQuery(ActivityReviewLogEntity.class)
                        .eq(ActivityReviewLogEntity::getActivityId,activityId)
                        .eq(ActivityReviewLogEntity::getDeletedFlag,false)
                        .select(ActivityReviewLogEntity::getReviewStage)
                        .orderByDesc(ActivityReviewLogEntity::getId)
                        .last("limit 1")
        );
        //防止审核顺序不对
        if(nextReviewLog.getReviewStage().getOrder() != nextReviewStage.getOrder()-1){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
    }

    public void validateReviewPermission(BackendUserEntity backendUser){
        if(!backendUser.getCanReview()){
            throw new BusinessException(UserErrorCode.NO_PERMISSION,"当前后台用户没有审核权限");
        }
    }


    public void validateReviewerName(String reviewerName,BackendUserEntity backendUser){
        if(!reviewerName.equals(backendUser.getUsername())){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"后台用户用户名与输入用户名不一致");
        }
    }

    public void validateReviewAction(ActivityReviewStage stage,ActivityReviewEvent event) {
        if (stage.equals(ActivityReviewStage.CONTENT_CHECK)) {
            if (!event.equals(ActivityReviewEvent.CHECK_PASS)) {
                throw new BusinessException(UserErrorCode.NO_PERMISSION, "活动审阅人不具有建议之外的权限");
            }
        } else {
            if (event.equals(ActivityReviewEvent.CHECK_PASS)) {
                throw new BusinessException(UserErrorCode.NO_PERMISSION, "活动审核人不具有建议权限");
            }
        }
    }


}
