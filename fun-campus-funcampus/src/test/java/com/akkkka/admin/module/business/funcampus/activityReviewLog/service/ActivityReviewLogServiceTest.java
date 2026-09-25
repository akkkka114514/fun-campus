package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;

import com.akkkka.admin.module.business.funcampus.MybatisPlusTestRegistry;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentValidator;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityRefundService;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.dto.EnrollersChangeDTO;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.manager.ActivitySigninManagerManager;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityScheduleUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.service.BackendUserValidator;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.module.support.message.constant.MessageTemplateEnum;
import com.akkkka.module.support.message.domain.MessageTemplateSendForm;
import com.akkkka.module.support.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 活动审核日志 Service 单元测试
 * <p>
 * 覆盖：当前审核阶段/审核人查询、初审发起、审核流转（update+新增下阶段日志）、
 * 驳回逻辑删除、终审通过时自动生成签到员/活动管理员的报名记录、完结审核
 * （签到/签退差异更新与学分发放）、报名审核的人员增删
 *
 * @Author akkkka114514
 * @Date 2026-09-03
 */
@ExtendWith(MockitoExtension.class)
public class ActivityReviewLogServiceTest {

    static {
        // 审核流/终审/完结事务中 select/set 会立即解析实体列
        MybatisPlusTestRegistry.register(ActivityReviewLogEntity.class, ActivityEnrollmentEntity.class);
    }

    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private ActivityWithScheduleService activityWithScheduleService;
    @Mock
    private ActivityReviewLogManager activityReviewLogManager;
    @Mock
    private ActivityManager activityManager;
    @Mock
    private ActivityScheduleManager activityScheduleManager;
    @Mock
    private ActivityEnrollmentManager enrollmentManager;
    @Mock
    private ActivitySigninManagerManager signinManagerManager;
    @Mock
    private ActivityReviewLogValidator addFormValidator;
    @Mock
    private PortalUserManager portalUserManager;
    @Mock
    private ActivityEnrollmentService enrollmentService;
    @Mock
    private ActivityEnrollmentValidator enrollmentValidator;
    @Mock
    private ActivityValidator activityValidator;
    @Mock
    private BackendUserValidator backendUserValidator;
    @Mock
    private ActivityReviewLogValidator reviewLogValidator;
    @Mock
    private ActivitySigninManagerService signinManagerService;
    @Mock
    private PortalUserValidator portalUserValidator;
    @Mock
    private MessageService messageService;
    @Mock
    private ActivityRefundService activityRefundService;

    private ActivityReviewLogService service;

    private TransactionStatus txStatus;

    /**
     * 构造器含两个 ActivityReviewLogValidator/多个同类型参数，
     * @InjectMocks 按类型注入时会随机错位，因此按字段声明顺序显式构造
     */
    @BeforeEach
    void setUp() {
        service = new ActivityReviewLogService(
                transactionTemplate, activityWithScheduleService, activityReviewLogManager,
                activityManager, activityScheduleManager, enrollmentManager, signinManagerManager,
                addFormValidator, portalUserManager, enrollmentService, enrollmentValidator,
                activityValidator, backendUserValidator, reviewLogValidator, signinManagerService,
                portalUserValidator, messageService, activityRefundService);
    }

    /** 让事务模板真正执行传入的消费逻辑，并记录事务状态 mock 供回滚断言 */
    private void runTransactionNow() {
        doAnswer(invocation -> {
            txStatus = mock(TransactionStatus.class);
            Consumer<TransactionStatus> consumer = invocation.getArgument(0);
            consumer.accept(txStatus);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
    }

    private ActivityReviewLogEntity reviewLog(Long id, Long activityId, ActivityReviewStage stage,
                                              ActivityReviewEvent action, Long reviewerId) {
        ActivityReviewLogEntity entity = new ActivityReviewLogEntity();
        entity.setId(id);
        entity.setActivityId(activityId);
        entity.setReviewStage(stage);
        entity.setAction(action);
        entity.setReviewerId(reviewerId);
        entity.setReviewerName("r" + reviewerId);
        entity.setDeletedFlag(false);
        return entity;
    }

    private ActivityReviewLogUpdateForm updateForm(Long id, ActivityReviewEvent action, Long reviewerId) {
        ActivityReviewLogUpdateForm form = new ActivityReviewLogUpdateForm();
        form.setId(id);
        form.setReviewerId(reviewerId);
        form.setReviewerName("r" + reviewerId);
        form.setAction(action);
        return form;
    }

    private ActivityReviewLogAddForm nextReviewAddForm(Long activityId, ActivityReviewStage stage) {
        ActivityReviewLogAddForm form = new ActivityReviewLogAddForm();
        form.setActivityId(activityId);
        form.setReviewerId(2L);
        form.setReviewerName("r2");
        form.setReviewStage(stage);
        return form;
    }

    private ActivityEntity activity(Long id, Long managerId, boolean needSignOut) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(id);
        activity.setActivityManagerId(managerId);
        activity.setNeedSignOut(needSignOut);
        return activity;
    }

    /** 打桩 doReviewTransaction 链中除事务外所需依赖（非完结阶段分支） */
    private void stubDoReviewChain(ActivityReviewLogEntity curLog, ActivityReviewLogAddForm nextForm) {
        when(reviewLogValidator.validateReviewLogId(curLog.getId())).thenReturn(curLog);
        if (!ActivityReviewStage.COMPLETION_REVIEW.equals(curLog.getReviewStage())) {
            when(activityValidator.validateActivityId(nextForm.getActivityId())).thenReturn(new ActivityEntity());
            BackendUserEntity backendUser = new BackendUserEntity();
            backendUser.setId(nextForm.getReviewerId());
            backendUser.setUsername(nextForm.getReviewerName());
            backendUser.setCanReview(true);
            when(backendUserValidator.validateBackendUserId(nextForm.getReviewerId())).thenReturn(backendUser);
        }
    }

    // ---------------------------------- 查询类 ----------------------------------

    @Test
    void getCurReviewStage_whenNoLog_returnDraft() {
        when(activityReviewLogManager.getOne(any())).thenReturn(null);
        assertEquals(ActivityReviewStage.DRAFT, service.getCurReviewStage(7L));
    }

    @Test
    void getCurReviewStage_whenHasLog_returnItsStage() {
        when(activityReviewLogManager.getOne(any()))
                .thenReturn(reviewLog(1L, 7L, ActivityReviewStage.CONTENT_CHECK, null, 2L));
        assertEquals(ActivityReviewStage.CONTENT_CHECK, service.getCurReviewStage(7L));
    }

    @Test
    void getCurReviewer_whenNoLog_returnNull() {
        when(activityReviewLogManager.getOne(any())).thenReturn(null);
        assertNull(service.getCurReviewer(7L));
    }

    @Test
    void getCurReviewer_whenHasLog_returnReviewerId() {
        when(activityReviewLogManager.getOne(any()))
                .thenReturn(reviewLog(1L, 7L, ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 30L));
        assertEquals(30L, service.getCurReviewer(7L));
    }

    @Test
    void getLatestReviewLog_returnLatestNotDeletedLog() {
        when(activityReviewLogManager.getOne(any()))
                .thenReturn(reviewLog(1L, 7L, ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 2L));
        ActivityReviewLogEntity result = service.getLatestReviewLog(7L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // ---------------------------------- 初审发起 initialReview ----------------------------------

    @Test
    void initialReview_withUpdateForm_editDraftFirstThenSaveInitialLog() {
        ActivityReviewLogAddForm addForm = nextReviewAddForm(7L, ActivityReviewStage.INITIAL_CONTENT_REVIEW);
        ActivityWithScheduleUpdateForm scheduleUpdate = new ActivityWithScheduleUpdateForm();
        when(activityReviewLogManager.save(any())).thenReturn(true);

        service.initialReview(scheduleUpdate, addForm);

        verify(activityWithScheduleService).editActivityDraft(7L, scheduleUpdate);
        ArgumentCaptor<ActivityReviewLogEntity> captor = ArgumentCaptor.forClass(ActivityReviewLogEntity.class);
        verify(activityReviewLogManager).save(captor.capture());
        ActivityReviewLogEntity saved = captor.getValue();
        assertEquals(ActivityReviewStage.INITIAL_CONTENT_REVIEW, saved.getReviewStage());
        assertEquals(7L, saved.getActivityId());
        assertEquals(false, saved.getDeletedFlag());
        assertNull(saved.getAction());
    }

    @Test
    void initialReview_withoutUpdateForm_onlySaveInitialLog() {
        ActivityReviewLogAddForm addForm = nextReviewAddForm(7L, ActivityReviewStage.INITIAL_CONTENT_REVIEW);
        when(activityReviewLogManager.save(any())).thenReturn(true);

        service.initialReview(null, addForm);

        verify(activityWithScheduleService, never()).editActivityDraft(any(), any());
        verify(activityReviewLogManager).save(any());
    }

    // ---------------------------------- 审核流转 doReviewTransaction ----------------------------------

    @Test
    void check_pass_updateCurrentLogAndAppendNextStageLog() {
        ActivityReviewLogUpdateForm curForm = updateForm(1L, ActivityReviewEvent.CHECK_PASS, 2L);
        ActivityReviewLogAddForm nextForm = nextReviewAddForm(7L, ActivityReviewStage.FINAL_CONTENT_REVIEW);
        // 当前阶段为审阅，最新审核日志模拟来自数据库
        ActivityReviewLogEntity curLog = reviewLog(1L, 7L, ActivityReviewStage.CONTENT_CHECK, null, 2L);
        stubDoReviewChain(curLog, nextForm);
        runTransactionNow();
        when(activityReviewLogManager.update(any())).thenReturn(true);
        when(activityReviewLogManager.save(any())).thenReturn(true);

        service.check(curForm, nextForm);

        // 旧日志补上 action
        ArgumentCaptor<com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper> updateCaptor =
                ArgumentCaptor.forClass(com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper.class);
        verify(activityReviewLogManager).update(updateCaptor.capture());
        // 追加下一阶段日志
        ArgumentCaptor<ActivityReviewLogEntity> saveCaptor = ArgumentCaptor.forClass(ActivityReviewLogEntity.class);
        verify(activityReviewLogManager).save(saveCaptor.capture());
        ActivityReviewLogEntity nextLog = saveCaptor.getValue();
        assertEquals(7L, nextLog.getActivityId());
        assertEquals(ActivityReviewStage.FINAL_CONTENT_REVIEW, nextLog.getReviewStage());
        assertEquals(2L, nextLog.getReviewerId());
    }

    // ---------------------------------- 驳回 ----------------------------------

    @Test
    void rejectInitial_logicalDeleteReviewLogAndDeleteDraft() {
        ActivityReviewLogUpdateForm curForm = updateForm(1L, ActivityReviewEvent.INITIAL_REVIEW_REJECT, 2L);
        when(reviewLogValidator.validateReviewLogId(1L))
                .thenReturn(reviewLog(1L, 7L, ActivityReviewStage.INITIAL_CONTENT_REVIEW, null, 2L));
        runTransactionNow();
        when(activityReviewLogManager.updateById(any())).thenReturn(true);

        service.rejectInitial(curForm);

        ArgumentCaptor<ActivityReviewLogEntity> captor = ArgumentCaptor.forClass(ActivityReviewLogEntity.class);
        verify(activityReviewLogManager).updateById(captor.capture());
        assertEquals(true, captor.getValue().getDeletedFlag());
        verify(activityWithScheduleService).deleteDraft(7L);
    }

    @Test
    void rejectFinal_logicalDeleteAndReturnActivityId() {
        ActivityReviewLogUpdateForm curForm = updateForm(1L, ActivityReviewEvent.FINAL_REVIEW_REJECT, 2L);
        when(reviewLogValidator.validateReviewLogId(1L))
                .thenReturn(reviewLog(1L, 7L, ActivityReviewStage.FINAL_CONTENT_REVIEW, null, 2L));
        runTransactionNow();
        when(activityReviewLogManager.updateById(any())).thenReturn(true);

        Long activityId = service.rejectFinal(curForm);

        assertEquals(7L, activityId);
        verify(activityWithScheduleService).deleteDraft(7L);
    }

    // ---------------------------------- 终审通过 passFinalReview ----------------------------------

    @Test
    void passFinalReview_whenDbEnrollTimeInPast_throw() {
        // 未携带 updateForm 时校验数据库时间表：报名开始时间已过则拒绝通过
        ActivityReviewLogUpdateForm curForm = updateForm(1L, ActivityReviewEvent.FINAL_REVIEW_PASS, 2L);
        ActivityReviewLogAddForm nextForm = nextReviewAddForm(7L, ActivityReviewStage.COMPLETION_REVIEW);
        when(activityScheduleManager.getById(7L)).thenReturn(scheduleWithEnrollStart(LocalDateTime.now().minusDays(1)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.passFinalReview(curForm, nextForm, null));
        assertTrue(ex.getMessage().contains("报名时间已经开始或已经结束"));
    }

    private ActivityScheduleEntity scheduleWithEnrollStart(LocalDateTime enrollStart) {
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setEnrollStartTime(enrollStart);
        schedule.setEnrollEndTime(enrollStart.plusHours(1));
        return schedule;
    }

    @Test
    void passFinalReview_generateManagerAndSignInManagerEnrollments() {
        ActivityReviewLogUpdateForm curForm = updateForm(1L, ActivityReviewEvent.FINAL_REVIEW_PASS, 2L);
        ActivityReviewLogAddForm nextForm = nextReviewAddForm(7L, ActivityReviewStage.COMPLETION_REVIEW);

        // updateForm：把活动管理员换成 8，签到员换成 [2,3]，报名与活动时间均在未来
        ActivityWithScheduleUpdateForm withScheduleUpdate = new ActivityWithScheduleUpdateForm();
        ActivityUpdateForm activityUpdate = new ActivityUpdateForm();
        activityUpdate.setNeedSignOut(true);
        activityUpdate.setActivityManagerId(8L);
        withScheduleUpdate.setActivityUpdateForm(activityUpdate);
        ActivityScheduleUpdateForm scheduleUpdate = new ActivityScheduleUpdateForm();
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        scheduleUpdate.setEnrollStartTime(future);
        scheduleUpdate.setEnrollEndTime(future.plusHours(1));
        withScheduleUpdate.setActivityScheduleUpdateForm(scheduleUpdate);
        withScheduleUpdate.setActivitySigninManagerIdList(new ArrayList<>(List.of(2L, 3L)));

        // 数据库中的活动原管理员为 9、原签到员为 [1,2]、需要签退
        when(activityManager.getById(7L)).thenReturn(activity(7L, 9L, true));
        when(signinManagerService.getSignInManagerIds(7L)).thenReturn(List.of(1L, 2L));
        when(enrollmentManager.saveBatch(any())).thenReturn(true);

        // 当前阶段为报名审核：跳过 doReviewTransaction 中的活动/后台用户强校验
        stubDoReviewChain(reviewLog(1L, 7L, ActivityReviewStage.ENROLLMENT_REVIEW, null, 2L), nextForm);
        runTransactionNow();
        when(activityReviewLogManager.update(any())).thenReturn(true);
        when(activityReviewLogManager.save(any())).thenReturn(true);

        service.passFinalReview(curForm, nextForm, withScheduleUpdate);

        verify(activityWithScheduleService).editActivityDraft(7L, withScheduleUpdate);
        ArgumentCaptor<List<ActivityEnrollmentEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(enrollmentManager).saveBatch(captor.capture());
        List<ActivityEnrollmentEntity> enrollments = captor.getValue();
        // 签到员 [2,3] + 活动管理员 8
        assertEquals(3, enrollments.size());
        List<Long> userIds = enrollments.stream().map(ActivityEnrollmentEntity::getUserId).toList();
        assertTrue(userIds.containsAll(List.of(2L, 3L, 8L)));
        for (ActivityEnrollmentEntity enrollment : enrollments) {
            assertEquals(7L, enrollment.getActivityId());
            assertEquals(true, enrollment.getSignInStatus());
            // needSignOut=true：自动成为需要签退的记录
            assertEquals(true, enrollment.getSignOutStatus());
            assertEquals(false, enrollment.getDeletedFlag());
        }
    }

    @Test
    void passFinalReview_updateFormNull_keepDbManagersAndSignOutFlag() {
        ActivityReviewLogUpdateForm curForm = updateForm(1L, ActivityReviewEvent.FINAL_REVIEW_PASS, 2L);
        ActivityReviewLogAddForm nextForm = nextReviewAddForm(7L, ActivityReviewStage.COMPLETION_REVIEW);

        // updateForm 为 null：报名时间校验走数据库时间表（未来时间）
        when(activityScheduleManager.getById(7L))
                .thenReturn(scheduleWithEnrollStart(LocalDateTime.now().plusDays(1)));
        // 数据库管理员 9、签到员 [1,2]、需要签退
        when(activityManager.getById(7L)).thenReturn(activity(7L, 9L, true));
        when(signinManagerService.getSignInManagerIds(7L)).thenReturn(List.of(1L, 2L));
        when(enrollmentManager.saveBatch(any())).thenReturn(true);
        stubDoReviewChain(reviewLog(1L, 7L, ActivityReviewStage.ENROLLMENT_REVIEW, null, 2L), nextForm);
        runTransactionNow();
        when(activityReviewLogManager.update(any())).thenReturn(true);
        when(activityReviewLogManager.save(any())).thenReturn(true);

        service.passFinalReview(curForm, nextForm, null);

        verify(activityWithScheduleService, never()).editActivityDraft(any(), any());
        ArgumentCaptor<List<ActivityEnrollmentEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(enrollmentManager).saveBatch(captor.capture());
        List<Long> userIds = captor.getValue().stream().map(ActivityEnrollmentEntity::getUserId).toList();
        assertEquals(List.of(1L, 2L, 9L), userIds);
    }

    // ---------------------------------- 完结审核 endReview ----------------------------------

    @Test
    void endReview_whenReviewLogMissing_throwParamError() {
        when(activityReviewLogManager.getById(1L)).thenReturn(null);
        assertThrows(BusinessException.class,
                () -> service.endReview(1L, List.of(), List.of(), Map.of()));
    }

    @Test
    void endReview_applySignInSignOutDiffAndGradeScores() {
        when(activityReviewLogManager.getById(1L))
                .thenReturn(reviewLog(1L, 7L, ActivityReviewStage.COMPLETION_REVIEW, null, 2L));
        // 数据库已签到用户 [2]，表单签到用户 [1,2,3] → 需要把 1、3 补成已签到
        // 数据库已签退用户 [5]，表单签退用户为空 → 需要把 5 置为未签退
        ActivityEnrollmentEntity signedIn = new ActivityEnrollmentEntity();
        signedIn.setUserId(2L);
        signedIn.setSignInStatus(true);
        ActivityEnrollmentEntity signedOut = new ActivityEnrollmentEntity();
        signedOut.setUserId(5L);
        signedOut.setSignOutStatus(true);
        when(enrollmentManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(signedIn), List.of(signedOut));
        runTransactionNow();
        when(enrollmentManager.update(any())).thenReturn(true);
        when(portalUserManager.updateBatchById(any())).thenReturn(true);

        service.endReview(1L, List.of(1L, 2L, 3L), List.of(),
                Map.of(9L, new BigDecimal("2.5")));

        verify(enrollmentManager, times(4)).update(any());
        ArgumentCaptor<List<com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity>> scoreCaptor =
                ArgumentCaptor.forClass(List.class);
        verify(portalUserManager).updateBatchById(scoreCaptor.capture());
        assertEquals(1, scoreCaptor.getValue().size());
        assertEquals(9L, scoreCaptor.getValue().get(0).getId());
        assertEquals(new BigDecimal("2.5"), scoreCaptor.getValue().get(0).getGradeScore());
    }

    @Test
    void endReview_whenDbUpdateFails_rollbackAndThrow() {
        when(activityReviewLogManager.getById(1L))
                .thenReturn(reviewLog(1L, 7L, ActivityReviewStage.COMPLETION_REVIEW, null, 2L));
        ActivityEnrollmentEntity signedIn = new ActivityEnrollmentEntity();
        signedIn.setUserId(2L);
        signedIn.setSignInStatus(true);
        when(enrollmentManager.list(any(LambdaQueryWrapper.class))).thenReturn(List.of(signedIn), List.of());
        runTransactionNow();
        // 第 2 次 update（清除多余已签到标记）失败
        when(enrollmentManager.update(any())).thenReturn(true, false, true, true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.endReview(1L, List.of(1L), List.of(), Map.of()));
        assertTrue(ex.getMessage().contains("终审事务失败"));
        verify(txStatus).setRollbackOnly();
        verify(portalUserManager, never()).updateBatchById(any());
    }

    // ---------------------------------- 报名审核 reviewEnroll ----------------------------------

    @Test
    void reviewEnroll_addNewEnrollersAndRemoveMissingEnrollers() {
        ActivityReviewLogUpdateForm curForm = updateForm(1L, null, 2L);
        // 表单报名者 [1,2,4]，数据库报名者 [1,2,3] → 新增 4、删除 3
        List<Long> formIds = new ArrayList<>(List.of(1L, 2L, 4L));
        ActivityReviewLogAddForm nextForm = stubSuccessfulEnrollReview(formIds);

        service.reviewEnroll(curForm, nextForm, formIds);

        // 审核动作被改写为报名审核通过，清空建议/驳回原因
        assertEquals(ActivityReviewEvent.ENROLL_REVIEW_PASS, curForm.getAction());
        assertNull(curForm.getCheckRemark());
        assertNull(curForm.getRejectReason());

        ArgumentCaptor<List<ActivityEnrollmentEntity>> delCaptor = ArgumentCaptor.forClass(List.class);
        verify(enrollmentManager).updateBatchById(delCaptor.capture());
        assertEquals(1, delCaptor.getValue().size());
        assertEquals(3L, delCaptor.getValue().get(0).getUserId());
        assertEquals(true, delCaptor.getValue().get(0).getDeletedFlag());

        ArgumentCaptor<List<ActivityEnrollmentEntity>> addCaptor = ArgumentCaptor.forClass(List.class);
        verify(enrollmentManager).saveBatch(addCaptor.capture());
        assertEquals(1, addCaptor.getValue().size());
        assertEquals(4L, addCaptor.getValue().get(0).getUserId());
        assertEquals(false, addCaptor.getValue().get(0).getDeletedFlag());

        // 事务提交成功后发送报名审核结果站内信：通过 3 人（1,2,4）、未通过 1 人（3）
        ArgumentCaptor<MessageTemplateSendForm> formCaptor = ArgumentCaptor.forClass(MessageTemplateSendForm.class);
        verify(messageService, times(2)).sendTemplateMessage(formCaptor.capture());
        List<MessageTemplateSendForm> sendForms = formCaptor.getAllValues();

        assertEquals(MessageTemplateEnum.ACTIVITY_ENROLL_PASS, sendForms.get(0).getMessageTemplateEnum());
        assertEquals(UserTypeEnum.PORTAL_USER, sendForms.get(0).getReceiverUserType());
        assertEquals(formIds, sendForms.get(0).getReceiverUserIdList());
        assertEquals(7L, sendForms.get(0).getDataId());
        assertEquals("校园志愿活动", sendForms.get(0).getContentParam().get("activityTitle"));

        assertEquals(MessageTemplateEnum.ACTIVITY_ENROLL_REJECT, sendForms.get(1).getMessageTemplateEnum());
        assertEquals(List.of(3L), sendForms.get(1).getReceiverUserIdList());
    }

    @Test
    void reviewEnroll_whenEnrollerIdsEmpty_skipEnrollmentChange() {
        ActivityReviewLogUpdateForm curForm = updateForm(1L, null, 2L);
        ActivityReviewLogAddForm nextForm = nextReviewAddForm(7L, ActivityReviewStage.COMPLETION_REVIEW);
        stubDoReviewChain(reviewLog(1L, 7L, ActivityReviewStage.ENROLLMENT_REVIEW, null, 2L), nextForm);
        runTransactionNow();
        when(activityReviewLogManager.update(any())).thenReturn(true);
        when(activityReviewLogManager.save(any())).thenReturn(true);

        service.reviewEnroll(curForm, nextForm, null);

        verify(enrollmentManager, never()).saveBatch(any());
        verify(enrollmentManager, never()).updateBatchById(any());
        verify(messageService, never()).sendTemplateMessage(any(MessageTemplateSendForm.class));
    }

    @Test
    void reviewEnroll_whenNotifyFails_reviewStillSucceeds() {
        ActivityReviewLogUpdateForm curForm = updateForm(1L, null, 2L);
        List<Long> formIds = new ArrayList<>(List.of(1L, 2L, 4L));
        ActivityReviewLogAddForm nextForm = stubSuccessfulEnrollReview(formIds);
        doThrow(new RuntimeException("消息服务不可用"))
                .when(messageService).sendTemplateMessage(any(MessageTemplateSendForm.class));

        assertDoesNotThrow(() -> service.reviewEnroll(curForm, nextForm, formIds));

        // 站内信发送失败不影响审核结果落库
        assertEquals(ActivityReviewEvent.ENROLL_REVIEW_PASS, curForm.getAction());
        verify(enrollmentManager).updateBatchById(any());
        verify(enrollmentManager).saveBatch(any());
    }

    /** 打桩 reviewEnroll 成功路径：差集转换结果 + 审核链 + 站内信所需的活动标题 */
    private ActivityReviewLogAddForm stubSuccessfulEnrollReview(List<Long> formIds) {
        ActivityReviewLogAddForm nextForm = nextReviewAddForm(7L, ActivityReviewStage.COMPLETION_REVIEW);
        List<Long> dbIds = new ArrayList<>(List.of(1L, 2L, 3L));
        when(enrollmentService.listPortalUserIds(7L)).thenReturn(dbIds);

        // enrollmentService 是 mock：直接给出差集转换结果（新增 4、删除 3）
        EnrollersChangeDTO changeDTO = new EnrollersChangeDTO();
        ActivityEnrollmentEntity toDel = new ActivityEnrollmentEntity();
        toDel.setActivityId(7L);
        toDel.setUserId(3L);
        toDel.setDeletedFlag(true);
        changeDTO.getDelList().add(toDel);
        ActivityEnrollmentEntity toAdd = new ActivityEnrollmentEntity();
        toAdd.setActivityId(7L);
        toAdd.setUserId(4L);
        toAdd.setDeletedFlag(false);
        changeDTO.getAddList().add(toAdd);
        when(enrollmentService.convertToEnrollmentChanges(7L, formIds, dbIds)).thenReturn(changeDTO);

        when(enrollmentManager.updateBatchById(any())).thenReturn(true);
        when(enrollmentManager.saveBatch(any())).thenReturn(true);
        stubDoReviewChain(reviewLog(1L, 7L, ActivityReviewStage.ENROLLMENT_REVIEW, null, 2L), nextForm);
        runTransactionNow();
        when(activityReviewLogManager.update(any())).thenReturn(true);
        when(activityReviewLogManager.save(any())).thenReturn(true);

        ActivityEntity activity7 = activity(7L, 2L, false);
        activity7.setTitle("校园志愿活动");
        when(activityManager.getById(7L)).thenReturn(activity7);
        return nextForm;
    }
}
