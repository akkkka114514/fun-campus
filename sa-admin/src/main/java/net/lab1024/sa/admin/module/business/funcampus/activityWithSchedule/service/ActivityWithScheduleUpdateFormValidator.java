package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.manager.ActivityCategoryManager;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.manager.OrganizationInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.manager.SchoolInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.tribe.service.TribeService;
import net.lab1024.sa.admin.module.business.funcampus.tribeUser.service.TribeUserService;
import net.lab1024.sa.admin.module.business.funcampus.util.StrictlyIncreasingLocalDateTimeList;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.manager.BackendUserManager;
import net.lab1024.sa.admin.module.system.login.domain.RequestBackendUser;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartRequestUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * author:akkkka114514
 * create at 2026-02-24 15:04
 * 不能作为单例复用
 */
public class ActivityWithScheduleUpdateFormValidator {
    @Resource
    private ActivityScheduleManager activityScheduleManager;
    @Resource
    private ActivityManager activityManager;
    @Resource
    private SchoolInfoManager schoolInfoManager;
    @Resource
    private CollegeInfoManager collegeInfoManager;
    @Resource
    private PortalUserManager portalUserManager;
    @Resource
    private OrganizationInfoManager organizationInfoManager;
    @Resource
    private ActivityReviewLogManager reviewLogManager;
    @Resource
    private BackendUserManager backendUserManager;
    @Resource
    private ActivityCategoryManager activityCategoryManager;
    @Resource
    private TribeUserService tribeUserService;
    private final RequestUser requestUser = SmartRequestUtil.getRequestUser();
    private PortalUserEntity portalUser;
    private BackendUserEntity backendUser;
    private ActivityEntity activityEntity;
    private Long schoolId;


    public void validate(ActivityWithScheduleUpdateForm updateForm){
        validateActivityId(updateForm);
        validateActivityUpdateUserAndTime(updateForm);
        validateActivityBelongTo(updateForm);
        validateActivityTitleUnique(updateForm);
        validateActivityCategory(updateForm);
        validateActivitySchedule(updateForm);
        validateActivityManager(updateForm);
        validateSignInManager(updateForm);
        validateInitialReviewer(updateForm);
    }

    private void validateActivityId(ActivityWithScheduleUpdateForm updateForm){
        activityEntity = activityManager.getById(updateForm.getId());
        if(activityEntity ==null || activityEntity.getDeletedFlag()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"修改不存在的活动");
        }
    }

    private void validateActivityUpdateUserAndTime(ActivityWithScheduleUpdateForm updateForm){
        Long activityId = updateForm.getId();
        LambdaQueryWrapper<ActivityReviewLogEntity> lqw=new LambdaQueryWrapper<>();
        //选取最新的活动审核日志
        lqw.eq(ActivityReviewLogEntity::getActivityId,activityId)
                .eq(ActivityReviewLogEntity::isDeletedFlag,false)
                .select(ActivityReviewLogEntity::getReviewerId
                        ,ActivityReviewLogEntity::getReviewStage
                        ,ActivityReviewLogEntity::getAction)
                .orderByDesc(ActivityReviewLogEntity::getCreateTime)
                .last("LIMIT 1");

        ActivityReviewLogEntity reviewLogEntity = reviewLogManager.getOne(lqw);
        if(reviewLogEntity!=null){
            if(requestUser instanceof RequestPortalUser){
                throw new BusinessException(UserErrorCode.NO_PERMISSION,
                        "活动正在审核中，前台用户没有权限修改活动，请先取消审核");
            }
            if(reviewLogEntity.getReviewStage().equals(ActivityReviewStage.CHECK)){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"审阅阶段不能修改活动内容");
            }
            if(requestUser instanceof RequestBackendUser){
                if(!Objects.equals(reviewLogEntity.getReviewerId(), requestUser.getUserId())) {
                    throw new BusinessException(UserErrorCode.PARAM_ERROR, "不是负责审核该活动阶段的后台用户");
                }else {
                    this.backendUser=backendUserManager.getById(requestUser.getUserId());
                    this.schoolId=backendUser.getSchoolId();
                }
            }
            validateActivityCanUpdateTime(reviewLogEntity);
        }else{
            if(requestUser instanceof RequestPortalUser){
                this.portalUser=portalUserManager.getById(requestUser.getUserId());
                this.schoolId=portalUser.getSchoolId();
            }
        }
    }
    private void validateActivityCanUpdateTime(ActivityReviewLogEntity reviewLogEntity){
        assert reviewLogEntity != null;
        if(Objects.equals(reviewLogEntity.getReviewStage(),ActivityReviewStage.FINAL_REVIEW)
                &&reviewLogEntity.getAction()!=null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"终审完成的活动不能修改");
        }
        if(reviewLogEntity.getReviewStage()>ActivityReviewStage.FINAL_REVIEW){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"终审完成的活动不能修改");
        }
    }

    private void validateActivitySchedule(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getEnrollStartTime()==null&&
        updateForm.getEnrollEndTime()==null&&
        updateForm.getActivityStartTime()==null&&
        updateForm.getActivityEndTime()==null&&
        updateForm.getSigninStartTime()==null&&
        updateForm.getSigninEndTime()==null&&
        updateForm.getSignoutStartTime()==null&&
        updateForm.getSignoutEndTime()==null){
            return;
        }
        ActivityScheduleEntity oldOne = activityScheduleManager.getById(updateForm.getId());
        ActivityEntity checkNeedSignOut = activityManager.getById(updateForm.getId());
        StrictlyIncreasingLocalDateTimeList list =new StrictlyIncreasingLocalDateTimeList();
        try {
            if (updateForm.getEnrollStartTime() != null) {
                list.add(updateForm.getEnrollStartTime());
            } else {
                list.add(oldOne.getEnrollStartTime());
            }
            if (updateForm.getEnrollEndTime() != null) {
                list.add(updateForm.getEnrollEndTime());
            } else {
                list.add(oldOne.getEnrollEndTime());
            }
            if (updateForm.getActivityStartTime() != null) {
                list.add(updateForm.getActivityStartTime());
            } else {
                list.add(oldOne.getActivityStartTime());
            }
            if (updateForm.getActivityEndTime() != null) {
                list.add(updateForm.getActivityEndTime());
            } else {
                list.add(oldOne.getActivityStartTime());
            }
            if (updateForm.getSigninStartTime() != null) {
                list.add(updateForm.getSigninStartTime());
            } else {
                list.add(oldOne.getSigninStartTime());
            }
            if (updateForm.getSigninStartTime() != null) {
                list.add(updateForm.getSigninEndTime());
            } else {
                list.add(oldOne.getSigninEndTime());
            }
            if (updateForm.isNeedSignOut() || checkNeedSignOut.isNeedSignOut()) {
                if (updateForm.getSignoutStartTime() != null) {
                    list.add(updateForm.getSignoutStartTime());
                } else {
                    list.add(oldOne.getSignoutStartTime());
                }
                if (updateForm.getSignoutStartTime() != null) {
                    list.add(updateForm.getSignoutEndTime());
                } else {
                    list.add(oldOne.getSignoutEndTime());
                }
            }
        }catch (IllegalArgumentException e){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"修改活动时间必须按照顺序");
        }
    }
    private void validateActivityBelongTo(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getActivityBelongToSchoolId()==null
            &&updateForm.getActivityBelongToOrganizationId()==null
            &&updateForm.getActivityBelongToCollegeId()==null){
            return;
        }
        //activityBelongToCollegeId和activityBelongToOrganizationId不能同时为空或同时不为空
        if((updateForm.getActivityBelongToCollegeId() != null|| updateForm.getActivityBelongToSchoolId()!=null) && updateForm.getActivityBelongToOrganizationId() != null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"activityBelongToCollegeId和activityBelongToOrganizationId不能同时为空或同时不为空");
        }
        //如果只改学校id又不改college id，肯定有问题
        if(updateForm.getActivityBelongToSchoolId()!=null&&updateForm.getActivityBelongToCollegeId()==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"如果只改学校id又不改college id");
        }
        //检查活动所属学校是否存在
        schoolInfoManager.getOptById(updateForm.getActivityBelongToSchoolId())
                .filter((school)->!school.getDeletedFlag())
                .filter(school->school.getId().equals(schoolId))
                .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"活动所属学校不存在"));

        //如果活动所属学院字段不为null,则说明是学院活动
        //检查学院是否存在,以及与表单中的活动所属学院id一致
        if(updateForm.getActivityBelongToCollegeId() != null){
            collegeInfoManager.getOptById(updateForm.getActivityBelongToCollegeId())
                    .filter((college)->!college.getDeletedFlag())
                    .filter((college)->college.getSchoolId().equals(schoolId))
                    .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"添加活动传入的collegeId为错误信息"));;
        }
        //如果活动所属组织字段不为null,则说明是组织活动
        //检查组织是否存在,以及与表单中的活动所属组织id一致
        if(updateForm.getActivityBelongToOrganizationId() != null){
            organizationInfoManager.getOptById(updateForm.getActivityBelongToOrganizationId())
                    .filter((org)->!org.getDeletedFlag())
                    .filter((org)->org.getSchoolId().equals(schoolId))
                    .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"organization不存在或organization的school与其他数据不一致"));
        }

    }

    private void validateActivityTitleUnique(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getTitle()==null){
            return;
        }
        //活动中不能有和添加活动标题一致的
        Optional.ofNullable(
                        activityManager.getOne(
                                new LambdaQueryWrapper<ActivityEntity>()
                                        .eq(ActivityEntity::getTitle,updateForm.getTitle())
                        )
                ).filter(e->!e.getDeletedFlag())
                .ifPresent((e)->{
                            throw new BusinessException(UserErrorCode.PARAM_ERROR,"包含此标题的活动已存在");
                        }
                );
    }
    private void validateActivityCategory(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getCategoryId()==null){
            return;
        }
        activityCategoryManager.getOptById(updateForm.getCategoryId())
                .filter((cate)->!cate.getDeletedFlag())
                .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"活动分类不存在"));

    }

    private void validateActivityManager(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getActivityManagerId()!=null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动管理员为发起活动者，不能更改");
        }
    }

    private void validateInitialReviewer(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getInitialReviewer()==null){
            return;
        }
        if(updateForm.getInitialReviewerName()==null){
            return;
        }
        if(requestUser instanceof RequestPortalUser){
            BackendUserEntity backendUser=Optional.ofNullable(backendUserManager.getById(updateForm.getInitialReviewer()))
                    .filter(e->!e.getDeletedFlag())
                    .filter(e->!e.getDisabledFlag())
                    .filter(e->e.getSchoolId().equals(portalUser.getSchoolId()))
                    .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"活动初审人校验不正确"));
            validateInitialReviewerName(updateForm,backendUser);
        }
        if(requestUser instanceof RequestBackendUser){
            throw new BusinessException(UserErrorCode.NO_PERMISSION,"后台用户不允许修改活动初审人");
        }
    }

    private void validateInitialReviewerName(ActivityWithScheduleUpdateForm updateForm,BackendUserEntity backendUser){
        if(!updateForm.getInitialReviewerName().equals(backendUser.getUsername())){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"传入的初审人用户名与数据库内容不相符");
        }
    }

    private void validateSignInManager(ActivityWithScheduleUpdateForm updateForm){
        if(updateForm.getActivitySigninManagerIdList()==null){
            return;
        }
        List<PortalUserEntity> filteredPortalUserList=portalUserManager.listByIds(updateForm.getActivitySigninManagerIdList())
                .stream()
                .filter(e->!e.getDisableFlag())
                .filter(e->!e.getDeletedFlag())
                .filter(e->e.getSchoolId().equals(schoolId))
                .toList();
        if(filteredPortalUserList.size()!=updateForm.getActivitySigninManagerIdList().size()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签到员id不合法");
        }
        if(updateForm.getCanEnrollTribeIdList()!=null
                &&tribeUserService.usersExistsInTribes(updateForm.getActivitySigninManagerIdList(),updateForm.getCanEnrollTribeIdList())){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签到员id不合法");
        }

        if(updateForm.getCanEnrollCollegeIdList()!=null){
            if(filteredPortalUserList.stream()
                    .filter(e->updateForm.getCanEnrollCollegeIdList().contains(e.getCollegeId()))
                    .filter(e->updateForm.getCanEnrollGradeIdList().contains(e.getGradeId()))
                    .count()!=updateForm.getActivitySigninManagerIdList().size()){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签到员id不合法");
            }
        }
    }
}
