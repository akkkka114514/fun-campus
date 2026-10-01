package com.akkkka.admin.integration;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 审核流集成测试
 * <p>
 * 覆盖链路：提交（进入初审）→ 初审通过 → 审阅通过 → 终审通过（进入报名审核 + 生成管理员报名记录）；
 * 并覆盖关键边界：初审驳回删除草稿、下一审核人无审核权限拦截（校验先于事务）、
 * 终审时报名时间已开始的合规拦截。
 * <p>
 * 说明：审核流转基于 activity_review_log 日志表：每个阶段一条日志，通过后写 action 并新增下一阶段日志。
 * passInitial/passFinalReview 内部会封装异常为 SYSTEM_ERROR，因此失败用例断言"抛异常 + 无副作用"而非具体文案。
 * <p>
 * author:akkkka114514
 * create at 2026-10-01
 */
@DisplayName("审核流集成测试")
class ReviewFlowIntegrationTest extends BaseIntegrationTest {

    @Resource
    private ActivityReviewLogService activityReviewLogService;

    /** 活动管理员 id（终审通过时会为其生成报名记录，必须配置） */
    private Long adminUserId;

    private BackendUserEntity initialReviewer;

    private BackendUserEntity checker;

    private BackendUserEntity finalReviewer;

    private BackendUserEntity enrollReviewer;

    private BackendUserEntity reviewerWithoutPermission;

    private ActivityEntity activity;

    @BeforeEach
    void setUp() {
        long suffix = System.nanoTime();
        adminUserId = createPortalUser("review-manager-" + suffix).getId();

        initialReviewer = createBackendUser("reviewer-initial-" + suffix, true);
        checker = createBackendUser("reviewer-check-" + suffix, true);
        finalReviewer = createBackendUser("reviewer-final-" + suffix, true);
        enrollReviewer = createBackendUser("reviewer-enroll-" + suffix, true);
        reviewerWithoutPermission = createBackendUser("reviewer-noperm-" + suffix, false);

        // 活动与时间表：报名/活动时间全部在未来（终审校验要求报名开始时间未到）
        activity = newActivity("审核活动-" + suffix, ActivityStatus.WAIT_ENROLL);
        activity.setActivityManagerId(adminUserId);
        saveActivity(activity);
        LocalDateTime now = LocalDateTime.now();
        saveSchedule(activity.getId(), now.plusHours(1), now.plusHours(2), now.plusHours(3), now.plusHours(4));
    }

    @Test
    @DisplayName("完整审核链路：提交→初审通过→审阅通过→终审通过（进入报名审核）")
    void fullReviewFlow_initialToFinalPass() {
        // ---- 1. 提交：进入初审（action 为空表示该阶段进行中） ----
        activityReviewLogService.initialReview(null,
                buildAddForm(initialReviewer, ActivityReviewStage.DRAFT));
        List<ActivityReviewLogEntity> logs = logsOf();
        assertEquals(1, logs.size());
        assertEquals(ActivityReviewStage.INITIAL_CONTENT_REVIEW, logs.get(0).getReviewStage());
        assertNull(logs.get(0).getAction());

        // ---- 2. 初审通过：写 action + 新增审阅阶段日志 ----
        activityReviewLogService.passInitial(null,
                buildUpdateForm(logs.get(0).getId(), initialReviewer, ActivityReviewEvent.INITIAL_REVIEW_PASS),
                buildAddForm(checker, ActivityReviewStage.CONTENT_CHECK));
        logs = logsOf();
        assertEquals(2, logs.size());
        assertEquals(ActivityReviewEvent.INITIAL_REVIEW_PASS, logs.get(0).getAction());
        assertEquals(ActivityReviewStage.CONTENT_CHECK, logs.get(1).getReviewStage());
        assertNull(logs.get(1).getAction());

        // ---- 3. 审阅通过：写 action + 新增终审阶段日志 ----
        activityReviewLogService.check(
                buildUpdateForm(logs.get(1).getId(), checker, ActivityReviewEvent.CHECK_PASS),
                buildAddForm(finalReviewer, ActivityReviewStage.FINAL_CONTENT_REVIEW));
        logs = logsOf();
        assertEquals(3, logs.size());
        assertEquals(ActivityReviewEvent.CHECK_PASS, logs.get(1).getAction());
        assertEquals(ActivityReviewStage.FINAL_CONTENT_REVIEW, logs.get(2).getReviewStage());
        assertNull(logs.get(2).getAction());

        // ---- 4. 终审通过：写 action + 新增报名审核阶段日志 + 生成管理员报名记录 ----
        activityReviewLogService.passFinalReview(
                buildUpdateForm(logs.get(2).getId(), finalReviewer, ActivityReviewEvent.FINAL_REVIEW_PASS),
                buildAddForm(enrollReviewer, ActivityReviewStage.ENROLLMENT_REVIEW),
                null);
        logs = logsOf();
        assertEquals(4, logs.size());
        assertEquals(ActivityReviewEvent.FINAL_REVIEW_PASS, logs.get(2).getAction());
        assertEquals(ActivityReviewStage.ENROLLMENT_REVIEW, logs.get(3).getReviewStage());
        assertNull(logs.get(3).getAction());

        // 管理员报名记录：signInStatus=true（终审通过即视为已完成签到）
        ActivityEnrollmentEntity adminEnrollment = enrollmentRow(activity.getId(), adminUserId);
        assertNotNull(adminEnrollment, "终审通过应为活动管理员生成报名记录");
        assertTrue(adminEnrollment.getSignInStatus());
        assertFalse(adminEnrollment.getDeletedFlag());
    }

    @Test
    @DisplayName("初审驳回：日志逻辑删除 + 活动草稿删除")
    void rejectInitial_deletesDraft() {
        activityReviewLogService.initialReview(null,
                buildAddForm(initialReviewer, ActivityReviewStage.DRAFT));
        Long initialLogId = logsOf().get(0).getId();

        ActivityReviewLogUpdateForm rejectForm = buildUpdateForm(initialLogId, initialReviewer,
                ActivityReviewEvent.INITIAL_REVIEW_REJECT);
        rejectForm.setRejectReason("内容不合规，请修改后重新提交");
        activityReviewLogService.rejectInitial(rejectForm);

        // 日志逻辑删除
        ActivityReviewLogEntity rejectedLog = activityReviewLogManager.getById(initialLogId);
        assertTrue(rejectedLog.getDeletedFlag(), "驳回后审核日志应逻辑删除");
        // 活动与时间表草稿删除
        assertTrue(activityManager.getById(activity.getId()).getDeletedFlag(), "驳回后活动草稿应删除");
        assertTrue(activityScheduleManager.getById(activity.getId()).getDeletedFlag(), "驳回后时间表草稿应删除");
    }

    @Test
    @DisplayName("下一审核人无审核权限：拦截且不产生任何写入")
    void passInitial_nextReviewerWithoutPermission_blocked() {
        activityReviewLogService.initialReview(null,
                buildAddForm(initialReviewer, ActivityReviewStage.DRAFT));
        Long initialLogId = logsOf().get(0).getId();

        assertThrows(BusinessException.class, () ->
                activityReviewLogService.passInitial(null,
                        buildUpdateForm(initialLogId, initialReviewer, ActivityReviewEvent.INITIAL_REVIEW_PASS),
                        buildAddForm(reviewerWithoutPermission, ActivityReviewStage.CONTENT_CHECK)));

        // 权限校验先于事务：当前日志 action 未被更新，无新日志
        List<ActivityReviewLogEntity> logs = logsOf();
        assertEquals(1, logs.size(), "校验失败不应新增日志");
        assertNull(logs.get(0).getAction(), "校验失败不应写入审核结果");
    }

    @Test
    @DisplayName("终审时报名时间已开始：合规拦截")
    void passFinalReview_whenEnrollStarted_blocked() {
        ActivityReviewLogEntity finalLog = advanceToFinalReview();

        // 将报名开始时间改为过去，触发终审前置校验
        ActivityScheduleEntity schedule = activityScheduleManager.getById(activity.getId());
        schedule.setEnrollStartTime(LocalDateTime.now().minusMinutes(10));
        activityScheduleManager.updateById(schedule);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                activityReviewLogService.passFinalReview(
                        buildUpdateForm(finalLog.getId(), finalReviewer, ActivityReviewEvent.FINAL_REVIEW_PASS),
                        buildAddForm(enrollReviewer, ActivityReviewStage.ENROLLMENT_REVIEW),
                        null));
        assertTrue(ex.getMessage().contains("报名时间已经开始"));

        // 终审结果未写入
        assertNull(activityReviewLogManager.getById(finalLog.getId()).getAction());
    }

    // ==================== 私有辅助 ====================

    /**
     * 将活动推进到「待终审」状态，返回终审阶段日志
     */
    private ActivityReviewLogEntity advanceToFinalReview() {
        activityReviewLogService.initialReview(null, buildAddForm(initialReviewer, ActivityReviewStage.DRAFT));
        ActivityReviewLogEntity initialLog = latestLog();

        activityReviewLogService.passInitial(null,
                buildUpdateForm(initialLog.getId(), initialReviewer, ActivityReviewEvent.INITIAL_REVIEW_PASS),
                buildAddForm(checker, ActivityReviewStage.CONTENT_CHECK));
        ActivityReviewLogEntity checkLog = latestLog();

        activityReviewLogService.check(
                buildUpdateForm(checkLog.getId(), checker, ActivityReviewEvent.CHECK_PASS),
                buildAddForm(finalReviewer, ActivityReviewStage.FINAL_CONTENT_REVIEW));
        ActivityReviewLogEntity finalLog = latestLog();

        assertEquals(ActivityReviewStage.FINAL_CONTENT_REVIEW, finalLog.getReviewStage());
        return finalLog;
    }

    private List<ActivityReviewLogEntity> logsOf() {
        return activityReviewLogManager.list(Wrappers.lambdaQuery(ActivityReviewLogEntity.class)
                .eq(ActivityReviewLogEntity::getActivityId, activity.getId())
                .orderByAsc(ActivityReviewLogEntity::getId));
    }

    private ActivityReviewLogEntity latestLog() {
        List<ActivityReviewLogEntity> logs = logsOf();
        return logs.get(logs.size() - 1);
    }

    private ActivityReviewLogAddForm buildAddForm(BackendUserEntity reviewer, ActivityReviewStage nextStage) {
        ActivityReviewLogAddForm form = new ActivityReviewLogAddForm();
        form.setActivityId(activity.getId());
        form.setReviewerId(reviewer.getId());
        form.setReviewerName(reviewer.getUsername());
        form.setReviewStage(nextStage);
        return form;
    }

    private ActivityReviewLogUpdateForm buildUpdateForm(Long reviewLogId, BackendUserEntity reviewer,
                                                        ActivityReviewEvent action) {
        ActivityReviewLogUpdateForm form = new ActivityReviewLogUpdateForm();
        form.setId(reviewLogId);
        form.setReviewerId(reviewer.getId());
        form.setReviewerName(reviewer.getUsername());
        form.setAction(action);
        return form;
    }
}
