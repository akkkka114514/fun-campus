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
            // 编辑场景排除自身，避免标题未改动时误报重复
            validateActivityTitleUniqueExcludeSelf(activity.getTitle(), activity.getId());
        }
        if(activity.getActivityManagerId()!=null){
            Long schoolId = activityManager.getById(activity.getId()).getActivityBelongToSchoolId();
            // 管理端编辑链路无门户登录态，这里只校验管理员用户存在且同校
            validateActivityManagerByAdmin(activity.getActivityManagerId(), schoolId);
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

    /**
     * 标题唯一性校验（排除自身）：编辑场景使用，标题未改动时不误报重复
     *
     * @param activityTitle  表单标题
     * @param selfActivityId 当前活动ID
     */
    public void validateActivityTitleUniqueExcludeSelf(String activityTitle,Long selfActivityId){
        Optional.ofNullable(
                        activityManager.getOne(
                                new LambdaQueryWrapper<ActivityEntity>()
                                        .eq(ActivityEntity::getTitle,activityTitle)
                                        .ne(selfActivityId!=null,ActivityEntity::getId,selfActivityId)
                        )
                ).filter(e->!e.getDeletedFlag())
                .ifPresent((e)->{
                            throw new BusinessException(UserErrorCode.PARAM_ERROR,"包含此标题的活动已存在");
                        }
                );
    }

    /**
     * 管理端新增活动校验：管理端无门户登录态，归属与活动管理员只做存在性/一致性校验
     */
    public void validateAddByAdmin(ActivityEntity entity){
        validateActivityBelongToByAdmin(entity);
        validateActivityTitleUnique(entity.getTitle());
        validateActivityManagerByAdmin(
                entity.getActivityManagerId(),
                entity.getActivityBelongToSchoolId());
        categoryValidator.validateActivityCategory(entity.getCategoryId());
    }

    /**
     * 归属校验（管理端）：学校必填且存在；组织/学院至少一个且存在；
     * 0 表示不归属该维度（数据库 organization_id 为 NOT NULL，仅归属学院时以 0 占位）；
     * 不校验当前用户的归属（管理端用户不属于任何 portal 组织/学院）
     */
    public void validateActivityBelongToByAdmin(ActivityEntity activity){
        if(activity.getActivityBelongToSchoolId()==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        schoolValidator.validateSchoolId(activity.getActivityBelongToSchoolId());

        Long organizationId = activity.getActivityBelongToOrganizationId();
        Long collegeId = activity.getActivityBelongToCollegeId();
        boolean hasOrganization = organizationId!=null&&organizationId!=0L;
        boolean hasCollege = collegeId!=null&&collegeId!=0L;
        if(hasOrganization){
            organizationValidator.validateOrganizationId(organizationId);
        }else if(hasCollege){
            collegeValidator.validateCollegeId(collegeId);
        }else{
            //organization id和college id不能同时为空
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
    }

    /**
     * 活动管理员校验（管理端）：只校验用户存在且属于活动学校；
     * 允许不指定管理员（activity_manager_id 库中可空）
     */
    public void validateActivityManagerByAdmin(Long userId,Long belongToSchoolId){
        if(userId==null){
            return;
        }
        PortalUserEntity portalUser = portalUserValidator.validatePortalUserId(userId);
        portalUserValidator.validateUserInSchool(portalUser,belongToSchoolId);
    }

}
