package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentValidator;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.dto.EnrollersChangeDTO;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogQueryForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.vo.ActivityReviewLogVO;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.manager.ActivitySigninManagerManager;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityScheduleUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.service.BackendUserValidator;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.IdNameVO;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartPageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.lang.Assert;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 活动审核日志 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-10 18:34:56
 * @Copyright akkkka114514
 */

@Slf4j
@Service
@AllArgsConstructor
public class ActivityReviewLogService {
    private final TransactionTemplate transactionTemplate;
    private final ActivityWithScheduleService activityWithScheduleService;
    private final ActivityReviewLogManager activityReviewLogManager;
    private final ActivityManager activityManager;
    private final ActivityScheduleManager activityScheduleManager;
    private final ActivityEnrollmentManager enrollmentManager;
    private final ActivitySigninManagerManager signinManagerManager;
    private final ActivityReviewLogValidator addFormValidator;
    private final PortalUserManager portalUserManager;
    private final ActivityEnrollmentService enrollmentService;
    private final ActivityEnrollmentValidator enrollmentValidator;
    private final ActivityValidator activityValidator;
    private final BackendUserValidator backendUserValidator;
    private final ActivityReviewLogValidator reviewLogValidator;
    private final ActivitySigninManagerService signinManagerService;
    private final PortalUserValidator portalUserValidator;

    public PageResult<ActivityReviewLogVO> queryPage(ActivityReviewLogQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityReviewLogVO> list = activityReviewLogManager.getBaseMapper().queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    public ResponseDTO<String> add(ActivityReviewLogAddForm addForm) {
        reviewLogValidator.validate(addForm);
        ActivityReviewLogEntity entity = ActivityReviewLogAddForm.convert(addForm);
        activityReviewLogManager.save(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> update(ActivityReviewLogUpdateForm updateForm) {
        ActivityReviewLogEntity entity = ActivityReviewLogUpdateForm.convert(updateForm);
        activityReviewLogManager.updateById(entity);
        return ResponseDTO.ok();
    }

    
    /**
     * 获取活动最新的未删除审核日志
     */
    public ActivityReviewLogEntity getLatestReviewLog(Long activityId) {
        return activityReviewLogManager.getOne(
                Wrappers.lambdaQuery(ActivityReviewLogEntity.class)
                        .eq(ActivityReviewLogEntity::getActivityId, activityId)
                        .eq(ActivityReviewLogEntity::getDeletedFlag, false)
                        .orderByDesc(ActivityReviewLogEntity::getCreateTime)
                        .last("limit 1")
        );
    }


    public void initialReview(@Nullable ActivityWithScheduleUpdateForm updateForm, ActivityReviewLogAddForm addForm) {
        if (updateForm != null) {
            activityWithScheduleService.editActivityDraft(addForm.getActivityId(), updateForm);
        }
        ActivityReviewLogEntity entity = ActivityReviewLogAddForm.convert(addForm);
        entity.setReviewStage(ActivityReviewStage.INITIAL_CONTENT_REVIEW);
        entity.setDeletedFlag(false);
        activityReviewLogManager.save(entity);
    }

    public IdNameVO getInitialReviewerIdNameByActivityId(Long activityId){
        ActivityReviewLogEntity entity=activityReviewLogManager.getOne(
                Wrappers.lambdaQuery(ActivityReviewLogEntity.class)
                        .eq(ActivityReviewLogEntity::getActivityId,activityId)
                        .eq(ActivityReviewLogEntity::getReviewStage,ActivityReviewStage.INITIAL_CONTENT_REVIEW)
                        .eq(ActivityReviewLogEntity::getDeletedFlag,false)
                        .select(ActivityReviewLogEntity::getReviewerId)
                        .select(ActivityReviewLogEntity::getReviewerName)
        );
        IdNameVO idNameVO = new IdNameVO();
        idNameVO.setId(entity.getReviewerId());
        idNameVO.setName(entity.getReviewerName());
        return idNameVO;
    }

    //每次审核必经过的审核本体的
    private void doReviewTransaction(ActivityReviewLogUpdateForm currentReview,
                                    ActivityReviewLogAddForm nextReview){
        ActivityReviewLogEntity curReviewEntity=reviewLogValidator.validateReviewLogId(currentReview.getId());
        reviewLogValidator.validateCurrentReviewStage(curReviewEntity.getActivityId(), curReviewEntity.getReviewStage());
        reviewLogValidator.validateNextReviewStage(curReviewEntity.getActivityId(),nextReview.getReviewStage());
        reviewLogValidator.validateReviewAction(curReviewEntity.getReviewStage(),currentReview.getAction());
        LambdaUpdateWrapper<ActivityReviewLogEntity> uw = new LambdaUpdateWrapper<>(ActivityReviewLogUpdateForm.convert(currentReview));


        if(!Objects.equals(curReviewEntity.getReviewStage(),
                ActivityReviewStage.COMPLETION_REVIEW)){
            activityValidator.validateActivityId(nextReview.getActivityId());
            BackendUserEntity nextReviewer = backendUserValidator.validateBackendUserId(nextReview.getReviewerId());
            reviewLogValidator.validateReviewerName(nextReview.getReviewerName(),nextReviewer);
            reviewLogValidator.validateReviewPermission(nextReviewer);
        }
        ActivityReviewLogEntity toAdd = ActivityReviewLogAddForm.convert(nextReview);
        transactionTemplate.executeWithoutResult(status -> {
            try{
                if(!activityReviewLogManager.update(uw)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
                if(!activityReviewLogManager.save(toAdd)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.warn("doReviewTransaction add和update抛出异常，事务回滚，update form={}，add form={}",
                        currentReview,nextReview,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR,e);
            }

        });

    }

    //初审和终审有驳回权限，驳回后需从头再走一遍审核流程
    public void passInitial(@Nullable ActivityWithScheduleUpdateForm updateForm,
                              ActivityReviewLogUpdateForm curReview,
                              ActivityReviewLogAddForm nextReview){
        transactionTemplate.executeWithoutResult(status->{
            try {
                if(updateForm!=null){
                    activityWithScheduleService.editActivityDraft(nextReview.getActivityId(), updateForm);
                }
                doReviewTransaction(curReview,nextReview);
            }catch (Exception e){
                log.error("活动初审结果提交操作中，事务回滚：curReview={},nextReview={}",curReview,nextReview,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
            }
        });
    }

    public void rejectInitial(ActivityReviewLogUpdateForm curReview){
        ActivityReviewLogEntity reviewLog = reviewLogValidator.validateReviewLogId(curReview.getId());
        reviewLogValidator.validateReviewAction(reviewLog.getReviewStage(),curReview.getAction());
        ActivityReviewLogEntity toUpdate = ActivityReviewLogUpdateForm.convert(curReview);
        toUpdate.setDeletedFlag(true);
        Long activityId = reviewLog.getActivityId();
        transactionTemplate.executeWithoutResult(status -> {
            try {
                if(!activityReviewLogManager.updateById(toUpdate)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
                activityWithScheduleService.deleteDraft(activityId);
            }catch (Exception e){
                log.error("rejectInitial 事务失败回，curReview={}",curReview,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR,e);
            }
        });

    }

    /*
    审阅操作
     */
    public void check(ActivityReviewLogUpdateForm currentReview,
                      ActivityReviewLogAddForm nextReview){
        currentReview.setAction(ActivityReviewEvent.CHECK_PASS);
        doReviewTransaction(currentReview,nextReview);
    }
    public void passFinalReview(ActivityReviewLogUpdateForm curReview,
                                ActivityReviewLogAddForm nextReview,
                                @Nullable ActivityWithScheduleUpdateForm updateForm){
        validateAllTimeFuture(nextReview.getActivityId(),updateForm);
        List<ActivityEnrollmentEntity> managerEnrollments = buildSignInManagerEnrollment(nextReview.getActivityId(),updateForm);
        managerEnrollments.add(buildActivityManagerEnrollment(nextReview.getActivityId(),updateForm));
        transactionTemplate.executeWithoutResult(status->{
            try {
                if(updateForm!=null){
                    activityWithScheduleService.editActivityDraft(nextReview.getActivityId(), updateForm);
                }
                AssertUtil.ifFalseThrowSysError(enrollmentManager.saveBatch(managerEnrollments));
                doReviewTransaction(curReview,nextReview);
            }catch (Exception e){
                log.error("活动终审结果提交操作中，事务回滚：curReview={},nextReview={}",curReview,nextReview,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
            }
        });
    }


    //终审被拒会返回activityId，由controller组装VO
    public Long rejectFinal(ActivityReviewLogUpdateForm curReview){
        ActivityReviewLogEntity reviewLog = reviewLogValidator.validateReviewLogId(curReview.getId());
        reviewLogValidator.validateReviewAction(reviewLog.getReviewStage(),curReview.getAction());
        ActivityReviewLogEntity toUpdate = ActivityReviewLogUpdateForm.convert(curReview);
        toUpdate.setDeletedFlag(true);
        Long activityId = reviewLog.getActivityId();
        transactionTemplate.executeWithoutResult(status -> {
            try {
                if(!activityReviewLogManager.updateById(toUpdate)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
                activityWithScheduleService.deleteDraft(activityId);
            }catch (Exception e){
                log.error("rejectFinal 事务失败回，curReview={}",curReview,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR,e);
            }
        });

        return reviewLog.getActivityId();
    }

    private void validateAllTimeFuture(Long activityId, ActivityWithScheduleUpdateForm updateForm){
        LocalDateTime now = LocalDateTime.now();

        //update表单中的时间表合规
        if(Objects.nonNull(updateForm) && Objects.nonNull(updateForm.getActivityScheduleUpdateForm())){
            ActivityScheduleUpdateForm scheduleForm = updateForm.getActivityScheduleUpdateForm();
            if(scheduleForm.getEnrollStartTime().isAfter(now)&&scheduleForm.getEnrollEndTime().isAfter(now)){
                return;
            }
        }
        //检查数据库中的时间表
        ActivityScheduleEntity activitySchedule = activityScheduleManager.getById(activityId);

        if(activitySchedule.getEnrollStartTime().isBefore(now)||activitySchedule.getEnrollEndTime().isBefore(now)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"报名时间已经开始或已经结束，请重新设置活动时间表");
        }
    }

    private List<ActivityEnrollmentEntity> buildSignInManagerEnrollment(Long activityId, ActivityWithScheduleUpdateForm updateForm) {
        ActivityEntity dbActivity = activityManager.getById(activityId);

        // 获取数据库中现有的签到员ID列表
        List<Long> dbSignInManagerIds = signinManagerService.getSignInManagerIds(activityId);

        // 确定需要处理的签到员ID列表
        List<Long> targetSignInManagerIds = determineTargetSignInManagerIds(updateForm, dbSignInManagerIds);

        // 确定是否需要签退
        boolean needSignOut = determineNeedSignOut(updateForm, dbActivity);

        // 构建报名记录列表
        return buildEnrollmentList(activityId, targetSignInManagerIds, needSignOut);
    }

    private ActivityEnrollmentEntity buildActivityManagerEnrollment(Long activityId, ActivityWithScheduleUpdateForm updateForm){
        ActivityEntity dbActivity = activityManager.getById(activityId);
        Long activityManagerId = determineTargetActivityManagerId(updateForm, dbActivity.getActivityManagerId());
        boolean needSignOut = determineNeedSignOut(updateForm,dbActivity);
        ActivityEnrollmentEntity enrollment = buildEnrollment(activityId,activityManagerId);
        enrollment.setSignOutStatus(needSignOut);
        return enrollment;

    }

    private Long determineTargetActivityManagerId(ActivityWithScheduleUpdateForm updateForm,Long dbActivityManagerId){
        if(Objects.nonNull(updateForm) &&
            Objects.nonNull(updateForm.getActivityUpdateForm())&&
            Objects.nonNull(updateForm.getActivityUpdateForm().getActivityManagerId())){
            return updateForm.getActivityUpdateForm().getActivityManagerId();
        }
        return dbActivityManagerId;
    }

    /**
     * 确定目标签到员ID列表
     */
    private List<Long> determineTargetSignInManagerIds(ActivityWithScheduleUpdateForm updateForm, List<Long> dbSignInManagerIds) {
        if (Objects.nonNull(updateForm) && Objects.nonNull(updateForm.getActivitySigninManagerIdList())
                && !updateForm.getActivitySigninManagerIdList().isEmpty()) {
            return updateForm.getActivitySigninManagerIdList();
        }
        return dbSignInManagerIds;
    }

    /**
     * 确定是否需要签退
     */
    private boolean determineNeedSignOut(ActivityWithScheduleUpdateForm updateForm, ActivityEntity dbActivity) {
        if (Objects.nonNull(updateForm)
                && Objects.nonNull(updateForm.getActivityUpdateForm())
                && Objects.nonNull(updateForm.getActivityUpdateForm().getNeedSignOut())) {
            return updateForm.getActivityUpdateForm().getNeedSignOut();
        }
        return dbActivity.getNeedSignOut();
    }

    /**
     * 构建报名记录列表
     */
    private List<ActivityEnrollmentEntity> buildEnrollmentList(Long activityId, List<Long> userIds, boolean needSignOut) {
        List<ActivityEnrollmentEntity> result = new LinkedList<>();
        for (Long userId : userIds) {
            ActivityEnrollmentEntity enrollment = buildEnrollment(activityId, userId);
            if (needSignOut) {
                enrollment.setSignOutStatus(true);
            }
            result.add(enrollment);
        }
        return result;
    }

    private ActivityEnrollmentEntity buildEnrollment(Long activityId,Long userId){
        ActivityEnrollmentEntity enrollment = new ActivityEnrollmentEntity();
        enrollment.setActivityId(activityId);
        enrollment.setUserId(userId);
        enrollment.setSignInStatus(true);
        enrollment.setCreateTime(LocalDateTime.now());
        enrollment.setUpdateTime(LocalDateTime.now());
        enrollment.setDeletedFlag(false);

        return enrollment;
    }


    //审核报名用户，对用户进行筛选
    public void reviewEnroll(ActivityReviewLogUpdateForm currentReview,
                             ActivityReviewLogAddForm nextReview,
                             @Nullable List<Long> enrollerIds){


        currentReview.setAction(ActivityReviewEvent.ENROLL_REVIEW_PASS);
        currentReview.setCheckRemark(null);
        currentReview.setRejectReason(null);

        EnrollersChangeDTO finalEnrollersChangeDTO = new EnrollersChangeDTO();
        if (enrollerIds != null && !enrollerIds.isEmpty()) {
            List<Long> dbEnrollerIds = enrollmentService.listPortalUserIds(nextReview.getActivityId());
            finalEnrollersChangeDTO = enrollmentService.convertToEnrollmentChanges(nextReview.getActivityId(), enrollerIds, dbEnrollerIds);
        }
        EnrollersChangeDTO changeDTO = finalEnrollersChangeDTO;

        transactionTemplate.executeWithoutResult(status->{
            try {
                if (!changeDTO.getDelList().isEmpty()) {
                    if (!enrollmentManager.updateBatchById(changeDTO.getDelList())) {
                        log.warn("活动报名审核，批量删除报名人员失败回滚，activityId:{}"
                                , nextReview.getActivityId());
                        status.setRollbackOnly();
                    }
                }
                if (!changeDTO.getAddList().isEmpty()) {
                    if (!enrollmentManager.saveBatch(changeDTO.getAddList())) {
                        log.warn("活动报名审核，批量添加报名人员失败回滚，activityId:{}"
                                , nextReview.getActivityId());
                        status.setRollbackOnly();
                    }
                }
                doReviewTransaction(currentReview,nextReview);
            }catch (Exception e){
                log.warn("活动报名审核，捕捉异常回滚：{}{}{}{}",e.getCause(),e.getStackTrace(),e.getMessage(),e.getLocalizedMessage());
                status.setRollbackOnly();
            }
        });

    }
    private enum SignInAndOut{
        SIGN_IN,
        SIGN_OUT;
    }
    private record DoubleUws(
            LambdaUpdateWrapper<ActivityEnrollmentEntity> toAdd,
            LambdaUpdateWrapper<ActivityEnrollmentEntity> toDel){}
    //TODO 要写结束活动操作，提交完结总结，相关代表性的照片或附件
    //TODO 活动超时未完结，扣减管理员信誉分
    //差异化扣分
    public void endReview(Long reviewLogId,
                          List<Long> signInUserIds,
                          List<Long> signOutUserIds,
                          Map<Long,BigDecimal> userGradeScore){
        ActivityReviewLogEntity reviewLog = activityReviewLogManager.getById(reviewLogId);
        if(reviewLog==null){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        Long activityId = reviewLog.getActivityId();
        DoubleUws signInChanges = getSignInOrOutUw(activityId,SignInAndOut.SIGN_IN,signInUserIds);
        DoubleUws signOutChanges = getSignInOrOutUw(activityId,SignInAndOut.SIGN_OUT,signOutUserIds);

        enrollmentValidator.validateEnrollmentsExist(activityId,userGradeScore.keySet().stream().toList());
        enrollmentValidator.validateEnrollmentsExist(activityId,signInUserIds);
        enrollmentValidator.validateEnrollmentsExist(activityId,signOutUserIds);

        List<PortalUserEntity> setScore = new ArrayList<>(64);
        userGradeScore.forEach((key,value)->{
            PortalUserEntity portalUser = new PortalUserEntity();
            portalUser.setId(key);
            portalUser.setGradeScore(value);
            setScore.add(portalUser);
        });

        transactionTemplate.executeWithoutResult(status -> {
            try {
                Assert.isTrue(enrollmentManager.update(signInChanges.toAdd));
                Assert.isTrue(enrollmentManager.update(signInChanges.toDel));
                Assert.isTrue(enrollmentManager.update(signOutChanges.toAdd));
                Assert.isTrue(enrollmentManager.update(signOutChanges.toDel));
                Assert.isTrue(portalUserManager.updateBatchById(setScore));
            }catch (Exception e){
                status.setRollbackOnly();
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR,"reviewLogId:"+reviewLogId+"终审事务失败："+e.getMessage(),e);
            }
        });

    }

    //找出signInUser和signoutUser中和数据库变化的部分，做成UpdateWrapper
    private DoubleUws getSignInOrOutUw(
            Long activityId,SignInAndOut signInAndOut,List<Long> signInOrOutUserIds){
        LambdaQueryWrapper<ActivityEnrollmentEntity> userQw = new LambdaQueryWrapper<>();
        if(signInAndOut.equals(SignInAndOut.SIGN_IN)){
            userQw.eq(ActivityEnrollmentEntity::getActivityId,activityId)
                    .eq(ActivityEnrollmentEntity::getDeletedFlag,false)
                    .eq(ActivityEnrollmentEntity::getSignInStatus,true)
                    .select(ActivityEnrollmentEntity::getUserId);
        }else{
            userQw.eq(ActivityEnrollmentEntity::getActivityId,activityId)
                    .eq(ActivityEnrollmentEntity::getDeletedFlag,false)
                    .eq(ActivityEnrollmentEntity::getSignOutStatus,true)
                    .select(ActivityEnrollmentEntity::getUserId);
        }

        List<ActivityEnrollmentEntity> dbUsers=enrollmentManager.list(userQw);
        assert dbUsers!=null;
        List<Long> dbUserIds = dbUsers
                .stream()
                .map(ActivityEnrollmentEntity::getUserId)
                .toList();
        //signInUser里有dbSignInUser没有的是要添加的
        List<Long> toAddUserIds = signInOrOutUserIds.stream()
                .filter(id -> !dbUserIds.contains(id))
                .toList();
        //dbSignInUser里有signInUser没有的是要添加的
        List<Long> toDeleteSignInUserIds = dbUserIds.stream()
                .filter(id->!signInOrOutUserIds.contains(id))
                .toList();
        LambdaUpdateWrapper<ActivityEnrollmentEntity> toAddUserUw=new LambdaUpdateWrapper<>();
        toAddUserUw.in(ActivityEnrollmentEntity::getUserId,toAddUserIds)
                .eq(ActivityEnrollmentEntity::getDeletedFlag,false);
        if(signInAndOut.equals(SignInAndOut.SIGN_IN)){
            toAddUserUw.set(ActivityEnrollmentEntity::getSignInStatus,true);
        }else{
            toAddUserUw.set(ActivityEnrollmentEntity::getSignOutStatus,true);
        }

        LambdaUpdateWrapper<ActivityEnrollmentEntity> toDeleteUserUw=new LambdaUpdateWrapper<>();
        toDeleteUserUw.in(ActivityEnrollmentEntity::getUserId,toDeleteSignInUserIds)
                .eq(ActivityEnrollmentEntity::getDeletedFlag,false);
        if(signInAndOut.equals(SignInAndOut.SIGN_IN)){
            toAddUserUw.set(ActivityEnrollmentEntity::getSignInStatus,false);
        }else{
            toAddUserUw.set(ActivityEnrollmentEntity::getSignOutStatus,false);
        }
        return new DoubleUws(toAddUserUw,toDeleteUserUw);
    }

    public ActivityReviewStage getCurReviewStage(Long activityId){
        ActivityReviewLogEntity reviewLog = activityReviewLogManager.getOne(
                Wrappers
                        .lambdaQuery(ActivityReviewLogEntity.class)
                        .select(ActivityReviewLogEntity::getReviewStage)
                        .eq(ActivityReviewLogEntity::getActivityId,activityId)
                        .eq(ActivityReviewLogEntity::getDeletedFlag,false)
                        .orderByDesc(ActivityReviewLogEntity::getCreateTime)
                        .last("limit 1")
        );
        if(reviewLog==null){
            return ActivityReviewStage.DRAFT;
        }
        return reviewLog.getReviewStage();
    }

    @Nullable
    public Long getCurReviewer(Long activityId){
        ActivityReviewLogEntity reviewLog = activityReviewLogManager.getOne(
                Wrappers
                        .lambdaQuery(ActivityReviewLogEntity.class)
                        .select(ActivityReviewLogEntity::getReviewStage)
                        .eq(ActivityReviewLogEntity::getActivityId,activityId)
                        .eq(ActivityReviewLogEntity::getDeletedFlag,false)
                        .orderByDesc(ActivityReviewLogEntity::getCreateTime)
                        .last("limit 1")
        );
        if(reviewLog==null){
            return null;
        }
        return reviewLog.getReviewerId();
    }
}
