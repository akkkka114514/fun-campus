package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.service.ActivityCanEnrollCollegeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.service.ActivityCanEnrollGradeService;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.service.ActivityCanEnrollTribeService;
import com.akkkka.admin.module.business.funcampus.activityCategory.service.ActivityCategoryService;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityReviewAttachment.manager.ActivityReviewAttachmentManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.manager.ActivityReviewLogManager;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.service.ActivityReviewLogService;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityEnrollNumDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityPhaseCountdownVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
import com.akkkka.admin.module.business.funcampus.organizationInfo.service.OrganizationInfoService;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.portalUser.service.PortalUserValidator;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.module.support.file.service.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 活动和时间表 组合 Service 单元测试（聚焦复杂逻辑方法）
 * <p>
 * 覆盖：phaseCountdown 八阶段倒计时（各阶段切换、首尾边界）、
 * validateEditDraftPermission 草稿编辑权限（后台审核员/前台管理员的身份与阶段判定）
 *
 * @Author akkkka114514
 * @Date 2026-09-03
 */
@ExtendWith(MockitoExtension.class)
public class ActivityWithScheduleServiceTest {

    @Mock
    private ActivityManager activityManager;
    @Mock
    private ActivityScheduleManager activityScheduleManager;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private ActivityDao activityDao;
    @Mock
    private PortalUserManager portalUserManager;
    @Mock
    private ActivityReviewLogManager activityReviewLogManager;
    @Mock
    private ActivityCanEnrollCollegeService canEnrollCollegeService;
    @Mock
    private ActivityCanEnrollTribeService canEnrollTribeService;
    @Mock
    private ActivityCanEnrollGradeService canEnrollGradeService;
    @Mock
    private PortalUserValidator portalUserValidator;
    @Mock
    private FileService fileService;
    @Mock
    private ActivityReviewAttachmentManager reviewAttachmentManager;
    @Mock
    private ActivitySigninManagerService signinManagerService;
    @Mock
    private ActivityValidator activityValidator;
    @Mock
    private ActivityScheduleValidator activityScheduleValidator;
    @Mock
    private ActivityReviewLogService reviewLogService;
    @Mock
    private ActivityCategoryService activityCategoryService;
    @Mock
    private CollegeInfoService collegeInfoService;
    @Mock
    private OrganizationInfoService organizationInfoService;
    @Mock
    private ActivityEnrollmentManager activityEnrollmentManager;
    @Mock
    private ActivityEnrollNumDao activityEnrollNumDao;
    @Mock
    private ActivityEnrollmentService activityEnrollmentService;

    @InjectMocks
    private ActivityWithScheduleService service;

    @AfterEach
    void cleanRequestUser() {
        SmartRequestUtil.remove();
    }

    private void setPortalUser(Long userId) {
        RequestPortalUser portalUser = new RequestPortalUser();
        portalUser.setId(userId);
        portalUser.setUsername("student" + userId);
        SmartRequestUtil.setRequestUser(portalUser);
    }

    private void setBackendUser(Long userId) {
        RequestBackendUser backendUser = new RequestBackendUser();
        backendUser.setId(userId);
        backendUser.setUsername("reviewer" + userId);
        SmartRequestUtil.setRequestUser(backendUser);
    }

    /**
     * 以 now 为基准构造一张时间表：报名开始于 offsetAfter 小时后，各阶段间隔 1 小时
     */
    private ActivityScheduleEntity scheduleFrom(LocalDateTime enrollStart) {
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setEnrollStartTime(enrollStart);
        schedule.setEnrollEndTime(enrollStart.plusHours(1));
        schedule.setActivityStartTime(enrollStart.plusHours(2));
        schedule.setActivityEndTime(enrollStart.plusHours(3));
        schedule.setSigninStartTime(enrollStart.plusHours(4));
        schedule.setSigninEndTime(enrollStart.plusHours(5));
        schedule.setSignoutStartTime(enrollStart.plusHours(6));
        schedule.setSignoutEndTime(enrollStart.plusHours(7));
        return schedule;
    }

    // ---------------------------------- 阶段倒计时 phaseCountdown ----------------------------------

    @Test
    void phaseCountdown_whenScheduleNotExist_throwParamError() {
        when(activityScheduleManager.getById(1L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.phaseCountdown(1L));
        assertTrue(ex.getMessage().contains("活动时间表不存在"));
    }

    @Test
    void phaseCountdown_whenBeforeEnrollStart_currentPhaseNotStarted() {
        LocalDateTime enrollStart = LocalDateTime.now().plusHours(2);
        when(activityScheduleManager.getById(1L)).thenReturn(scheduleFrom(enrollStart));

        ActivityPhaseCountdownVO vo = service.phaseCountdown(1L);

        assertEquals("未开始", vo.getCurrentPhase());
        assertEquals("报名开始", vo.getNextPhase());
        assertTrue(vo.getRemainingSeconds() > 0);
        assertEquals(enrollStart.toString(), vo.getNextPhaseStartTime());
    }

    @Test
    void phaseCountdown_whenInEnrollPeriod_currentPhaseEnrollStart() {
        // enrollStart=now-30min，enrollEnd=now+30min：now 处于报名开始阶段内
        LocalDateTime now = LocalDateTime.now();
        when(activityScheduleManager.getById(1L)).thenReturn(scheduleFrom(now.minusMinutes(30)));

        ActivityPhaseCountdownVO vo = service.phaseCountdown(1L);

        assertEquals("报名开始", vo.getCurrentPhase());
        assertEquals("报名结束", vo.getNextPhase());
        // 下一阶段为 30 分钟后，允许秒级误差
        assertTrue(Math.abs(vo.getRemainingSeconds() - Duration.ofMinutes(30).getSeconds()) < 5);
    }

    @Test
    void phaseCountdown_atExactEnrollEndTime_currentPhaseEnrollEnd() {
        // now 恰好等于报名结束时刻：最后一个满足 startTime<=now 的阶段是"报名结束"
        LocalDateTime now = LocalDateTime.now();
        ActivityScheduleEntity schedule = scheduleFrom(now.minusHours(1));
        schedule.setEnrollEndTime(now);
        when(activityScheduleManager.getById(1L)).thenReturn(schedule);

        ActivityPhaseCountdownVO vo = service.phaseCountdown(1L);

        assertEquals("报名结束", vo.getCurrentPhase());
        assertEquals("活动开始", vo.getNextPhase());
    }

    @Test
    void phaseCountdown_whenInActivityPeriod_currentPhaseActivityStart() {
        // activityStart=now-30min，activityEnd=now+30min：now 处于活动进行阶段内
        LocalDateTime now = LocalDateTime.now();
        when(activityScheduleManager.getById(1L)).thenReturn(scheduleFrom(now.minusHours(2).minusMinutes(30)));

        ActivityPhaseCountdownVO vo = service.phaseCountdown(1L);

        assertEquals("活动开始", vo.getCurrentPhase());
        assertEquals("活动结束", vo.getNextPhase());
        assertTrue(Math.abs(vo.getRemainingSeconds() - Duration.ofMinutes(30).getSeconds()) < 5);
    }

    @Test
    void phaseCountdown_whenInSignOutPeriod_currentPhaseSignOutStart() {
        // signoutStart=now-30min，signoutEnd=now+30min：now 处于签退开始阶段内
        LocalDateTime now = LocalDateTime.now();
        when(activityScheduleManager.getById(1L)).thenReturn(scheduleFrom(now.minusHours(6).minusMinutes(30)));

        ActivityPhaseCountdownVO vo = service.phaseCountdown(1L);

        assertEquals("签退开始", vo.getCurrentPhase());
        assertEquals("签退结束", vo.getNextPhase());
        assertTrue(Math.abs(vo.getRemainingSeconds() - Duration.ofMinutes(30).getSeconds()) < 5);
    }

    @Test
    void phaseCountdown_whenAllFinished_currentPhaseFinished() {
        when(activityScheduleManager.getById(1L)).thenReturn(scheduleFrom(LocalDateTime.now().minusHours(8)));

        ActivityPhaseCountdownVO vo = service.phaseCountdown(1L);

        assertEquals("已结束", vo.getCurrentPhase());
        assertNull(vo.getNextPhase());
        assertEquals(-1L, vo.getRemainingSeconds());
    }

    // ---------------------------------- 草稿编辑权限 validateEditDraftPermission ----------------------------------

    private ActivityEntity activityManagedBy(Long managerId) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(7L);
        activity.setActivityManagerId(managerId);
        return activity;
    }

    @Test
    void validateEditDraftPermission_backendReviewerInOwnReviewStage_pass() {
        setBackendUser(30L);
        when(reviewLogService.getCurReviewStage(7L)).thenReturn(ActivityReviewStage.INITIAL_CONTENT_REVIEW);
        when(reviewLogService.getCurReviewer(7L)).thenReturn(30L);

        assertDoesNotThrow(() -> service.validateEditDraftPermission(activityManagedBy(20L)));
    }

    @Test
    void validateEditDraftPermission_backendButStageNotContentReview_throwNoPermission() {
        setBackendUser(30L);
        when(reviewLogService.getCurReviewStage(7L)).thenReturn(ActivityReviewStage.ENROLLMENT_REVIEW);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validateEditDraftPermission(activityManagedBy(20L)));
        assertEquals(30005, ex.getCode());
    }

    @Test
    void validateEditDraftPermission_backendButNotCurrentReviewer_throwNoPermission() {
        setBackendUser(30L);
        when(reviewLogService.getCurReviewStage(7L)).thenReturn(ActivityReviewStage.INITIAL_CONTENT_REVIEW);
        when(reviewLogService.getCurReviewer(7L)).thenReturn(99L);

        assertThrows(BusinessException.class,
                () -> service.validateEditDraftPermission(activityManagedBy(20L)));
    }

    @Test
    void validateEditDraftPermission_portalManagerInDraftStage_pass() {
        setPortalUser(20L);
        when(reviewLogService.getCurReviewStage(7L)).thenReturn(ActivityReviewStage.DRAFT);

        assertDoesNotThrow(() -> service.validateEditDraftPermission(activityManagedBy(20L)));
    }

    @Test
    void validateEditDraftPermission_portalButNotActivityManager_throwNoPermission() {
        setPortalUser(21L);
        when(reviewLogService.getCurReviewStage(7L)).thenReturn(ActivityReviewStage.DRAFT);

        assertThrows(BusinessException.class,
                () -> service.validateEditDraftPermission(activityManagedBy(20L)));
    }

    @Test
    void validateEditDraftPermission_portalManagerButNotDraftStage_throwNoPermission() {
        setPortalUser(20L);
        when(reviewLogService.getCurReviewStage(7L)).thenReturn(ActivityReviewStage.INITIAL_CONTENT_REVIEW);

        assertThrows(BusinessException.class,
                () -> service.validateEditDraftPermission(activityManagedBy(20L)));
    }
}
