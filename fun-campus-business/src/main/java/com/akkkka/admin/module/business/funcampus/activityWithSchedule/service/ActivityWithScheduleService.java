package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.manager.ActivityCanEnrollCollegeManager;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.service.ActivityCanEnrollCollegeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.manager.ActivityCanEnrollGradeManager;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.service.ActivityCanEnrollGradeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.manager.ActivityCanEnrollTribeManager;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.service.ActivityCanEnrollTribeService;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.entity.ActivityCategoryEntity;
import com.akkkka.admin.module.business.funcampus.activityCategory.manager.ActivityCategoryManager;
import com.akkkka.admin.module.business.funcampus.activityCategory.service.ActivityCategoryService;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityReviewAttachment.domain.entity.ActivityReviewAttachmentEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewAttachment.manager.ActivityReviewAttachmentManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogService;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewStateMachineContext;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.manager.ActivitySigninManagerManager;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.converter.ActivityAddFormConverter;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.converter.ActivityScheduleAddFormConverter;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEnrollNum;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.*;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.*;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.entity.CollegeInfoEntity;
import com.akkkka.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import com.akkkka.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.entity.GradeInfoEntity;
import com.akkkka.admin.module.business.funcampus.gradeInfo.manager.GradeInfoManager;
import com.akkkka.admin.module.business.funcampus.gradeInfo.service.GradeInfoService;
import com.akkkka.admin.module.business.funcampus.organizationInfo.service.OrganizationInfoService;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserService;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.admin.module.business.funcampus.schoolInfo.service.SchoolInfoService;
import com.akkkka.admin.module.business.funcampus.tribe.domain.entity.TribeEntity;
import com.akkkka.admin.module.business.funcampus.tribe.manager.TribeManager;
import com.akkkka.admin.module.system.backendUser.service.BackendUserService;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.code.UnexpectedErrorCode;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.module.support.file.service.FileService;
import com.alibaba.cola.statemachine.StateMachine;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private ActivityEnrollNumDao activityEnrollNumDao;
    private PortalUserManager portalUserManager;
    private ActivityCanEnrollTribeManager activityCanEnrollTribeManager;
    private ActivityCanEnrollGradeManager activityCanEnrollGradeManager;
    private ActivityCanEnrollCollegeManager activityCanEnrollCollegeManager;
    private ActivityReviewLogManager activityReviewLogManager;
    private ActivitySigninManagerManager signinManagerManager;
    private ActivityCategoryManager activityCategoryManager;
    private CollegeInfoManager collegeInfoManager;
    private GradeInfoManager gradeInfoManager;
    private TribeManager tribeManager;
    private ActivityCanEnrollCollegeService canEnrollCollegeService;
    private ActivityCanEnrollTribeService canEnrollTribeService;
    private ActivityCanEnrollGradeService canEnrollGradeService;
    private ActivityEnrollmentService enrollmentService;
    private ActivityEnrollmentManager enrollmentManager;
    private PortalUserValidator portalUserValidator;
    @Resource
    private FileService fileService;
    private ActivityReviewAttachmentManager reviewAttachmentManager;
    @Resource
    private StateMachine<ActivityReviewStage, ActivityReviewEvent, ActivityReviewStateMachineContext> stateMachine;
    private ActivitySigninManagerService signinManagerService;
    private ActivityValidator activityValidator;
    private ActivityScheduleValidator activityScheduleValidator;
    private ActivityReviewLogService reviewLogService;
    private final SchoolInfoService schoolInfoService;
    private final CollegeInfoService collegeInfoService;
    private final GradeInfoService gradeInfoService;
    private final OrganizationInfoService organizationInfoService;
    private final ActivityCategoryService categoryService;
    private final PortalUserService portalUserService;
    @Autowired
    private BackendUserService backendUserService;


    public ActivityWithScheduleVO getDraft(Long activityId){
        getDraftActivityPart(activityId);
        getDraftActivitySchedulePart(activityId);

    }

    public ActivityVO getDraftActivityPart(Long activityId){
        ActivityEntity activity = activityManager.getById(activityId);

        ActivityVO vo = new ActivityVO();
        vo.setId(activityId);
        vo.setTitle(activity.getTitle());
        vo.setStatus(activity.getStatus());
        vo.setPosition(activity.getPosition());
        vo.setScoreCanGet(activity.getScoreCanGet());
        vo.setEnrollNumLimit(activity.getEnrollNumLimit());
        vo.setActivityBelongToSchoolId(activity.getActivityBelongToSchoolId());
        vo.setActivityBelongToSchoolName(schoolInfoService.getNameById(activity.getActivityBelongToSchoolId()));
        vo.setActivityBelongToOrganizationId(activity.getActivityBelongToOrganizationId());
        vo.setActivityBelongToOrganizationName(organizationInfoService.getNameById(activity.getActivityBelongToOrganizationId()));
        vo.setActivityBelongToCollegeId(activity.getActivityBelongToCollegeId());
        vo.setActivityBelongToCollegeName(collegeInfoService.getNameById(activity.getActivityBelongToCollegeId()));
        vo.setCreateTime(activity.getCreateTime());
        vo.setUpdateTime(activity.getUpdateTime());
        vo.setDescription(activity.getDescription());
        vo.setEnrollNeedReview(activity.getEnrollNeedReview());
        vo.setNeedSignOut(activity.getNeedSignOut());
        vo.setAttachment(activity.getAttachment());
        vo.setCategoryId(activity.getCategoryId());
        vo.setCategoryName(categoryService.getNameById(activity.getCategoryId()));
        vo.setCoverImg(activity.getCoverImg());
        vo.setActivityManager(portalUserService.getById(activity.getActivityManagerId()));

        return vo;
    }

    public ActivityScheduleVO getDraftActivitySchedulePart(Long activityId){
        ActivityScheduleEntity schedule = activityScheduleManager.getById(activityId);
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


    public RejectedActivityDraftVO getRejectedActivityDraft(Long activityId){
        RejectedActivityDraftVO rejectedActivityDraftVO = new RejectedActivityDraftVO();
        rejectedActivityDraftVO.setActivityVO(getDraftActivityPart(activityId));
        rejectedActivityDraftVO.setScheduleVO(getDraftActivitySchedulePart(activityId));
        rejectedActivityDraftVO.setInitialReviewer(
                reviewLogService.getInitialReviewerIdNameByActivityId(activityId));
        rejectedActivityDraftVO.setCanEnrollCollege(
                canEnrollCollegeService.listIdNameByActivityId(activityId));
        rejectedActivityDraftVO.setCanEnrollTribe(
                canEnrollTribeService.getIdNameByActivityId(activityId));
        rejectedActivityDraftVO.setCanEnrollGrade(
                canEnrollGradeService.getIdNameByActivityId(activityId));
        rejectedActivityDraftVO.setEditActivityDraftSelectionsVO(
                getEditActivityDraftSelections());
        return rejectedActivityDraftVO;

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

    public EditActivityDraftSelectionsVO getEditActivityDraftSelections(){
        portalUserValidator.validateIsCurrentUserPortal();
        Long userId = SmartRequestUtil.getRequestUserId();
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        Long schoolId = portalUser.getSchoolId();

        EditActivityDraftSelectionsVO vo = new EditActivityDraftSelectionsVO();

        vo.setSelectableCollegeVOList(collegeInfoService.getIdNameBySchoolId(schoolId));

        vo.setSelectableOrganizationVOList(organizationInfoService.getIdNameBySchoolId(schoolId));

        vo.setSelectableCollegeReviewerList(
                backendUserService.getCollegeReviewerListMap(
                        collegeInfoService.getIdsBySchoolId(schoolId)));

        vo.setSelectableOrganizationReviewerList(
                backendUserService.getOrganizationReviewerListMap(
                        organizationInfoService.getIdsBySchoolId(schoolId)));

        vo.setSelectableCategoryVOList(categoryService.getAll());

        vo.setSelectableGradeVOList(gradeInfoService.getAll());

        return vo;
    }

    public ResponseDTO<Page<ActivityWithScheduleVO>> notStartAndPendingEnrollActivityPageGlobal(Long pageNum, Long pageSize){
        Page<ActivityWithScheduleVO> page = new Page<>(pageNum, pageSize);
        Page<ActivityWithScheduleVO> result =activityDao.notStartAndPendingEnrollActivityGlobal(page);
        return ResponseDTO.ok(result);
    }

    public ResponseDTO<Page<ActivityWithScheduleVO>> notStartAndPendingEnrollActivityPage(Long pageNum, Long pageSize){
        Page<ActivityWithScheduleVO> page = new Page<>(pageNum, pageSize);
        Long userId = SmartRequestUtil.getRequestUserId();
        PortalUserEntity portalUserEntity =portalUserManager.getById(userId);
        if(portalUserEntity==null||portalUserEntity.getDeletedFlag()) {
            return ResponseDTO.error(UnexpectedErrorCode.BUSINESS_HANDING, "用户不存在");
        }
        Long schoolId = portalUserEntity.getSchoolId();
        Page<ActivityWithScheduleVO> result =activityDao.notStartAndPendingEnrollActivity(page,schoolId);
        return ResponseDTO.ok(result);
    }

    public ActivityDetailVO detail(Long activityId){
        log.info("开始查询活动详情，activityId: {}", activityId);

        ActivityEntity activity = activityManager.getById(activityId);
        if(activity==null||activity.getDeletedFlag()){
            log.warn("活动不存在或已删除，activityId: {}", activityId);
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"活动不存在");
        }
        log.debug("查询到活动基本信息，title: {}", activity.getTitle());

        ActivityScheduleEntity activitySchedule = activityScheduleManager.getByActivityId(activityId);
        log.debug("查询到活动时间表信息");

        ActivityCategoryEntity activityCategory = activityCategoryManager.getById(activity.getCategoryId());
        log.debug("查询到活动分类信息，category: {}", activityCategory.getName());

        ActivityEnrollNum activityEnrollNum = activityEnrollNumDao.selectById(activityId);
        log.debug("查询到活动报名人数，enrollNum: {}", activityEnrollNum.getEnrollNum());

        List<Long> collegeIds = canEnrollCollegeService.getCollegeIdsByActivityId(activityId);
        List<Long> tribeIds = canEnrollTribeService.getTribeIdsByActivityId(activityId);
        List<Long> gradeIds = canEnrollGradeService.getGradeIdByActivityId(activityId);
        log.debug("查询到报名限制条件 - collegeIds: {}, tribeIds: {}, gradeIds: {}",
                collegeIds, tribeIds, gradeIds);

        List<String> collegeNames=null;
        List<String> tribeNames=null;
        List<String> gradeNames=null;
        List<EnrollerVO> enrollers = new ArrayList<>();
        if(gradeIds!=null&&collegeIds!=null){
            collegeNames=collegeInfoManager
                    .listByIds(collegeIds)
                    .stream()
                    .map(CollegeInfoEntity::getName)
                    .toList();
            gradeNames = gradeInfoManager
                    .listByIds(gradeIds)
                    .stream()
                    .map(GradeInfoEntity::getName)
                    .toList();
            log.debug("转换学院和年级名称 - colleges: {}, grades: {}", collegeNames, gradeNames);
        }
        if(tribeIds!=null){
            tribeNames = tribeManager
                    .listByIds(tribeIds)
                    .stream()
                    .map(TribeEntity::getName)
                    .toList();
            log.debug("转换部落名称 - tribes: {}", tribeNames);
        }

        // 构建查询条件：获取活动报名用户 ID 及签到状态
        LambdaQueryWrapper<ActivityEnrollmentEntity> activityEnrollmentQw =
                ActivityEnrollmentService.listByActivityIdQw(activityId)
                .select(ActivityEnrollmentEntity::getUserId)
                .select(ActivityEnrollmentEntity::getSignInStatus);
        List<ActivityEnrollmentEntity> enrollmentEntities = enrollmentManager.list(activityEnrollmentQw);
        log.debug("查询到活动报名记录数：{}", enrollmentEntities != null ? enrollmentEntities.size() : 0);

        // 将报名用户列表转换为 Map<userId, signInStatus>，便于快速查找签到状态
        if (enrollmentEntities != null && !enrollmentEntities.isEmpty()) {
            Map<Long, Boolean> userSignInStatus = enrollmentEntities.stream().collect(Collectors.toMap(
                    ActivityEnrollmentEntity::getUserId,
                    ActivityEnrollmentEntity::getSignInStatus
            ));
            log.debug("已构建用户签到状态 Map，大小：{}", userSignInStatus.size());

            // 提取所有报名用户的 ID 列表
            List<Long> enrollerIds = enrollmentEntities
                    .stream()
                    .map(ActivityEnrollmentEntity::getUserId)
                    .toList();
            log.debug("提取到报名用户 ID 列表，数量：{}", enrollerIds.size());

            // 批量查询 Portal 用户信息
            List<PortalUserEntity> portalUsers = portalUserManager.listByIds(enrollerIds);
            log.debug("查询到 Portal 用户信息，数量：{}", portalUsers != null ? portalUsers.size() : 0);

            LambdaQueryWrapper<ActivitySigninManagerEntity> signInManagerQw = new LambdaQueryWrapper<>();
            signInManagerQw.eq(ActivitySigninManagerEntity::getActivityId,activityId)
                            .eq(ActivitySigninManagerEntity::getDeletedFlag,false)
                            .select(ActivitySigninManagerEntity::getPortalUserId);
            List<ActivitySigninManagerEntity> signinManagerList = signinManagerManager.list(signInManagerQw);
            List<Long> signinManagerIdList;
            if(signinManagerList!=null&&!signinManagerList.isEmpty()){
                signinManagerIdList = signinManagerList.stream().map(ActivitySigninManagerEntity::getPortalUserId).toList();
                log.debug("查询到活动签到管理员列表，数量：{}", signinManagerIdList.size());
            } else {
                signinManagerIdList = null;
                log.debug("该活动没有签到管理员，activityId: {}", activityId);
            }
            // 组装 EnrollerVO 对象并设置对应的签到状态
            portalUsers.forEach(e -> {
                EnrollerVO enroller = new EnrollerVO();
                enroller.setId(e.getId());
                enroller.setName(e.getUsername());
                enroller.setAvatarKey(e.getAvatar());
                // 从 Map 中获取该用户的签到状态
                enroller.setSignInStatus(userSignInStatus.get(e.getId()));
                if(activity.getActivityManagerId().equals(e.getId())){
                    enroller.setActivityManagerFlag(true);
                }
                if(signinManagerIdList!=null&&signinManagerIdList.contains(e.getId())){
                    enroller.setSigninManagerFlag(true);
                }
                enrollers.add(enroller);
            });
            log.debug("已构建报名用户 VO 列表，数量：{}", enrollers.size());
        } else {
            log.debug("该活动暂无报名用户，activityId: {}", activityId);
        }
        //统计签到人数
        LambdaQueryWrapper<ActivityEnrollmentEntity> countSignIn = new LambdaQueryWrapper<>();
        countSignIn.eq(ActivityEnrollmentEntity::getActivityId,activityId)
                    .eq(ActivityEnrollmentEntity::getSignInStatus,true)
                    .eq(ActivityEnrollmentEntity::getDeletedFlag,false);
        long count = enrollmentManager.count(countSignIn);
        log.debug("统计签到人数：{}", count);

        ActivityDetailVO activityDetailVO = new ActivityDetailVO();
        activityDetailVO.setId(activityId);
        activityDetailVO.setTitle(activity.getTitle());
        activityDetailVO.setStatus(activity.getStatus());
        activityDetailVO.setPosition(activityDetailVO.getPosition());
        activityDetailVO.setScoreCanGet(activity.getScoreCanGet());
        activityDetailVO.setEnrollNumLimit(activity.getEnrollNumLimit());
        activityDetailVO.setDescription(activity.getDescription());
        activityDetailVO.setEnrollNeedReview(activity.getEnrollNeedReview());
        activityDetailVO.setNeedSignOut(activity.getNeedSignOut());
        activityDetailVO.setAttachment(activity.getAttachment());
        activityDetailVO.setCategory(activityCategory.getName());
        activityDetailVO.setCoverImg(activity.getCoverImg());
        activityDetailVO.setEnrollUsers(enrollers);
        activityDetailVO.setEnrollNum(activityEnrollNum.getEnrollNum());
        activityDetailVO.setSignInNum(count);
        activityDetailVO.setCanEnrollGrade(gradeNames);
        activityDetailVO.setCanEnrollCollege(collegeNames);
        activityDetailVO.setCanEnrollTribe(tribeNames);
        activityDetailVO.setEnrollStartTime(activitySchedule.getEnrollStartTime());
        activityDetailVO.setEnrollEndTime(activitySchedule.getEnrollEndTime());
        activityDetailVO.setActivityStartTime(activitySchedule.getActivityStartTime());
        activityDetailVO.setActivityEndTime(activitySchedule.getActivityEndTime());

        log.info("活动详情查询完成，activityId: {}, title: {}", activityId, activity.getTitle());
        return activityDetailVO;
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