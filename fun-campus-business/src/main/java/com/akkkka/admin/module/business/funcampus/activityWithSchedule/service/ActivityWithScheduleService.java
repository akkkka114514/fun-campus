package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.service.ActivityCanEnrollCollegeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.service.ActivityCanEnrollGradeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.service.ActivityCanEnrollTribeService;

import com.akkkka.admin.module.business.funcampus.activityReviewAttachment.domain.entity.ActivityReviewAttachmentEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewAttachment.manager.ActivityReviewAttachmentManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogService;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.converter.ActivityAddFormConverter;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.converter.ActivityScheduleAddFormConverter;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.*;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.*;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;

import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.code.UnexpectedErrorCode;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.module.support.file.service.FileService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 活动和时间表 组合服务
 *
 * @Author akkkka114514
 * @Date 2025-09-07
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivityWithScheduleService {
    private ActivityManager activityManager;
    private ActivityScheduleManager activityScheduleManager;
    private TransactionTemplate transactionTemplate;
    private ActivityDao activityDao;
    private PortalUserManager portalUserManager;
    private ActivityReviewLogManager activityReviewLogManager;
    private ActivityCanEnrollCollegeService canEnrollCollegeService;
    private ActivityCanEnrollTribeService canEnrollTribeService;
    private ActivityCanEnrollGradeService canEnrollGradeService;
    private PortalUserValidator portalUserValidator;
    @Resource
    private FileService fileService;
    private ActivityReviewAttachmentManager reviewAttachmentManager;
    private ActivitySigninManagerService signinManagerService;
    private ActivityValidator activityValidator;
    private ActivityScheduleValidator activityScheduleValidator;
    private ActivityReviewLogService reviewLogService;

    public ResponseDTO<String> updateActivityWithSchedule(ActivityWithScheduleUpdateForm updateForm) {
        // TODO: implement full update logic
        return ResponseDTO.ok();
    }

    public ResponseDTO<PageResult<ActivityWithScheduleVO>> queryActivityWithSchedule(ActivityWithScheduleQueryForm queryForm) {
        return null; // TODO: implement
    }

    public ResponseDTO<String> batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseDTO.ok();
        }
        ids.forEach(this::deleteDraft);
        return ResponseDTO.ok();
    }


    public void validateParticipateTypeNotEmpty(ActivityWithScheduleAddForm addForm){
        //不能提交空列表
        if(addForm.getCanEnrollTribeIdList().isEmpty()
                &&addForm.getCanEnrollGradeIdList().isEmpty()
                &&addForm.getCanEnrollCollegeIdList().isEmpty()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
    }

    public void submitDraft(ActivityWithScheduleAddForm addForm) {
        log.info("添加待审核的活动：{}", addForm.toString());
        Long userId = SmartRequestUtil.getRequestUserId();
        portalUserValidator.validateIsCurrentUserPortal();
        portalUserValidator.validatePortalUserCanPublishActivity();
        PortalUserEntity portalUser = portalUserValidator.validatePortalUserId(userId);
        validateParticipateTypeNotEmpty(addForm);
        //todo 根据这个方法的写法优化其他方法
        //todo 重写定时任务
        ActivityEntity activityEntity= ActivityAddFormConverter.convert(addForm.getActivityAddForm());
        ActivityScheduleEntity scheduleEntity= ActivityScheduleAddFormConverter.convert(addForm.getActivityScheduleAddForm());

        transactionTemplate.executeWithoutResult(status -> {
            try {
                Long activityId = doSaveActivityTransaction(activityEntity,portalUser);
                scheduleEntity.setActivityId(activityId);
                doSaveActivityScheduleTransaction(scheduleEntity
                        ,addForm.getActivityAddForm().getNeedSignOut());
                canEnrollGradeService.doSaveBatchTransaction(addForm.getCanEnrollGradeIdList(),activityId);
                canEnrollCollegeService.doSaveBatchTransaction(addForm.getCanEnrollCollegeIdList(),activityId);
                canEnrollTribeService.doSaveBatchTransaction(addForm.getCanEnrollTribeIdList(),activityId);
                signinManagerService.doSaveBatchTransaction(
                        addForm.getActivitySigninManagerIdList(),
                        addForm.getActivityAddForm().getActivityBelongToSchoolId(),
                        activityId);

                log.info("活动草稿提交成功, activityId={}", activityId);
            } catch (Exception e) {
                log.error("活动草稿提交失败 exception occurred, title={}", activityEntity.getTitle(), e);
                status.setRollbackOnly();
            }
        });
    }

    public void deleteDraft(Long activityId){
        doDeleteActivityTransaction(activityId);
        doDeleteActivityScheduleTransaction(activityId);
        canEnrollCollegeService.doDeleteBatchTransaction(activityId);
        canEnrollGradeService.doDeleteBatchTransaction(activityId);
        canEnrollTribeService.doDeleteBatchTransaction(activityId);
        signinManagerService.doDeleteBatchTransaction(activityId);
    }

    public Long doSaveActivityTransaction(ActivityEntity entity,PortalUserEntity portalUser){
        activityValidator.validateAdd(entity,portalUser);
        transactionTemplate.executeWithoutResult(status -> {
            try {
                if(!activityManager.save(entity)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }//todo根据这个重写事务回滚和事务抛异常
            }catch (Exception e){
                log.error("插入activity失败，事务回滚，entity:{}",entity,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
            }
        });
        return entity.getId();
    }

    public void doSaveActivityScheduleTransaction(ActivityScheduleEntity schedule,boolean needSignOut){
        activityScheduleValidator.validateActivityScheduleOrder(schedule,needSignOut);
        transactionTemplate.executeWithoutResult(status -> {
            try {
                if (!activityScheduleManager.save(schedule)) {
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.error("doSaveActivityScheduleTransaction事务失败回滚：schedule={}",schedule);
            }
        });
    }


    public void doDeleteActivityTransaction(Long activityId) {
        ActivityEntity deletedActivity = new ActivityEntity();
        deletedActivity.setDeletedFlag(true);
        deletedActivity.setId(activityId);

        transactionTemplate.executeWithoutResult(status -> {
            try {
                if (!activityManager.updateById(deletedActivity)) {
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.info("doDeleteActivityTransaction success: activity deleted, activityId={}", activityId,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
            }
        });
    }

    public void doDeleteActivityScheduleTransaction(Long activityId) {
        ActivityScheduleEntity deletedSchedule = new ActivityScheduleEntity();
        deletedSchedule.setDeletedFlag(true);
        deletedSchedule.setActivityId(activityId);

        transactionTemplate.executeWithoutResult(status -> {
            try {
                if (!activityScheduleManager.updateById(deletedSchedule)) {
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.info("doDeleteActivityTransaction success: activity deleted, activityId={}", activityId,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
            }
        });
    }

    public void doUpdateActivityTransaction(ActivityUpdateForm updateForm){
        if (Objects.isNull(updateForm)){
            return;
        }
        ActivityEntity activity = ActivityUpdateForm.convert(updateForm);
        activityValidator.validateUpdate(activity);
        transactionTemplate.executeWithoutResult(status -> {
            try {
                if (!activityManager.updateById(activity)) {
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.error("doUpdateActivityTransaction 事务失败回滚：activity={}", activity,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
            }
        });

    }

    public void doUpdateActivityScheduleTransaction(ActivityEntity activity,ActivityScheduleUpdateForm updateForm){
        if(Objects.isNull(updateForm)){
            return;
        }
        ActivityScheduleEntity schedule = ActivityScheduleUpdateForm.convert(updateForm);
        schedule.setActivityId(activity.getId());
        activityScheduleValidator.validateUpdate(activity,schedule);
        transactionTemplate.executeWithoutResult(status -> {
            try{
                if(!activityScheduleManager.updateById(schedule)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.error("doUpdateActivityScheduleTransaction事务失败回滚：activity schedule={}",schedule,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
            }

        });
    }

    //只有当草稿未提交或重新成为草稿或在审核员手里可以修改
    //在草稿状态可以改审核员
    //被驳回会变成草稿
    //一旦提交草稿就只能由审核员进行修改
    //todo 驳回的话数据库的提交上来的都要作废，还要返回经过审核中改动的表单
    public void editActivityDraft(Long activityId,ActivityWithScheduleUpdateForm updateForm) {
        ActivityEntity activity = activityValidator.validateActivityId(activityId);
        validateEditDraftPermission(activity);
        transactionTemplate.executeWithoutResult(status -> {
            try {
                doUpdateActivityTransaction(updateForm.getActivityUpdateForm());
                doUpdateActivityScheduleTransaction(activity,updateForm.getActivityScheduleUpdateForm());
                canEnrollCollegeService.doUpdateBatchTransaction(updateForm.getCanEnrollCollegeIdList(),activityId);
                canEnrollGradeService.doUpdateBatchTransaction(updateForm.getCanEnrollGradeIdList(),activityId);
                canEnrollTribeService.doUpdateBatchTransaction(updateForm.getCanEnrollTribeIdList(), activityId);
                signinManagerService.doUpdateBatchTransaction(updateForm.getActivitySigninManagerIdList(),activity.getActivityBelongToSchoolId(),activityId);
            } catch (Exception e) {
                log.warn("更新活动内容事务失败,activityId:{}",activityId, e);
                status.setRollbackOnly();
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR, "更新活动内容失败，请重试");
            }

        });
    }

    public void validateEditDraftPermission(ActivityEntity activity){
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        ActivityReviewStage curReviewStage = reviewLogService.getCurReviewStage(activity.getId());
        if(requestUser instanceof RequestBackendUser){
            //审核只要不是这两个都不能改活动内容
            if(!Objects.equals(curReviewStage,ActivityReviewStage.INITIAL_CONTENT_REVIEW)&&
            !Objects.equals(curReviewStage,ActivityReviewStage.FINAL_CONTENT_REVIEW)){
                throw new BusinessException(UserErrorCode.NO_PERMISSION);
            }
            Long curReviewerId = reviewLogService.getCurReviewer(activity.getId());
            if(!Objects.equals(curReviewerId,requestUser.getUserId())){
                throw new BusinessException(UserErrorCode.NO_PERMISSION);
            }
        }
        if(requestUser instanceof RequestPortalUser){
            //只有activity manager能改
            if(!Objects.equals(activity.getActivityManagerId(),requestUser.getUserId())){
                throw new BusinessException(UserErrorCode.NO_PERMISSION);
            }
            //只有draft阶段能改
            if(!Objects.equals(curReviewStage,ActivityReviewStage.DRAFT)){
                throw new BusinessException(UserErrorCode.NO_PERMISSION);
            }
        }

    }

    public Page<ActivityWithScheduleVO> notStartAndPendingEnrollActivityPage(Long pageNum, Long pageSize){
        Page<ActivityWithScheduleVO> page = new Page<>(pageNum, pageSize);
        Long userId = SmartRequestUtil.getRequestUserId();
        PortalUserEntity portalUserEntity =portalUserManager.getById(userId);
        if(portalUserEntity==null||portalUserEntity.getDeletedFlag()) {
            throw new BusinessException(UnexpectedErrorCode.BUSINESS_HANDING, "用户不存在");
        }
        Long schoolId = portalUserEntity.getSchoolId();
        return activityDao.notStartAndPendingEnrollActivity(page,schoolId);
    }

    //活动完结申请
    //attachment:完结证明材料
    //TODO 添加未审核活动时可能要上传复数附件
    //TODO 把审核流程重写成责任链加策略模式
    public void endActivityProposal(
            Long reviewLogId,
            List<String> fileKeys){
        Long proposerId = SmartRequestUtil.getRequestUserId();
        ActivityReviewLogEntity reviewLog = activityReviewLogManager.getById(reviewLogId);
        if(reviewLog==null||reviewLog.getDeletedFlag()){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动审核记录不存在");
        }
        Long activityId = reviewLog.getActivityId();
        ActivityEntity activity = activityManager.getById(activityId);
        //防止无关人士
        if(Objects.equals(activity.getActivityManagerId(),proposerId)){
            throw new BusinessException(UserErrorCode.NO_PERMISSION,"你不是该活动的活动管理员，无权操作");
        }
        List<ActivityReviewAttachmentEntity> reviewAttachments=new LinkedList<>();
        fileKeys.forEach((fileKey)->{
            fileService.validateFileKey(fileKey,"fun-campus-attachment");
            ActivityReviewAttachmentEntity ra = new ActivityReviewAttachmentEntity();
            ra.setReviewLogId(reviewLogId);
            ra.setFileKey(fileKey);
            ra.setDeletedFlag(false);
            ra.setCreateTime(LocalDateTime.now());
            ra.setUpdateTime(LocalDateTime.now());
            reviewAttachments.add(ra);
        });

        reviewAttachmentManager.saveBatch(reviewAttachments);
        //todo 关于activityAttachment和activityReviewAttachment的改动，在其他方法还没改
        //todo 在提交未审核活动时，要同时把附件插入到activityAttachment和activityReviewAttachment

    }
}