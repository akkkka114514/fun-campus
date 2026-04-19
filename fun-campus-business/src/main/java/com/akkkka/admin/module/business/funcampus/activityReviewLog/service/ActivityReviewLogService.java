package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cn.hutool.core.lang.Assert;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentValidator;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.common.code.ErrorCode;
import com.akkkka.common.code.SystemErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Nullable;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.dao.ActivityReviewLogDao;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.dto.EnrollersChangeDTO;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogQueryForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.vo.ActivityReviewLogVO;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.manager.ActivitySigninManagerManager;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akkkka.common.util.SmartRequestUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.support.TransactionTemplate;

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

    
    private ActivityReviewLogDao activityReviewLogDao;

    
    private TransactionTemplate transactionTemplate;

    
    private ActivityWithScheduleService activityWithScheduleService;

    
    private ActivityReviewLogManager activityReviewLogManager;

    
    private ActivityManager activityManager;

    
    private ActivityScheduleManager activityScheduleManager;

    
    private ActivityEnrollmentManager enrollmentManager;

    
    private ActivitySigninManagerService  signinManagerService;

    
    private ActivitySigninManagerManager signinManagerManager;

    
    private ActivityReviewLogValidator addFormValidator;

    
    private PortalUserManager portalUserManager;

    
    private ActivityEnrollmentService enrollmentService;

    private PortalUserValidator portalUserValidator;
    private ActivityEnrollmentValidator enrollmentValidator;

    /**
     * 分页查询
     */
    public PageResult<ActivityReviewLogVO> queryPage(ActivityReviewLogQueryForm queryForm) {
        log.info("ActivityReviewLogService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityReviewLogVO> list = activityReviewLogDao.queryPage(page, queryForm);
        log.info("ActivityReviewLogService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityReviewLogAddForm addForm) {
        log.info("ActivityReviewLogService.add called, addForm={}", addForm);
        ActivityReviewLogEntity activityReviewLogEntity = SmartBeanUtil.copy(addForm, ActivityReviewLogEntity.class);
        int result = activityReviewLogDao.insert(activityReviewLogEntity);
        if (result > 0) {
            log.info("ActivityReviewLogService.add success: new record created with id={}", activityReviewLogEntity.getId());
        } else {
            log.error("ActivityReviewLogService.add failed: no record created");
        }
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityReviewLogUpdateForm updateForm) {
        log.info("ActivityReviewLogService.update called, updateForm={}", updateForm);
        ActivityReviewLogEntity activityReviewLogEntity = SmartBeanUtil.copy(updateForm, ActivityReviewLogEntity.class);
        int result = activityReviewLogDao.updateById(activityReviewLogEntity);
        if (result > 0) {
            log.info("ActivityReviewLogService.update success: record updated with id={}", updateForm.getId());
        } else {
            log.warn("ActivityReviewLogService.update warning: no records updated");
        }
        return ResponseDTO.ok();
    }

    public void submitDraft()
    //初审和终审有驳回权限，驳回后需从头再走一遍审核流程
    public void doInitialReview(@Nullable ActivityWithScheduleUpdateForm updateForm, ActivityReviewLogAddForm addForm){
        ActivityReviewLogEntity nextReview = new ActivityReviewLogEntity();
        nextReview.setActivityId(addForm.getActivityId());
        nextReview.setReviewerId(addForm.getNextReviewerId());
        nextReview.setReviewerName(addForm.getNextReviewerName());
        nextReview.setReviewStage(ActivityReviewStage.CHECK);
        nextReview.setCreateTime(LocalDateTime.now());
        nextReview.setDeletedFlag(false);

        LambdaUpdateWrapper<ActivityReviewLogEntity> update = new LambdaUpdateWrapper<>();
        update.eq(ActivityReviewLogEntity::getActivityId,addForm.getActivityId())
                .eq(ActivityReviewLogEntity::getReviewerId,addForm.getReviewerId())
                .set(ActivityReviewLogEntity::getAction,addForm.getAction())
                .set(ActivityReviewLogEntity::getRejectReason,addForm.getRejectReason());

        transactionTemplate.executeWithoutResult(status->{
            try {
                if(updateForm!=null){
                    activityWithScheduleService.updateActivityWithSchedule(updateForm);
                }
                if(!activityReviewLogManager.update(update)){
                    log.warn("活动初审结果提交操作中，填写审核结果失败，事务回滚，addform:{}",addForm);
                    status.setRollbackOnly();
                }
                if(!activityReviewLogManager.save(nextReview)){
                    log.warn("活动初审结果提交操作中，初始化下一阶段审核失败，事务回滚，addform：{}",addForm);
                }
            }catch (Exception e){
                log.warn("活动初审结果提交操作中，事务回滚：{},{},{},{}",addForm,e.getMessage(),e.getCause(),e.getStackTrace());
                status.setRollbackOnly();
            }
        });
    }
    /*
    让审核中的活动取消审核，具体操作是删除当前该活动所有审核阶段的记录。
    审核中的活动前台不能进行修改，取消审核后前台用户可以进行修改
     */
    public void cancelReview(Long activityId){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if(requestUser instanceof RequestBackendUser){
            throw new BusinessException(UserErrorCode.NO_PERMISSION,"后台用户没有取消审核的权限");
        }

        ActivityEntity activity = activityManager.getById(activityId);
        if(activity==null||activity.getDeletedFlag()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"该活动不存在或已删除");
        }

        if(!activity.getActivityManagerId().equals(SmartRequestUtil.getRequestUserId())){
            throw new BusinessException(UserErrorCode.NO_PERMISSION,"只有活动发起者才有权限取消活动审核");
        }
        LambdaUpdateWrapper<ActivityReviewLogEntity> luw = new LambdaUpdateWrapper<>();
        luw.eq(ActivityReviewLogEntity::getActivityId,activityId)
            .eq(ActivityReviewLogEntity::getDeletedFlag,false)
            .set(ActivityReviewLogEntity::getDeletedFlag,true);

        activityReviewLogManager.update(luw);
    }

    /*
    审阅操作
     */
    public void check(ActivityReviewLogAddForm addForm){
        ActivityReviewLogEntity nextReview = new ActivityReviewLogEntity();
        nextReview.setActivityId(addForm.getActivityId());
        nextReview.setReviewerId(addForm.getNextReviewerId());
        nextReview.setReviewerName(addForm.getNextReviewerName());
        nextReview.setReviewStage(ActivityReviewStage.FINAL_REVIEW);
        nextReview.setCreateTime(LocalDateTime.now());
        nextReview.setDeletedFlag(false);

        LambdaUpdateWrapper<ActivityReviewLogEntity> luw = new LambdaUpdateWrapper<>();
        luw.eq(ActivityReviewLogEntity::getActivityId,addForm.getActivityId())
                .eq(ActivityReviewLogEntity::getReviewStage,ActivityReviewStage.CHECK)
                .set(ActivityReviewLogEntity::getAction,ActivityReviewStage.CHECK)
                .set(ActivityReviewLogEntity::getCheckRemark,addForm.getCheckRemark());
    }

    public void finalReview(@Nullable ActivityWithScheduleUpdateForm updateForm, ActivityReviewLogAddForm addForm){
        validateAllTimeFuture(addForm.getActivityId());
        ActivityEntity activity = activityManager.getById(addForm.getActivityId());
        assert activity!=null;
        ActivityReviewLogEntity nextReview = new ActivityReviewLogEntity();

        nextReview.setActivityId(addForm.getActivityId());
        nextReview.setReviewerId(addForm.getNextReviewerId());
        nextReview.setReviewerName(addForm.getNextReviewerName());
        nextReview.setCreateTime(LocalDateTime.now());
        nextReview.setDeletedFlag(false);
        if(updateForm!=null){
            //update form指定要改
            if(updateForm.getEnrollNeedReview()!=null) {
                if (updateForm.getEnrollNeedReview()) {
                    nextReview.setReviewStage(ActivityReviewStage.ENROLL_REVIEW);
                }else{
                    nextReview.setReviewStage(ActivityReviewStage.END_REVIEW);
                }
            }
        }else{
            //update form不该，数据库是报名是审核
            if(activity.isEnrollNeedReview()){
                nextReview.setReviewStage(ActivityReviewStage.ENROLL_REVIEW);
            }else {
                nextReview.setReviewStage(ActivityReviewStage.END_REVIEW);
            }
        }

        LambdaUpdateWrapper<ActivityReviewLogEntity> update = new LambdaUpdateWrapper<>();
        update.eq(ActivityReviewLogEntity::getActivityId,addForm.getActivityId())
                .eq(ActivityReviewLogEntity::getReviewerId,addForm.getReviewerId())
                .set(ActivityReviewLogEntity::getAction,addForm.getAction())
                .set(ActivityReviewLogEntity::getRejectReason,addForm.getRejectReason());
        //活动管理员和签到员一开始就已签到和签退
        List<ActivityEnrollmentEntity> preSignin;
        if(updateForm!=null){
            preSignin = buildPreSignInAndSignOutEntities(updateForm,addForm);
        }
        preSignin=buildPreSignInAndSignOutEntities(addForm);

        List<ActivityEnrollmentEntity> finalPreSignin = preSignin;
        transactionTemplate.executeWithoutResult(status->{
            try {
                if(updateForm!=null){
                    activityWithScheduleService.updateActivityWithSchedule(updateForm);
                }
                if(!activityReviewLogManager.update(update)){
                    log.warn("活动终审结果提交操作中，填写审核结果失败，事务回滚，addform:{}",addForm);
                    status.setRollbackOnly();
                }
                if(!activityReviewLogManager.save(nextReview)){
                    log.warn("活动终审结果提交操作中，初始化下一阶段审核失败，事务回滚，addform：{}",addForm);
                    status.setRollbackOnly();
                }
                if(!enrollmentManager.saveBatch(finalPreSignin)){
                    log.warn("活动终审结果提交操作中，活动管理员和签到员提前签到失败，事务回滚，addform：{}",addForm);
                    status.setRollbackOnly();
                }
            }catch (Exception e){
                log.warn("活动终审结果提交操作中，事务回滚：{},{},{},{}",addForm,e.getMessage(),e.getCause(),e.getStackTrace());
                status.setRollbackOnly();
            }
        });
    }

    private void validateAllTimeFuture(Long activityId){
        ActivityScheduleEntity activitySchedule = activityScheduleManager.getById(activityId);
        LocalDateTime now = LocalDateTime.now();
        if(activitySchedule.getEnrollStartTime().isBefore(now)||activitySchedule.getEnrollEndTime().isBefore(now)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"报名时间已经开始或已经结束，请重新设置活动时间表");
        }
    }

    //适用于终审updateForm不为null的情况
    private List<ActivityEnrollmentEntity> buildPreSignInAndSignOutEntities(
            ActivityWithScheduleUpdateForm updateForm,
            ActivityReviewLogAddForm addForm){
        ActivityEnrollmentEntity managerEnrollment = new ActivityEnrollmentEntity();
        List<ActivityEnrollmentEntity> result = new ArrayList<>();

        ActivityEntity activity = activityManager.getById(addForm.getActivityId());
        if(updateForm.getActivitySigninManagerIdList()!=null&&!updateForm.getActivitySigninManagerIdList().isEmpty()) {
            updateForm.getActivitySigninManagerIdList().forEach(
                    e -> {
                        ActivityEnrollmentEntity enrollment = new ActivityEnrollmentEntity();
                        enrollment.setActivityId(updateForm.getId());
                        enrollment.setUserId(e);
                        enrollment.setSignInStatus(true);
                        enrollment.setCreateTime(LocalDateTime.now());
                        enrollment.setUpdateTime(LocalDateTime.now());
                        enrollment.setDeletedFlag(false);
                        if(updateForm.getNeedSignOut()==null){
                            if (activity.isNeedSignOut()) {
                                enrollment.setSignOutStatus(true);
                            }
                        }else if(updateForm.getNeedSignOut()){
                            enrollment.setSignOutStatus(true);
                        }

                    }
            );
        }
        if(updateForm.getActivityManagerId()!=null){
            managerEnrollment.setActivityId(addForm.getActivityId());
            managerEnrollment.setUserId(updateForm.getActivityManagerId());
            managerEnrollment.setSignInStatus(true);
            managerEnrollment.setCreateTime(LocalDateTime.now());
            managerEnrollment.setUpdateTime(LocalDateTime.now());
            managerEnrollment.setDeletedFlag(false);
            if(updateForm.getNeedSignOut()==null){
                if (activity.isNeedSignOut()) {
                    managerEnrollment.setSignOutStatus(true);
                }
            }else if(updateForm.getNeedSignOut()){
                managerEnrollment.setSignOutStatus(true);
            }
            result.add(managerEnrollment);
            return result;
        }
        managerEnrollment.setActivityId(addForm.getActivityId());
        managerEnrollment.setUserId(activity.getActivityManagerId());
        managerEnrollment.setSignInStatus(true);
        managerEnrollment.setCreateTime(LocalDateTime.now());
        managerEnrollment.setUpdateTime(LocalDateTime.now());
        managerEnrollment.setDeletedFlag(false);
        if(updateForm.getNeedSignOut()==null){
            if (activity.isNeedSignOut()) {
                managerEnrollment.setSignOutStatus(true);
            }
        }else if(updateForm.getNeedSignOut()){
            managerEnrollment.setSignOutStatus(true);
        }
        result.add(managerEnrollment);
        return result;

    }
    //适用于终审updateForm为null的情况
    private List<ActivityEnrollmentEntity> buildPreSignInAndSignOutEntities(
            ActivityReviewLogAddForm addForm) {
        ActivityEnrollmentEntity managerEnrollment = new ActivityEnrollmentEntity();
        List<ActivityEnrollmentEntity> result = new ArrayList<>();

        ActivityEntity activity = activityManager.getById(addForm.getActivityId());

        List<ActivitySigninManagerEntity> signinManagerEntities = signinManagerManager.list(
                ActivitySigninManagerService
                        .listByActivityIdQw(activity.getId())
                        .select(ActivitySigninManagerEntity::getPortalUserId)
        );
        if (signinManagerEntities != null && !signinManagerEntities.isEmpty()) {
            signinManagerEntities.forEach(e -> {
                ActivityEnrollmentEntity enrollment = new ActivityEnrollmentEntity();
                enrollment.setActivityId(activity.getId());
                enrollment.setUserId(e.getPortalUserId());
                enrollment.setSignInStatus(true);
                enrollment.setCreateTime(LocalDateTime.now());
                enrollment.setUpdateTime(LocalDateTime.now());
                enrollment.setDeletedFlag(false);
                if (activity.isNeedSignOut()) {
                    enrollment.setSignOutStatus(true);
                }
                result.add(enrollment);
            });
        }

        managerEnrollment.setActivityId(addForm.getActivityId());
        managerEnrollment.setUserId(activity.getActivityManagerId());
        managerEnrollment.setSignInStatus(true);
        managerEnrollment.setCreateTime(LocalDateTime.now());
        managerEnrollment.setUpdateTime(LocalDateTime.now());
        managerEnrollment.setDeletedFlag(false);
        if (activity.isNeedSignOut()) {
            managerEnrollment.setSignOutStatus(true);
        }
        result.add(managerEnrollment);
        return result;
    }

    //审核报名用户，对用户进行筛选
    public void reviewEnroll(ActivityReviewLogAddForm addForm,@Nullable List<Long> enrollerIds){
        addFormValidator.validate(addForm);
        EnrollersChangeDTO enrollersChangeDTO = new EnrollersChangeDTO();
        if(enrollerIds!=null && !enrollerIds.isEmpty()){
            List<PortalUserEntity> portalUsers = portalUserManager.listByIds(enrollerIds);
            if(portalUsers.size()!=enrollerIds.size()){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"审核要更改的报名人员异常，请仔细核对");
            }
            //如果活动管理员与签到员变动会在convertor体现出来，会有不好的后果。要保证managers没有变动
            Long activityManagerId = activityManager.getById(addForm.getActivityId()).getActivityManagerId();
            if(!enrollerIds.contains(activityManagerId)){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"报名审核不能更改活动管理员和签到员");
            }

            List<ActivitySigninManagerEntity> signinManagerlist = signinManagerManager.list(
                    ActivitySigninManagerService.listByActivityIdQw(addForm.getActivityId())
                        .select(ActivitySigninManagerEntity::getPortalUserId)
            );
            if(!new HashSet<>(enrollerIds).containsAll(
                    signinManagerlist
                        .stream()
                        .map(ActivitySigninManagerEntity::getPortalUserId)
                        .toList())
            ){
                throw new BusinessException(UserErrorCode.PARAM_ERROR,"报名审核不能更改活动管理员和签到员");
            }


            List<ActivityEnrollmentEntity> activityEnrollmentUserIds = enrollmentManager.list(
                    ActivityEnrollmentService.listByActivityIdQw(addForm.getActivityId())
                            .select(ActivityEnrollmentEntity::getUserId)
            );

            assert activityEnrollmentUserIds!=null;
            assert !activityEnrollmentUserIds.isEmpty();

            enrollersChangeDTO = enrollmentService.convertToEnrollmentChanges(enrollerIds, addForm.getActivityId());
        }
        EnrollersChangeDTO finalEnrollersChangeDTO = enrollersChangeDTO;

        ActivityReviewLogEntity nextReviewLog = new ActivityReviewLogEntity();
        nextReviewLog.setActivityId(addForm.getActivityId());
        nextReviewLog.setReviewerId(addForm.getReviewerId());
        nextReviewLog.setReviewerName(addForm.getReviewerName());
        nextReviewLog.setReviewStage(ActivityReviewStage.END_REVIEW);
        nextReviewLog.setCreateTime(LocalDateTime.now());
        nextReviewLog.setDeletedFlag(false);

        LambdaUpdateWrapper<ActivityReviewLogEntity> uw = new LambdaUpdateWrapper<>();
        uw.eq(ActivityReviewLogEntity::getActivityId,addForm.getActivityId())
                        .eq(ActivityReviewLogEntity::getReviewStage,ActivityReviewStage.ENROLL_REVIEW)
                        .eq(ActivityReviewLogEntity::getDeletedFlag,false)
                        .set(ActivityReviewLogEntity::getAction, ActivityReviewEvent.APPROVED);
        transactionTemplate.executeWithoutResult(status->{
            try {
                assert finalEnrollersChangeDTO != null;
                if (!finalEnrollersChangeDTO.getDelList().isEmpty()) {
                    if (!enrollmentManager.updateBatchById(finalEnrollersChangeDTO.getDelList())) {
                        log.warn("活动报名审核，批量删除报名人员失败回滚，activityId:{},userId:{}"
                                , addForm.getActivityId(), finalEnrollersChangeDTO.getDelList());
                        status.setRollbackOnly();
                    }
                }
                if (!finalEnrollersChangeDTO.getAddList().isEmpty()) {
                    if (!enrollmentManager.saveBatch(finalEnrollersChangeDTO.getAddList())) {
                        log.warn("活动报名审核，批量添加报名人员失败回滚，activityId:{},userId:{}"
                                , addForm.getActivityId(), finalEnrollersChangeDTO.getAddList());
                        status.setRollbackOnly();
                    }
                }
                if (!activityReviewLogManager.save(nextReviewLog)) {
                    log.warn("活动报名审核，插入活动审核日志失败，activityId:{},reviewAddForm:{}"
                            , addForm.getActivityId(), addForm);
                    status.setRollbackOnly();
                }
                if (!activityReviewLogManager.update(uw)) {
                    log.warn("活动报名审核，通过报名审核失败回滚，activityId:{}"
                            , addForm.getActivityId());
                    status.setRollbackOnly();
                }
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
}
