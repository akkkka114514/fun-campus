package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import com.akkkka.admin.module.business.funcampus.activityCategory.service.ActivityCategoryValidator;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityAddForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import com.akkkka.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import com.akkkka.admin.module.business.funcampus.collegeInfo.service.CollegeInfoValidator;
import com.akkkka.admin.module.business.funcampus.gradeInfo.manager.GradeInfoManager;
import com.akkkka.admin.module.business.funcampus.organizationInfo.manager.OrganizationInfoManager;
import com.akkkka.admin.module.business.funcampus.organizationInfo.service.OrganizationInfoValidator;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.admin.module.business.funcampus.schoolInfo.manager.SchoolInfoManager;
import com.akkkka.admin.module.business.funcampus.schoolInfo.service.SchoolInfoValidator;
import com.akkkka.admin.module.business.funcampus.tribe.manager.TribeManager;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * author:akkkka114514
 * create at 2026-03-26 08:28
 */
@Service
@AllArgsConstructor
public class ActivityValidator {
    private final ActivityManager activityManager;
    private final CollegeInfoValidator collegeValidator;
    private final OrganizationInfoValidator organizationValidator;
    private final SchoolInfoValidator schoolValidator;
    private final PortalUserValidator portalUserValidator;
    private final ActivityCategoryValidator categoryValidator;


    public void validateAdd(ActivityEntity entity,PortalUserEntity portalUser){
        validateActivityBelongTo(entity,portalUser);
        validateActivityTitleUnique(entity.getTitle());
        validateActivityManager(
                entity.getActivityManagerId(),
                entity.getActivityBelongToSchoolId());
        categoryValidator.validateActivityCategory(entity.getCategoryId());
    }

    public void validateUpdate(ActivityEntity activity){
        validateActivityId(activity.getId());
        if(activity.getTitle()!=null){
            validateActivityTitleUnique(activity.getTitle());
        }
        if(activity.getActivityManagerId()!=null){
            Long schoolId = activityManager.getById(activity.getId()).getActivityBelongToSchoolId();
            validateActivityManager(activity.getActivityManagerId(), schoolId);
        }
        if(activity.getCategoryId()!=null){
            categoryValidator.validateActivityCategory(activity.getCategoryId());
        }
    }

    public ActivityEntity validateActivityId(Long activityId){
        ActivityEntity activityEntity = activityManager.getById(activityId);
        //检查活动是否存在，是否已删除
        if(activityEntity==null||activityEntity.getDeletedFlag()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        return activityEntity;
    }
    public void validateActivityBelongTo(ActivityEntity activity, PortalUserEntity portalUser){
        if(activity.getActivityBelongToSchoolId()==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }

        //检查活动所属学校是否存在
        schoolValidator.validateSchoolId(activity.getActivityBelongToSchoolId());
        schoolValidator.validateUserSchool(portalUser, activity.getActivityBelongToSchoolId());

        //如果活动所属组织字段不为null,则说明是组织活动
        //检查组织是否存在,以及与表单中的活动所属组织id一致
        if(activity.getActivityBelongToOrganizationId()!=null){
            organizationValidator.validateOrganizationId(activity.getActivityBelongToOrganizationId());
            organizationValidator.validateUserOrganization(portalUser, activity.getActivityBelongToOrganizationId());
        }else if(activity.getActivityBelongToCollegeId()!=null){
            collegeValidator.validateCollegeId(activity.getActivityBelongToCollegeId());
            collegeValidator.validateUserCollege(portalUser,activity.getActivityBelongToCollegeId());
        }else{
            //organization id和college id不能同时为空
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }

    }

    public void validateActivityTitleUnique(String activityTitle){
        //活动中不能有和添加活动标题一致的
        Optional.ofNullable(
                        activityManager.getOne(
                                new LambdaQueryWrapper<ActivityEntity>()
                                        .eq(ActivityEntity::getTitle,activityTitle)
                        )
                ).filter(e->!e.getDeletedFlag())
                .ifPresent((e)->{
                            throw new BusinessException(UserErrorCode.PARAM_ERROR,"包含此标题的活动已存在");
                        }
                );
    }

    public void validateActivityManager(Long userId,Long belongToSchoolId){
        portalUserValidator.validateIsCurrentUserPortal();
        PortalUserEntity portalUser = portalUserValidator.validatePortalUserId(userId);
        portalUserValidator.validateUserInSchool(portalUser,belongToSchoolId);
    }

}
