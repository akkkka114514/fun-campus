package net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.beust.ah.A;
import jakarta.annotation.Nullable;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.dao.ActivityReviewLogDao;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.vo.ActivityReviewLogVO;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.manager.ActivitySigninManagerManager;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import net.lab1024.sa.admin.module.system.login.domain.RequestBackendUser;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.UserPermission;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
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
public class ActivityReviewLogService {

    @Resource
    private ActivityReviewLogDao activityReviewLogDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ActivityWithScheduleService activityWithScheduleService;

    @Resource
    private ActivityReviewLogManager activityReviewLogManager;

    @Resource
    private ActivityManager activityManager;

    @Resource
    private ActivityScheduleManager activityScheduleManager;

    @Resource
    private ActivityEnrollmentManager enrollmentManager;

    @Resource
    private ActivitySigninManagerService  signinManagerService;

    @Resource
    private ActivitySigninManagerManager signinManagerManager;

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

    public void initialReview(@Nullable ActivityWithScheduleUpdateForm updateForm, ActivityReviewLogAddForm addForm){
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
            .eq(ActivityReviewLogEntity::isDeletedFlag,false)
            .set(ActivityReviewLogEntity::isDeletedFlag,true);

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

        ActivityReviewLogEntity nextReview = new ActivityReviewLogEntity();
        nextReview.setActivityId(addForm.getActivityId());
        nextReview.setReviewerId(addForm.getNextReviewerId());
        nextReview.setReviewerName(addForm.getNextReviewerName());
        nextReview.setReviewStage(ActivityReviewStage.END_REVIEW);
        nextReview.setCreateTime(LocalDateTime.now());
        nextReview.setDeletedFlag(false);

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
}
