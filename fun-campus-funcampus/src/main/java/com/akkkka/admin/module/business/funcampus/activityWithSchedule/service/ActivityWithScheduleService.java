package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.service.ActivityCanEnrollCollegeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.service.ActivityCanEnrollGradeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.service.ActivityCanEnrollTribeService;
import com.akkkka.admin.module.business.funcampus.activityCategory.service.ActivityCategoryService;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.MyEnrollmentStatusVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityRefundService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEnrollNum;
import com.akkkka.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
import com.akkkka.admin.module.business.funcampus.organizationInfo.service.OrganizationInfoService;

import com.akkkka.admin.module.business.funcampus.activityReviewAttachment.domain.entity.ActivityReviewAttachmentEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewAttachment.manager.ActivityReviewAttachmentManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogService;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.IndexActivityPageConst;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.converter.ActivityAddFormConverter;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.converter.ActivityScheduleAddFormConverter;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.*;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.*;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityStatusCacheManager;

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
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.module.support.file.service.FileService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    @Lazy
    private ActivityReviewLogService reviewLogService;
    private ActivityCategoryService activityCategoryService;
    private CollegeInfoService collegeInfoService;
    private OrganizationInfoService organizationInfoService;
    private ActivityEnrollmentManager activityEnrollmentManager;
    private ActivityEnrollNumDao activityEnrollNumDao;
    private ActivityEnrollmentService activityEnrollmentService;
    private ActivityStatusCacheManager activityStatusCacheManager;
    private ActivityOrderManager activityOrderManager;
    private ActivityRefundService activityRefundService;

    /**
     * 活动详情页
     */
    public ActivityDetailVO detail(Long activityId) {
        // 校验活动存在性
        ActivityEntity activity = activityValidator.validateActivityId(activityId);

        // 活动时间表
        ActivityScheduleEntity schedule = activityScheduleManager.getById(activityId);

        // 组装 ActivityVO
        ActivityVO activityVO = buildActivityVO(activity);

        // 组装 ActivityScheduleVO
        ActivityScheduleVO scheduleVO = buildScheduleVO(schedule);

        // 报名人数
        ActivityEnrollNum enrollNumEntity = activityEnrollNumDao.selectById(activityId);
        Integer enrollNum = enrollNumEntity != null ? enrollNumEntity.getEnrollNum() : 0;

        // 签到人数
        Long signInNum = activityEnrollmentManager.count(
                activityEnrollmentManager.qwByActivityId(activityId)
                        .eq(ActivityEnrollmentEntity::getSignInStatus, true)
        );

        // 报名用户列表
        List<EnrollerVO> enrollUsers = activityEnrollmentService.listEnrollUserAsEnrollerVO(activityId);

        // 报名范围
        ActivityDetailVO detailVO = new ActivityDetailVO();
        detailVO.setActivity(activityVO);
        detailVO.setSchedule(scheduleVO);
        detailVO.setEnrollUsers(enrollUsers);
        detailVO.setEnrollNum(enrollNum);
        detailVO.setSignInNum(signInNum);
        detailVO.setCanEnrollCollege(canEnrollCollegeService.listIdNameByActivityId(activityId));
        detailVO.setCanEnrollGrade(canEnrollGradeService.getIdNameByActivityId(activityId));
        detailVO.setCanEnrollTribe(canEnrollTribeService.getIdNameByActivityId(activityId));

        // 付费活动：附加当前门户用户最新订单（未登录/非门户用户返回 null）
        detailVO.setCurrentUserOrder(buildCurrentUserOrder(activityId));

        // 当前门户用户的报名状态（未登录/非门户用户返回 null，免费付费通用）
        detailVO.setCurrentUserEnrollment(buildCurrentUserEnrollment(activityId));

        return detailVO;
    }

    /**
     * 当前门户用户在该活动的最新订单（未登录/非门户用户/无订单时返回 null）
     */
    private ActivityOrderVO buildCurrentUserOrder(Long activityId) {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (requestUser == null || requestUser.getUserType() != UserTypeEnum.PORTAL_USER) {
            return null;
        }
        ActivityOrderEntity order = activityOrderManager.getLatestByActivityAndUser(activityId, requestUser.getUserId());
        if (order == null) {
            return null;
        }
        ActivityOrderVO orderVO = new ActivityOrderVO();
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setActivityId(order.getActivityId());
        orderVO.setAmountFen(order.getAmountFen());
        orderVO.setStatus(order.getStatus() == null ? null : order.getStatus().getCode());
        orderVO.setPayChannel(order.getPayChannel() == null ? null : order.getPayChannel().getCode());
        orderVO.setPayTime(order.getPayTime());
        orderVO.setExpireTime(order.getExpireTime());
        orderVO.setCloseTime(order.getCloseTime());
        orderVO.setCreateTime(order.getCreateTime());
        return orderVO;
    }

    /**
     * 当前门户用户在该活动的报名状态（未登录/非门户用户返回 null；未报名时 enrolled=false）
     */
    private MyEnrollmentStatusVO buildCurrentUserEnrollment(Long activityId) {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (requestUser == null || requestUser.getUserType() != UserTypeEnum.PORTAL_USER) {
            return null;
        }
        ActivityEnrollmentEntity enrollment = activityEnrollmentManager.getOne(
                activityEnrollmentManager.qwByActivityId(activityId)
                        .eq(ActivityEnrollmentEntity::getUserId, requestUser.getUserId()));
        MyEnrollmentStatusVO vo = new MyEnrollmentStatusVO();
        vo.setActivityId(activityId);
        vo.setEnrolled(enrollment != null);
        vo.setSignInStatus(enrollment != null && Boolean.TRUE.equals(enrollment.getSignInStatus()));
        vo.setSignOutStatus(enrollment != null && Boolean.TRUE.equals(enrollment.getSignOutStatus()));
        return vo;
    }

    private ActivityVO buildActivityVO(ActivityEntity activity) {
        ActivityVO vo = new ActivityVO();
        vo.setId(activity.getId());
        vo.setTitle(activity.getTitle());
        vo.setStatus(activity.getStatus() == null ? null : activity.getStatus().getCode());
        vo.setPosition(activity.getPosition());
        vo.setScoreCanGet(activity.getScoreCanGet());
        vo.setEnrollNumLimit(activity.getEnrollNumLimit());
        vo.setDescription(activity.getDescription());
        vo.setEnrollNeedReview(activity.getEnrollNeedReview());
        vo.setNeedSignOut(activity.getNeedSignOut());
        vo.setAttachment(activity.getAttachment());
        vo.setCoverImg(activity.getCoverImg());
        vo.setCreateTime(activity.getCreateTime());
        vo.setUpdateTime(activity.getUpdateTime());
        // 归属信息
        vo.setActivityBelongToSchoolId(activity.getActivityBelongToSchoolId());
        vo.setActivityBelongToSchoolName(collegeInfoService.getNameById(activity.getActivityBelongToSchoolId()));
        vo.setActivityBelongToOrganizationId(activity.getActivityBelongToOrganizationId());
        vo.setActivityBelongToOrganizationName(organizationInfoService.getNameById(activity.getActivityBelongToOrganizationId()));
        vo.setActivityBelongToCollegeId(activity.getActivityBelongToCollegeId());
        vo.setActivityBelongToCollegeName(collegeInfoService.getNameById(activity.getActivityBelongToCollegeId()));
        // 分类
        vo.setCategoryId(activity.getCategoryId());
        vo.setCategoryName(activityCategoryService.getNameById(activity.getCategoryId()));
        // 付费信息
        vo.setPaidFlag(activity.getPaidFlag());
        vo.setPriceFen(activity.getPriceFen());
        vo.setRefundPolicy(activity.getRefundPolicy() == null ? null : activity.getRefundPolicy().getCode());
        // 活动管理员
        PortalUserEntity manager = portalUserManager.getById(activity.getActivityManagerId());
        if (manager != null) {
            com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO managerVO =
                    new com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO();
            managerVO.setId(manager.getId());
            managerVO.setUsername(manager.getUsername());
            managerVO.setSchoolId(manager.getSchoolId());
            managerVO.setCollegeId(manager.getCollegeId());
            vo.setActivityManager(managerVO);
        }
        return vo;
    }

    private ActivityScheduleVO buildScheduleVO(ActivityScheduleEntity schedule) {
        ActivityScheduleVO vo = new ActivityScheduleVO();
        vo.setEnrollStartTime(schedule.getEnrollStartTime());
        vo.setEnrollEndTime(schedule.getEnrollEndTime());
        vo.setActivityStartTime(schedule.getActivityStartTime());
        vo.setActivityEndTime(schedule.getActivityEndTime());
        vo.setSigninStartTime(schedule.getSigninStartTime());
        vo.setSigninEndTime(schedule.getSigninEndTime());
        vo.setSignoutStartTime(schedule.getSignoutStartTime());
        vo.setSignoutEndTime(schedule.getSignoutEndTime());
        return vo;
    }

    /**
     * 门户端更新活动（草稿/审核阶段）：校验通过后走既有草稿编辑链路
     */
    public ResponseDTO<String> updateActivityWithSchedule(ActivityWithScheduleUpdateForm updateForm) {
        if (updateForm == null || updateForm.getActivityUpdateForm() == null
                || updateForm.getActivityUpdateForm().getId() == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "缺少活动ID");
        }
        Long activityId = updateForm.getActivityUpdateForm().getId();
        editActivityDraft(activityId, updateForm);
        return ResponseDTO.ok();
    }

    /**
     * 状态倒退：回退活动状态并清空该活动的报名数据（逻辑删 + 报名数清 0）
     * 用于时间表后调导致期望状态小于当前状态的场景，倒退后报名窗口重新开放即可重新报名。
     * 同一事务内完成，且先用原状态做乐观条件更新：状态被并发修改时不做任何变更并返回 false，
     * 下一轮定时任务会重新检测；中途失败整体回滚，可安全重试
     *
     * @param activityId 活动ID
     * @param currentStatus 当前状态（乐观条件）
     * @param expectedStatus 期望回退到的状态
     * @return 是否完成倒退
     */
    public boolean retreatActivityStatusTransaction(Long activityId, ActivityStatus currentStatus, ActivityStatus expectedStatus) {
        return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            if (!activityManager.updateStatusIfMatch(activityId, expectedStatus, currentStatus)) {
                return false;
            }
            // 清空报名数据（逻辑删除）
            activityEnrollmentManager.lambdaUpdate()
                    .set(ActivityEnrollmentEntity::getDeletedFlag, true)
                    .eq(ActivityEnrollmentEntity::getActivityId, activityId)
                    .eq(ActivityEnrollmentEntity::getDeletedFlag, false)
                    .update();
            // 报名计数清零
            activityEnrollNumDao.resetEnrollNum(activityId);
            log.info("活动状态倒退完成：activityId={}, {} -> {}，报名数据已清空",
                    activityId, currentStatus, expectedStatus);
            return true;
        }));
    }

    /**
     * 管理端取消活动：活动置为「已取消」并对全部已支付订单发起系统退款
     * <p>
     * - 取消窗口：已结束/已取消不可再取消，其余状态（等待报名~进行中）均可取消；
     * - 状态更新使用 CAS 条件更新，与定时任务推进互斥，防并发覆盖；
     * - 状态更新成功后依次处理存量订单：待支付订单立即 CAS 关单并释放名额（防止取消后继续完成支付）；
     *   已支付订单批量系统退款（单笔失败仅记日志，可在退款管理页重试）
     */
    public ResponseDTO<String> cancelActivity(Long activityId) {
        ActivityEntity activity = activityValidator.validateActivityId(activityId);
        ActivityStatus currentStatus = activity.getStatus();
        if (currentStatus == ActivityStatus.CANCELLED) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动已取消，请勿重复操作");
        }
        if (currentStatus == ActivityStatus.FINISHED) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动已结束，不可取消");
        }
        if (!activityManager.updateStatusIfMatch(activityId, ActivityStatus.CANCELLED, currentStatus)) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动状态已变更，取消失败，请刷新后重试");
        }
        log.info("管理端取消活动成功：activityId={}，原状态={}", activityId, currentStatus);
        // 状态更新成功后依次处理存量订单：先关待支付，再退已支付
        closeWaitPayOrdersForCancel(activityId);
        activityRefundService.refundForCanceledActivity(activityId);
        return ResponseDTO.ok();
    }

    /**
     * 活动取消联动关单：关闭全部待支付订单并释放锁定的名额
     * <p>
     * 防止取消后用户从已打开的收银台继续完成支付（与超时关单同为 CAS 抢占，
     * 在途支付输给关单时由支付回调按「回调晚于关单」记录补偿日志）
     */
    private void closeWaitPayOrdersForCancel(Long activityId) {
        List<ActivityOrderEntity> waitPayOrders = activityOrderManager.listWaitPayOrdersByActivity(activityId);
        if (waitPayOrders.isEmpty()) {
            return;
        }
        LocalDateTime closeTime = LocalDateTime.now();
        for (ActivityOrderEntity order : waitPayOrders) {
            if (!activityOrderManager.closeIfWaitPayCas(order.getId(), closeTime)) {
                // CAS 失败：订单已被支付回调/用户取消先行处理，跳过
                continue;
            }
            if (!activityEnrollNumDao.decreaseEnrollNum(activityId)) {
                log.warn("活动取消关单释放名额失败：activityId={}，orderNo={}", activityId, order.getOrderNo());
            }
        }
        log.info("活动取消联动关单完成：activityId={}，待支付订单数={}", activityId, waitPayOrders.size());
    }

    public ResponseDTO<PageResult<ActivityWithScheduleVO>> queryActivityWithSchedule(ActivityWithScheduleQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityWithScheduleVO> list = activityDao.queryActivityWithSchedule(page, queryForm);
        PageResult<ActivityWithScheduleVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return ResponseDTO.ok(pageResult);
    }

    /**
     * 活动详情（含时间表）：按活动ID查询，供编辑回显使用
     */
    public ActivityWithScheduleVO detailWithSchedule(Long activityId) {
        if (activityId == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "缺少活动ID");
        }
        ActivityWithScheduleVO vo = activityDao.getActivityWithScheduleById(activityId);
        if (vo == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动不存在");
        }
        return vo;
    }

    public ResponseDTO<String> batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseDTO.ok();
        }
        ids.forEach(this::deleteDraft);
        return ResponseDTO.ok();
    }


    public void validateParticipateTypeNotEmpty(ActivityWithScheduleAddForm addForm){
        //不能提交空列表（列表允许为 null，视为该维度不限）
        boolean gradeEmpty = addForm.getCanEnrollGradeIdList()==null||addForm.getCanEnrollGradeIdList().isEmpty();
        boolean collegeEmpty = addForm.getCanEnrollCollegeIdList()==null||addForm.getCanEnrollCollegeIdList().isEmpty();
        boolean tribeEmpty = addForm.getCanEnrollTribeIdList()==null||addForm.getCanEnrollTribeIdList().isEmpty();
        if(gradeEmpty&&collegeEmpty&&tribeEmpty){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"报名范围不能为空，请至少选择学院/年级/部落其一");
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
        // 数据库 status 为 NOT NULL：按时间表初始化状态，避免插入失败
        activityEntity.setStatus(calcInitStatus(scheduleEntity));

        transactionTemplate.executeWithoutResult(status -> {
            try {
                Long activityId = doSaveActivityTransaction(activityEntity,portalUser);
                scheduleEntity.setActivityId(activityId);
                doSaveActivityScheduleTransaction(scheduleEntity
                        ,Boolean.TRUE.equals(addForm.getActivityAddForm().getNeedSignOut()));
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

    /**
     * 管理端创建活动：创建后按时间表直接进入对应时间阶段（无需走门户审核链路）
     * 管理端无门户登录态，归属与活动管理员只做存在性/一致性校验，不校验当前用户归属
     */
    public ResponseDTO<String> createByAdmin(ActivityWithScheduleAddForm addForm) {
        if (addForm == null || addForm.getActivityAddForm() == null
                || addForm.getActivityScheduleAddForm() == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动基本信息与时间表不能为空");
        }
        validateParticipateTypeNotEmpty(addForm);
        ActivityEntity activityEntity = ActivityAddFormConverter.convert(addForm.getActivityAddForm());
        ActivityScheduleEntity scheduleEntity = ActivityScheduleAddFormConverter.convert(addForm.getActivityScheduleAddForm());
        // 数据库 organization_id 为 NOT NULL：仅归属学院时以 0 占位，0 表示不归属组织
        if (activityEntity.getActivityBelongToOrganizationId() == null) {
            activityEntity.setActivityBelongToOrganizationId(0L);
        }
        // 数据库 status 为 NOT NULL：按时间表初始化状态
        activityEntity.setStatus(calcInitStatus(scheduleEntity));
        activityValidator.validateAddByAdmin(activityEntity);

        transactionTemplate.executeWithoutResult(status -> {
            try {
                Long activityId = doSaveActivityEntityTransaction(activityEntity);
                scheduleEntity.setActivityId(activityId);
                doSaveActivityScheduleTransaction(scheduleEntity, Boolean.TRUE.equals(activityEntity.getNeedSignOut()));
                replaceCanEnrollColleges(addForm.getCanEnrollCollegeIdList(), activityId);
                replaceCanEnrollGrades(addForm.getCanEnrollGradeIdList(), activityId);
                replaceCanEnrollTribes(addForm.getCanEnrollTribeIdList(), activityId);
                replaceSigninManagers(addForm.getActivitySigninManagerIdList(),
                        activityEntity.getActivityBelongToSchoolId(), activityId);
                log.info("管理端创建活动成功, activityId={}", activityId);
            } catch (BusinessException e) {
                status.setRollbackOnly();
                throw e;
            } catch (Exception e) {
                log.error("管理端创建活动失败, title={}", activityEntity.getTitle(), e);
                status.setRollbackOnly();
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR, "创建活动失败，请重试");
            }
        });
        return ResponseDTO.ok();
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
        return doSaveActivityEntityTransaction(entity);
    }

    /**
     * 仅落库活动实体（校验由调用方前置完成），返回自增主键
     */
    private Long doSaveActivityEntityTransaction(ActivityEntity entity){
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
                log.error("doSaveActivityScheduleTransaction事务失败回滚：schedule={}",schedule,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
            }
        });
        // 尽力投递：新活动当天有关键时间点时立即加入缓存名单（失败不影响业务，漏投由重建/回源兜底）
        activityStatusCacheManager.tryAddToTodayCache(schedule);
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
        // 尽力投递：时间表更新后当天有关键时间点时立即加入缓存名单
        activityStatusCacheManager.tryAddToTodayCache(schedule);
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

    /**
     * 管理端更新活动：任意时间阶段均可编辑（无审核链路权限限制）
     * 未提交的子表单/列表按「不修改」处理；空列表表示清空；非空列表替换
     */
    public ResponseDTO<String> updateByAdmin(ActivityWithScheduleUpdateForm updateForm) {
        if (updateForm == null || updateForm.getActivityUpdateForm() == null
                || updateForm.getActivityUpdateForm().getId() == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "缺少活动ID");
        }
        ActivityUpdateForm activityUpdateForm = updateForm.getActivityUpdateForm();
        Long activityId = activityUpdateForm.getId();
        ActivityEntity dbActivity = activityValidator.validateActivityId(activityId);
        // 未提交签退开关时沿用数据库现值，供时间表校验使用
        boolean needSignOut = activityUpdateForm.getNeedSignOut() != null
                ? activityUpdateForm.getNeedSignOut()
                : Boolean.TRUE.equals(dbActivity.getNeedSignOut());

        transactionTemplate.executeWithoutResult(status -> {
            try {
                doUpdateActivityTransaction(activityUpdateForm);
                doUpdateActivityScheduleByAdmin(activityId, updateForm.getActivityScheduleUpdateForm(), needSignOut);
                replaceCanEnrollColleges(updateForm.getCanEnrollCollegeIdList(), activityId);
                replaceCanEnrollGrades(updateForm.getCanEnrollGradeIdList(), activityId);
                replaceCanEnrollTribes(updateForm.getCanEnrollTribeIdList(), activityId);
                replaceSigninManagers(updateForm.getActivitySigninManagerIdList(),
                        dbActivity.getActivityBelongToSchoolId(), activityId);
            } catch (BusinessException e) {
                status.setRollbackOnly();
                throw e;
            } catch (Exception e) {
                log.warn("管理端更新活动失败, activityId={}", activityId, e);
                status.setRollbackOnly();
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR, "更新活动失败，请重试");
            }
        });
        return ResponseDTO.ok();
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

    /**
     * 活动日历：按日期区间查询活动
     * <p>
     * 命中规则：活动时间与 [startDate, endDate]（含当天）有重叠；
     * activeActivityPage：1-本校（取当前用户学校） 2-全局
     */
    public List<ActivityCalendarVO> calendar(LocalDate startDate, LocalDate endDate, Integer activeActivityPage) {
        if (startDate == null || endDate == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "日期不能为空");
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "结束日期不能早于开始日期");
        }
        if (ChronoUnit.DAYS.between(startDate, endDate) > 62) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "查询范围最多62天");
        }
        if (activeActivityPage == null || (!activeActivityPage.equals(IndexActivityPageConst.MY_SCHOOL_ACTIVITY)
                && !activeActivityPage.equals(IndexActivityPageConst.GLOBAL_ACTIVITY))) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动范围参数错误");
        }
        Long schoolId = null;
        if (activeActivityPage.equals(IndexActivityPageConst.MY_SCHOOL_ACTIVITY)) {
            Long userId = SmartRequestUtil.getRequestUserId();
            PortalUserEntity portalUser = portalUserManager.getById(userId);
            if (portalUser == null || Boolean.TRUE.equals(portalUser.getDeletedFlag())) {
                throw new BusinessException(UnexpectedErrorCode.BUSINESS_HANDING, "用户不存在");
            }
            if (portalUser.getSchoolId() == null) {
                throw new BusinessException(UserErrorCode.PARAM_ERROR, "当前用户未绑定学校");
            }
            schoolId = portalUser.getSchoolId();
        }
        // 半开区间 [startDate 00:00, endDate+1 00:00)，与活动时间有交集即命中
        return activityDao.queryCalendar(schoolId, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    /**
     * 活动阶段倒计时：返回当前阶段、下一阶段名称及剩余秒数
     * 
     * 根据活动时间表计算当前所处阶段，并计算距离下一阶段的剩余秒数
     * 前端用于展示活动倒计时卡片
     * 
     * 阶段顺序：报名开始 → 报名结束 → 活动开始 → 活动结束 → 签到开始 → 签到结束 → 签退开始 → 签退结束
     * 
     * @param activityId 活动ID
     * @return 倒计时VO，包含：
     *         - currentPhase: 当前阶段名称（"未开始"/"报名开始"/.../"已结束"）
     *         - nextPhase: 下一阶段名称
     *         - nextPhaseStartTime: 下一阶段开始时间
     *         - remainingSeconds: 剩余秒数（-1表示活动已结束）
     */
    public ActivityPhaseCountdownVO phaseCountdown(Long activityId) {
        ActivityScheduleEntity schedule = activityScheduleManager.getById(activityId);
        if (schedule == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动时间表不存在");
        }

        LocalDateTime now = LocalDateTime.now();

        // 按时间顺序定义各阶段
        String[] phaseNames = {"报名开始", "报名结束", "活动开始", "活动结束", "签到开始", "签到结束", "签退开始", "签退结束"};
        LocalDateTime[] phaseTimes = {
                schedule.getEnrollStartTime(),
                schedule.getEnrollEndTime(),
                schedule.getActivityStartTime(),
                schedule.getActivityEndTime(),
                schedule.getSigninStartTime(),
                schedule.getSigninEndTime(),
                schedule.getSignoutStartTime(),
                schedule.getSignoutEndTime()
        };

        ActivityPhaseCountdownVO vo = new ActivityPhaseCountdownVO();

        // 活动尚未开始（报名还未开始）
        if (now.isBefore(phaseTimes[0])) {
            vo.setCurrentPhase("未开始");
            vo.setNextPhase(phaseNames[0]);
            vo.setNextPhaseStartTime(phaseTimes[0].toString());
            vo.setRemainingSeconds(Duration.between(now, phaseTimes[0]).getSeconds());
            return vo;
        }

        // 活动已全部结束
        if (!now.isBefore(phaseTimes[phaseTimes.length - 1])) {
            vo.setCurrentPhase("已结束");
            vo.setNextPhase(null);
            vo.setNextPhaseStartTime(null);
            vo.setRemainingSeconds(-1L);
            return vo;
        }

        // 找到当前所处阶段：最后一个已开始（startTime <= now）的阶段
        int currentIdx = 0;
        for (int i = phaseTimes.length - 1; i >= 0; i--) {
            if (phaseTimes[i] != null && !now.isBefore(phaseTimes[i])) {
                currentIdx = i;
                break;
            }
        }

        vo.setCurrentPhase(phaseNames[currentIdx]);

        // 下一阶段
        int nextIdx = currentIdx + 1;
        if (nextIdx < phaseTimes.length && phaseTimes[nextIdx] != null) {
            vo.setNextPhase(phaseNames[nextIdx]);
            vo.setNextPhaseStartTime(phaseTimes[nextIdx].toString());
            vo.setRemainingSeconds(Duration.between(now, phaseTimes[nextIdx]).getSeconds());
        } else {
            vo.setNextPhase("已结束");
            vo.setNextPhaseStartTime(null);
            vo.setRemainingSeconds(-1L);
        }

        return vo;
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

    // ==================== 管理端创建/更新辅助方法 ====================

    /**
     * 按时间表初始化活动状态，关键时间缺失时兜底为「等待报名」
     */
    private ActivityStatus calcInitStatus(ActivityScheduleEntity scheduleEntity) {
        ActivityStatus status = ActivityStatus.calculate(LocalDateTime.now(),
                scheduleEntity.getEnrollStartTime(),
                scheduleEntity.getEnrollEndTime(),
                scheduleEntity.getActivityStartTime(),
                scheduleEntity.getActivityEndTime());
        return status == null ? ActivityStatus.WAIT_ENROLL : status;
    }

    /**
     * 管理端时间表更新：未提交的时间字段回填数据库现值后整体校验；
     * 需要签退时签退时间必填（未提交则沿用数据库现值）；更新后尽力投递当天关键时间缓存
     */
    private void doUpdateActivityScheduleByAdmin(Long activityId,
                                                 ActivityScheduleUpdateForm updateForm,
                                                 boolean needSignOut) {
        if (Objects.isNull(updateForm)) {
            return;
        }
        // activity_schedule 主键即 activity_id（与活动ID相同）
        ActivityScheduleEntity dbSchedule = activityScheduleManager.getById(activityId);
        if (dbSchedule == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动时间表不存在");
        }
        ActivityScheduleEntity schedule = ActivityScheduleUpdateForm.convert(updateForm);
        schedule.setActivityId(activityId);
        // 未提交的时间字段回填数据库现值，保证整体顺序校验可执行
        if (schedule.getEnrollStartTime() == null) {
            schedule.setEnrollStartTime(dbSchedule.getEnrollStartTime());
        }
        if (schedule.getEnrollEndTime() == null) {
            schedule.setEnrollEndTime(dbSchedule.getEnrollEndTime());
        }
        if (schedule.getActivityStartTime() == null) {
            schedule.setActivityStartTime(dbSchedule.getActivityStartTime());
        }
        if (schedule.getActivityEndTime() == null) {
            schedule.setActivityEndTime(dbSchedule.getActivityEndTime());
        }
        if (schedule.getSigninStartTime() == null) {
            schedule.setSigninStartTime(dbSchedule.getSigninStartTime());
        }
        if (schedule.getSigninEndTime() == null) {
            schedule.setSigninEndTime(dbSchedule.getSigninEndTime());
        }
        if (needSignOut) {
            // 需要签退时签退时间必填：未提交则沿用数据库现值
            if (schedule.getSignoutStartTime() == null) {
                schedule.setSignoutStartTime(dbSchedule.getSignoutStartTime());
            }
            if (schedule.getSignoutEndTime() == null) {
                schedule.setSignoutEndTime(dbSchedule.getSignoutEndTime());
            }
        }
        validateScheduleOrderForAdmin(schedule, needSignOut);
        if (!activityScheduleManager.updateById(schedule)) {
            throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
        }
        // 尽力投递：时间表更新后当天有关键时间点时立即加入缓存名单
        activityStatusCacheManager.tryAddToTodayCache(schedule);
    }

    /**
     * 管理端时间表顺序校验：报名开始《报名结束《活动开始《活动结束《签到开始《签到结束；
     * 需要签退时签退时间必填；签退时间填写（成对）后需晚于签到结束
     */
    private void validateScheduleOrderForAdmin(ActivityScheduleEntity schedule, boolean needSignOut) {
        if (schedule.getEnrollStartTime() == null || schedule.getEnrollEndTime() == null
                || schedule.getActivityStartTime() == null || schedule.getActivityEndTime() == null
                || schedule.getSigninStartTime() == null || schedule.getSigninEndTime() == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动时间表不完整");
        }
        if (!(schedule.getEnrollStartTime().isBefore(schedule.getEnrollEndTime())
                && schedule.getEnrollEndTime().isBefore(schedule.getActivityStartTime())
                && schedule.getActivityStartTime().isBefore(schedule.getActivityEndTime())
                && schedule.getActivityEndTime().isBefore(schedule.getSigninStartTime())
                && schedule.getSigninStartTime().isBefore(schedule.getSigninEndTime()))) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动时间不按顺序");
        }
        boolean signoutAnyFilled = schedule.getSignoutStartTime() != null || schedule.getSignoutEndTime() != null;
        boolean signoutPairFilled = schedule.getSignoutStartTime() != null && schedule.getSignoutEndTime() != null;
        if (signoutAnyFilled && !signoutPairFilled) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "签退时间需成对填写");
        }
        if (needSignOut && !signoutPairFilled) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "需要签退的活动必须填写签退时间");
        }
        if (signoutPairFilled
                && !(schedule.getSignoutEndTime().isAfter(schedule.getSignoutStartTime())
                && schedule.getSignoutStartTime().isAfter(schedule.getSigninEndTime()))) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动时间不按顺序");
        }
    }

    /**
     * 报名学院范围：null 表示不修改；空列表清空；非空替换
     */
    private void replaceCanEnrollColleges(List<Long> collegeIdList, Long activityId) {
        if (collegeIdList == null) {
            return;
        }
        if (collegeIdList.isEmpty()) {
            canEnrollCollegeService.doDeleteBatchTransaction(activityId);
            return;
        }
        canEnrollCollegeService.doUpdateBatchTransaction(collegeIdList, activityId);
    }

    /**
     * 报名年级范围：null 表示不修改；空列表清空；非空替换
     */
    private void replaceCanEnrollGrades(List<Long> gradeIdList, Long activityId) {
        if (gradeIdList == null) {
            return;
        }
        if (gradeIdList.isEmpty()) {
            canEnrollGradeService.doDeleteBatchTransaction(activityId);
            return;
        }
        canEnrollGradeService.doUpdateBatchTransaction(gradeIdList, activityId);
    }

    /**
     * 报名部落范围：null 表示不修改；空列表清空；非空替换
     */
    private void replaceCanEnrollTribes(List<Long> tribeIdList, Long activityId) {
        if (tribeIdList == null) {
            return;
        }
        if (tribeIdList.isEmpty()) {
            canEnrollTribeService.doDeleteBatchTransaction(activityId);
            return;
        }
        canEnrollTribeService.doUpdateBatchTransaction(tribeIdList, activityId);
    }

    /**
     * 签到员：null 表示不修改；空列表清空；非空替换
     */
    private void replaceSigninManagers(List<Long> signinManagerIdList, Long schoolId, Long activityId) {
        if (signinManagerIdList == null) {
            return;
        }
        if (signinManagerIdList.isEmpty()) {
            signinManagerService.doDeleteBatchTransaction(activityId);
            return;
        }
        signinManagerService.doUpdateBatchTransaction(signinManagerIdList, schoolId, activityId);
    }

}