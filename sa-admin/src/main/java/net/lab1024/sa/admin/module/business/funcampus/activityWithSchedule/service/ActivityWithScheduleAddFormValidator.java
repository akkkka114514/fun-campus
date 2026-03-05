package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.manager.ActivityCategoryManager;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.manager.GradeInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.manager.OrganizationInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.manager.SchoolInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.tribe.manager.TribeManager;
import net.lab1024.sa.admin.module.business.funcampus.tribeUser.service.TribeUserService;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.manager.BackendUserManager;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartRequestUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * author:akkkka114514
 * create at 2026-02-22 12:08
 * 不能作为单例复用
 */
public class ActivityWithScheduleAddFormValidator {
    @Resource
    private PortalUserManager portalUserManager;
    @Resource
    private CollegeInfoManager collegeInfoManager;
    @Resource
    private SchoolInfoManager schoolInfoManager;
    @Resource
    private OrganizationInfoManager organizationInfoManager;
    @Resource
    private ActivityCategoryManager activityCategoryManager;
    @Resource
    private TribeManager tribeManager;
    @Resource
    private GradeInfoManager gradeInfoManager;
    @Resource
    private ActivityManager activityManager;
    @Resource
    private BackendUserManager backendUserManager;
    @Resource
    private TribeUserService tribeUserService;
    @Resource
    private ActivityReviewLogManager reviewLogManager;

    private final Long userId = SmartRequestUtil.getRequestUserId();
    private PortalUserEntity portalUser;

    public void validate(ActivityWithScheduleAddForm addForm){
        validatePortalUser();
        validatePortalUserCanPublishActivity();
        validateActivitySchedule(addForm);
        validateActivityBelongTo(addForm);
        validateActivityCategory(addForm);
        validateActivityTitleUnique(addForm);
        validateInitialReviewer(addForm);
        validateSigninManager(addForm);
        validateActivityParticipateType(addForm);
        validateActivityManager(addForm);
    }
    //校验活动时间相关
    private void validateActivitySchedule(ActivityWithScheduleAddForm addForm){
        //报名开始时间《报名结束时间《活动开始时间《活动结束时间《签到开始时间《签到结束时间
        if(!(
                addForm.getEnrollStartTime().isBefore(addForm.getEnrollEndTime())
                &&addForm.getEnrollEndTime().isBefore(addForm.getActivityStartTime())
                &&addForm.getActivityStartTime().isBefore(addForm.getActivityEndTime())
                &&addForm.getActivityEndTime().isBefore(addForm.getSigninStartTime())
                &&addForm.getSigninStartTime().isBefore(addForm.getSigninEndTime())
            )
        ){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动时间不按顺序");
        }
        //如果选了需要签退
        if(addForm.isNeedSignOut()){
            if(addForm.getSignoutStartTime()==null||addForm.getSignoutEndTime()==null){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"选了需要签退但是签退时间为空");
            }
        }

        if (!(addForm.getSignoutEndTime().isAfter(addForm.getSignoutStartTime())
                &&addForm.getSignoutStartTime().isAfter(addForm.getSigninEndTime()))){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动时间不按顺序");
        }

        if((addForm.getCanEnrollGradeIdList()==null&&addForm.getCanEnrollCollegeIdList()!=null)||
                addForm.getCanEnrollGradeIdList()!=null&&addForm.getCanEnrollCollegeIdList()==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"表单规定必须同时为空或不为空");
        }
        if((addForm.getCanEnrollCollegeIdList()==null&&addForm.getCanEnrollTribeIdList()==null)||
                addForm.getCanEnrollCollegeIdList()!=null&&addForm.getCanEnrollTribeIdList()!=null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"表单规定不能同时为空或同时不为空");
        }
    }

    private void validatePortalUser(){
        this.portalUser = portalUserManager.getOptById(userId)
                .filter(portalUserEntity -> !portalUserEntity.getDeletedFlag())
                .filter(portalUserEntity -> !portalUserEntity.getDisableFlag())
                .orElseThrow(()->new BusinessException(UserErrorCode.USER_STATUS_ERROR));
    }
    //检查用户是否有发布活动的权限
    private void validatePortalUserCanPublishActivity(){
        if (!portalUser.isCanPublishActivity()) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION);
        }
    }

    private void validateActivityBelongTo(ActivityWithScheduleAddForm addForm){
        //activityBelongToCollegeId和activityBelongToOrganizationId不能同时为空或同时不为空
        if((addForm.getActivityBelongToCollegeId() != null && addForm.getActivityBelongToOrganizationId() != null)||
                (addForm.getActivityBelongToCollegeId() == null && addForm.getActivityBelongToOrganizationId() == null)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"activityBelongToCollegeId和activityBelongToOrganizationId不能同时为空或同时不为空");
        }
        //检查活动所属学校是否存在
        schoolInfoManager.getOptById(addForm.getActivityBelongToSchoolId())
                .filter((school)->!school.getDeletedFlag())
                .filter(school->school.getId().equals(portalUser.getSchoolId()))
                .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"活动所属学校不存在"));

        //如果活动所属学院字段不为null,则说明是学院活动
        //检查学院是否存在,以及与表单中的活动所属学院id一致
        if(addForm.getActivityBelongToCollegeId() != null){
            collegeInfoManager.getOptById(addForm.getActivityBelongToCollegeId())
                    .filter((college)->!college.getDeletedFlag())
                    .filter((college)->college.getSchoolId().equals(portalUser.getSchoolId()))
                    .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"添加活动传入的collegeId为错误信息"));;
        }
        //如果活动所属组织字段不为null,则说明是组织活动
        //检查组织是否存在,以及与表单中的活动所属组织id一致
        if(addForm.getActivityBelongToOrganizationId() != null){
            organizationInfoManager.getOptById(addForm.getActivityBelongToOrganizationId())
                    .filter((org)->!org.getDeletedFlag())
                    .filter((org)->org.getSchoolId().equals(portalUser.getSchoolId()))
                    .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"organization不存在或organization的school与其他数据不一致"));
        }
    }

    private void validateActivityCategory(ActivityWithScheduleAddForm addForm){
        activityCategoryManager.getOptById(addForm.getCategoryId())
                .filter((cate)->!cate.getDeletedFlag())
                .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"活动分类不存在"));

    }

    private void validateActivityParticipateType(ActivityWithScheduleAddForm addForm){
        //不能提交空列表
        if(addForm.getCanEnrollTribeIdList().isEmpty()
                &&addForm.getCanEnrollGradeIdList().isEmpty()
                &&addForm.getCanEnrollCollegeIdList().isEmpty()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        //如果院系年级不为空，为按院系年级进行参与
        //检查输入的院系年级是否存在
        if(addForm.getCanEnrollGradeIdList()!=null && !addForm.getCanEnrollGradeIdList().isEmpty()){
            if(gradeInfoManager
                    .getBaseMapper()
                    .selectByIds(addForm.getCanEnrollGradeIdList())
                    .stream().filter(e->!e.getDeletedFlag())
                    .count()
                    != addForm.getCanEnrollGradeIdList().size()){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"提交的年级里有年级不存在");
            }
        }

        if(addForm.getCanEnrollCollegeIdList()!=null && !addForm.getCanEnrollCollegeIdList().isEmpty()){
            if(collegeInfoManager.getBaseMapper()
                    .selectByIds(addForm.getCanEnrollCollegeIdList())
                    .stream().filter(e->!e.getDeletedFlag())
                    .count()
                    != addForm.getCanEnrollCollegeIdList().size()
            ){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"提交的学院里有学院不存在");
            }
        }


        //如果部落不为空，为按部落进行参与
        //检查输入的部落是否存在
        if(addForm.getCanEnrollTribeIdList()!=null && !addForm.getCanEnrollTribeIdList().isEmpty()){
            if(tribeManager
                    .getBaseMapper()
                    .selectByIds(addForm.getCanEnrollTribeIdList())
                    .stream().filter(e->!e.getDeletedFlag())
                    .count()!=addForm.getCanEnrollTribeIdList().size()){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"提交的年级里有部落不存在");
            }
        }
    }

    private void validateActivityTitleUnique(ActivityWithScheduleAddForm addForm){
        //活动中不能有和添加活动标题一致的
        Optional.ofNullable(
                        activityManager.getOne(
                                new LambdaQueryWrapper<ActivityEntity>()
                                        .eq(ActivityEntity::getTitle,addForm.getTitle())
                        )
                ).filter(e->!e.getDeletedFlag())
                .ifPresent((e)->{
                            throw new BusinessException(UserErrorCode.PARAM_ERROR,"包含此标题的活动已存在");
                        }
                );
    }
    //返回username，构建插入内容时要用
    private void validateInitialReviewer(ActivityWithScheduleAddForm addForm){
        //检查initialReviewer是否存在且有效
        BackendUserEntity initialReviewer=Optional.ofNullable(backendUserManager.getById(addForm.getInitialReviewer()))
                .filter(e->!e.getDeletedFlag())
                .filter(e->!e.getDisabledFlag())
                .filter(e->e.getSchoolId().equals(portalUser.getSchoolId()))
                .orElseThrow(()->new BusinessException(UserErrorCode.PARAM_ERROR,"活动初审人校验不正确"));
        validateInitialReviewerName(addForm,initialReviewer);
    }

    private void validateInitialReviewerName(ActivityWithScheduleAddForm addForm,BackendUserEntity backendUser){
        if(!addForm.getInitialReviewerName().equals(backendUser.getUsername())){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"传入的初审人用户名与数据库内容不相符");
        }
    }

    private void validateActivityManager(ActivityWithScheduleAddForm addForm){
        if(!Objects.equals(userId, addForm.getActivityManagerId())) {
            //检查activityManager是否存在且有效
            Optional.ofNullable(portalUserManager.getById(addForm.getActivityManagerId()))
                    .filter(e -> !e.getDeletedFlag())
                    .filter(e -> !e.getDisableFlag())
                    .filter(e -> e.getSchoolId().equals(addForm.getActivityBelongToSchoolId()))
                    .orElseThrow(() -> new BusinessException(UserErrorCode.PARAM_ERROR,"活动管理员校验不正确"));
        }
    }

    private void validateSigninManager(ActivityWithScheduleAddForm addForm){
        List<PortalUserEntity> filteredPortalUserList=portalUserManager.listByIds(addForm.getActivitySigninManagerIdList())
                .stream()
                .filter(e->!e.getDisableFlag())
                .filter(e->!e.getDeletedFlag())
                .filter(e->e.getSchoolId().equals(portalUser.getSchoolId()))
                .toList();
        if(filteredPortalUserList.size()!=addForm.getActivitySigninManagerIdList().size()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签到员id不合法");
        }
        if(addForm.getCanEnrollTribeIdList()!=null
                &&tribeUserService.usersExistsInTribes(addForm.getActivitySigninManagerIdList(),addForm.getCanEnrollTribeIdList())){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签到员id不合法");
        }

        if(addForm.getCanEnrollCollegeIdList()!=null){
            if(filteredPortalUserList.stream().filter(e->addForm.getCanEnrollCollegeIdList().contains(e.getCollegeId()))
                    .filter(e->addForm.getCanEnrollGradeIdList().contains(e.getGradeId()))
                    .count()!=addForm.getActivitySigninManagerIdList().size()){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动签到员id不合法");
            }
        }
    }


}
