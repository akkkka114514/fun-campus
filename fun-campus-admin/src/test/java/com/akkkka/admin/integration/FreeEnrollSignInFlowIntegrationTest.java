package com.akkkka.admin.integration;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.constant.RedisKey;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.transaction.TestTransaction;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 免费报名与签到集成测试
 * <p>
 * 覆盖链路：免费报名（记录写入 + 名额占用）→ 取消 → 重新报名复活 → 扫码签到 → 扫码签退；
 * 并覆盖关键边界：满员回滚、重复报名拦截、付费活动拦截、已签到不可取消、报名截止后不可取消、
 * 非签到员操作拒绝、签到窗口未开始拒绝。
 * <p>
 * author:akkkka114514
 * create at 2026-10-01
 */
@DisplayName("免费报名与签到集成测试")
class FreeEnrollSignInFlowIntegrationTest extends BaseIntegrationTest {

    @Resource
    private ActivityEnrollmentService activityEnrollmentService;

    private PortalUserEntity userA;

    private PortalUserEntity userB;

    private ActivityEntity freeActivity;

    @BeforeEach
    void setUp() {
        userA = createPortalUser("free-a-" + System.nanoTime());
        userB = createPortalUser("free-b-" + System.nanoTime());
        freeActivity = saveActivity(newActivity("免费活动-" + System.nanoTime(), ActivityStatus.ENROLLING));
        LocalDateTime now = LocalDateTime.now();
        saveSchedule(freeActivity.getId(), now.minusHours(1), now.plusHours(2), now.plusHours(3), now.plusHours(4));
    }

    @Test
    @DisplayName("报名成功：写入报名记录并占用名额")
    void enroll_success_createsRecordAndOccupiesSeat() {
        loginAsPortalUser(userA);

        assertDoesNotThrow(() -> activityEnrollmentService.enroll(freeActivity.getId()));

        ActivityEnrollmentEntity enrollment = enrollmentRow(freeActivity.getId(), userA.getId());
        assertNotNull(enrollment, "报名成功应写入报名记录");
        assertFalse(enrollment.getDeletedFlag());
        assertFalse(enrollment.getSignInStatus());
        assertEquals(1, enrollNumOf(freeActivity.getId()), "报名成功应占用名额");
    }

    @Test
    @DisplayName("满员回滚：抢占的报名记录一并撤销，名额不超卖")
    void enroll_whenFull_rollbacks() {
        freeActivity.setEnrollNumLimit(1);
        activityManager.updateById(freeActivity);

        loginAsPortalUser(userA);
        activityEnrollmentService.enroll(freeActivity.getId());

        loginAsPortalUser(userB);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityEnrollmentService.enroll(freeActivity.getId()));
        assertTrue(ex.getMessage().contains("人数已满"));

        // @Transactional 测试中内层 REQUIRED 事务加入外层测试事务，异常仅标记回滚（物理回滚延后到测试结束），
        // 无法断言"抢占记录已消失"；以"事务被标记回滚 + 名额不超卖"验证回滚语义
        assertTrue(TestTransaction.isFlaggedForRollback(), "满员失败应标记事务回滚，抢占的报名记录随回滚撤销");
        assertEquals(1, enrollNumOf(freeActivity.getId()), "名额保持 1，不超卖");
    }

    @Test
    @DisplayName("重复报名：被拦截")
    void enroll_duplicate_blocked() {
        loginAsPortalUser(userA);
        activityEnrollmentService.enroll(freeActivity.getId());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityEnrollmentService.enroll(freeActivity.getId()));
        assertTrue(ex.getMessage().contains("请勿重复报名"));
        assertEquals(1, enrollNumOf(freeActivity.getId()), "重复报名不应重复占座");
    }

    @Test
    @DisplayName("付费活动免费报名：被拦截")
    void enroll_paidActivity_blocked() {
        ActivityEntity paidActivity = saveActivity(
                newPaidActivity("付费活动-免费报名拦截-" + System.nanoTime(), ActivityStatus.ENROLLING, 100));
        LocalDateTime now = LocalDateTime.now();
        saveSchedule(paidActivity.getId(), now.minusHours(1), now.plusHours(2), now.plusHours(3), now.plusHours(4));

        loginAsPortalUser(userA);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityEnrollmentService.enroll(paidActivity.getId()));
        assertTrue(ex.getMessage().contains("付费活动"));
        assertEquals(0, enrollNumOf(paidActivity.getId()));
    }

    @Test
    @DisplayName("取消后重新报名：逻辑删除记录复活并重置签到状态")
    void cancel_thenReEnroll_revives() {
        loginAsPortalUser(userA);
        activityEnrollmentService.enroll(freeActivity.getId());
        assertEquals(1, enrollNumOf(freeActivity.getId()));

        // 取消报名：逻辑删除 + 释放名额
        activityEnrollmentService.cancelEnrollment(freeActivity.getId());
        assertTrue(enrollmentRow(freeActivity.getId(), userA.getId()).getDeletedFlag());
        assertEquals(0, enrollNumOf(freeActivity.getId()));

        // 重新报名：复活原记录
        activityEnrollmentService.enroll(freeActivity.getId());
        ActivityEnrollmentEntity revived = enrollmentRow(freeActivity.getId(), userA.getId());
        assertNotNull(revived);
        assertFalse(revived.getDeletedFlag(), "重新报名应复活报名记录");
        assertFalse(revived.getSignInStatus(), "复活后签到状态应重置");
        assertEquals(1, enrollNumOf(freeActivity.getId()));
    }

    @Test
    @DisplayName("报名截止后取消：被拦截")
    void cancel_afterEnrollEnd_blocked() {
        loginAsPortalUser(userA);
        activityEnrollmentService.enroll(freeActivity.getId());

        ActivityScheduleEntity schedule = activityScheduleManager.getById(freeActivity.getId());
        schedule.setEnrollEndTime(LocalDateTime.now().minusMinutes(1));
        activityScheduleManager.updateById(schedule);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityEnrollmentService.cancelEnrollment(freeActivity.getId()));
        assertTrue(ex.getMessage().contains("报名已截止"));
        assertFalse(enrollmentRow(freeActivity.getId(), userA.getId()).getDeletedFlag());
    }

    @Test
    @DisplayName("签到签退全流程：非签到员拒绝→签到成功 token 作废→已签到不可取消→签退成功")
    void signInAndSignOut_fullFlow() {
        ActivityEntity signActivity = buildSignActivity();
        PortalUserEntity signer = createPortalUser("signer-" + System.nanoTime());
        PortalUserEntity attendee = createPortalUser("attendee-" + System.nanoTime());
        addSigninManager(signActivity.getId(), signer);

        // attendee 报名
        loginAsPortalUser(attendee);
        activityEnrollmentService.enroll(signActivity.getId());

        // 模拟用户生成二维码 token（由 signInQRCode 写入 Redis，30s 有效）
        String signInToken = seedQrToken(attendee.getId());

        // 1. 非签到员操作：拒绝
        BusinessException notManagerEx = assertThrows(BusinessException.class,
                () -> activityEnrollmentService.signIn(signActivity.getId(), attendee.getId(), signInToken));
        assertTrue(notManagerEx.getMessage().contains("签到员"));
        assertNotNull(stringRedisTemplate.opsForValue().get(RedisKey.qrCodeTokenKey(attendee.getId())),
                "签到失败时 token 应保留");

        // 2. 签到员扫码签到：成功 + token 作废
        loginAsPortalUser(signer);
        activityEnrollmentService.signIn(signActivity.getId(), attendee.getId(), signInToken);
        assertTrue(enrollmentRow(signActivity.getId(), attendee.getId()).getSignInStatus());
        assertNull(stringRedisTemplate.opsForValue().get(RedisKey.qrCodeTokenKey(attendee.getId())),
                "签到成功后 token 应作废");

        // 3. 已签到不可取消报名
        loginAsPortalUser(attendee);
        BusinessException cancelEx = assertThrows(BusinessException.class,
                () -> activityEnrollmentService.cancelEnrollment(signActivity.getId()));
        assertTrue(cancelEx.getMessage().contains("已签到"));

        // 4. 签退：成功 + token 作废
        String signOutToken = seedQrToken(attendee.getId());
        loginAsPortalUser(signer);
        activityEnrollmentService.signOut(signActivity.getId(), attendee.getId(), signOutToken);
        assertTrue(enrollmentRow(signActivity.getId(), attendee.getId()).getSignOutStatus());
        assertNull(stringRedisTemplate.opsForValue().get(RedisKey.qrCodeTokenKey(attendee.getId())),
                "签退成功后 token 应作废");
    }

    @Test
    @DisplayName("签到窗口未开始：拒绝")
    void signIn_beforeWindow_blocked() {
        ActivityEntity signActivity = buildSignActivity();
        // 覆盖时间表：签到窗口在未来
        LocalDateTime now = LocalDateTime.now();
        ActivityScheduleEntity schedule = activityScheduleManager.getById(signActivity.getId());
        schedule.setSigninStartTime(now.plusHours(1));
        schedule.setSigninEndTime(now.plusHours(2));
        activityScheduleManager.updateById(schedule);

        PortalUserEntity signer = createPortalUser("signer-" + System.nanoTime());
        PortalUserEntity attendee = createPortalUser("attendee-" + System.nanoTime());
        addSigninManager(signActivity.getId(), signer);

        loginAsPortalUser(attendee);
        activityEnrollmentService.enroll(signActivity.getId());
        String token = seedQrToken(attendee.getId());

        loginAsPortalUser(signer);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityEnrollmentService.signIn(signActivity.getId(), attendee.getId(), token));
        assertTrue(ex.getMessage().contains("尚未开始"));
        assertFalse(enrollmentRow(signActivity.getId(), attendee.getId()).getSignInStatus());
    }

    @Test
    @DisplayName("未报名用户被扫码签到：拒绝")
    void signIn_notEnrolled_blocked() {
        ActivityEntity signActivity = buildSignActivity();
        PortalUserEntity signer = createPortalUser("signer-" + System.nanoTime());
        PortalUserEntity stranger = createPortalUser("stranger-" + System.nanoTime());
        addSigninManager(signActivity.getId(), signer);

        String token = seedQrToken(stranger.getId());
        loginAsPortalUser(signer);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityEnrollmentService.signIn(signActivity.getId(), stranger.getId(), token));
        assertTrue(ex.getMessage().contains("已报名"));
    }

    // ==================== 私有辅助 ====================

    /**
     * 构建一个开启签退、签到/签退窗口覆盖当前时间的活动
     */
    private ActivityEntity buildSignActivity() {
        ActivityEntity signActivity = saveActivity(newActivity("签到活动-" + System.nanoTime(), ActivityStatus.ENROLLING));
        signActivity.setNeedSignOut(true);
        activityManager.updateById(signActivity);

        LocalDateTime now = LocalDateTime.now();
        saveScheduleWithSignWindows(signActivity.getId(),
                now.minusHours(2), now.plusHours(3),
                now.minusHours(1), now.plusHours(4),
                now.minusHours(1), now.plusHours(1),
                now.minusHours(1), now.plusHours(1));
        return signActivity;
    }

    /**
     * 模拟用户二维码 token 写入 Redis（与 ActivityEnrollmentService.signInQRCode 同 key 格式）
     */
    private String seedQrToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        // 业务 signIn 通过 RedisTemplate<String,String>（纯字符串序列化）读取 token，必须使用相同序列化的模板写入
        stringRedisTemplate.opsForValue().set(RedisKey.qrCodeTokenKey(userId), token, 30, TimeUnit.SECONDS);
        return token;
    }
}
