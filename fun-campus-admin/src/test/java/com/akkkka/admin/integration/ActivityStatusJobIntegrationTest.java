package com.akkkka.admin.integration;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.job.ActivityStatusUpdateJob;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityStatusCacheManager;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * 活动状态推进 Job 集成测试
 * <p>
 * 覆盖链路：Redis 当天关键活动名单 → ActivityStatusUpdateJob.run() → 状态计算（ActivityStatus.calculate）
 * → 前进（CAS 更新 + 站内信）/ 倒退（清空报名数据）/ 消费确认（移出名单）；
 * 并覆盖：CANCELLED 业务状态不参与推进、名单中不存在/已删除活动跳过、名单缺失时 rebuild 自愈。
 * <p>
 * 名单构造：直接向 Redis 播种"仅含测试活动"的名单（含生成日期，保证名单新鲜不触发全量重建），
 * 避免 rebuild 把开发库真实活动卷入测试处理；测试结束统一清理名单 key，调度侧下一轮自动重建。
 * <p>
 * author:akkkka114514
 * create at 2026-10-01
 */
@DisplayName("活动状态推进 Job 集成测试")
class ActivityStatusJobIntegrationTest extends BaseIntegrationTest {

    @Resource
    private ActivityStatusUpdateJob activityStatusUpdateJob;

    @Resource
    private ActivityStatusCacheManager activityStatusCacheManager;

    @Test
    @DisplayName("状态前进：等待报名→报名中，当天仍有未来关键时间点则保留在名单")
    void job_advanceToEnrolling_keepsInList() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
        // 报名结束时间需落在"今天内且未来"，临近零点时无法构造，跳过
        assumeTrue(now.plusMinutes(5).isBefore(todayEnd), "临近零点，跳过时间敏感用例");

        ActivityEntity activity = saveActivity(newActivity("Job前进-" + System.nanoTime(), ActivityStatus.WAIT_ENROLL));
        // 报名已开始、报名结束/活动时间在今天的未来
        saveSchedule(activity.getId(),
                now.minusMinutes(1), now.plusMinutes(2), now.plusMinutes(3), now.plusMinutes(4));
        seedTodayCriticalActivities(activity.getId());

        String result = activityStatusUpdateJob.run("");

        assertTrue(result.contains("前进1个"), "实际返回：" + result);
        assertTrue(result.contains("移出名单0个"), "仍有未来关键时间点不应移出名单，实际返回：" + result);
        assertEquals(ActivityStatus.ENROLLING, statusOf(activity.getId()));
        assertTrue(inTodayCriticalActivities(activity.getId()), "当天仍有未来关键时间点，活动应保留在名单");
    }

    @Test
    @DisplayName("状态前进到终点：进行中→已结束，全部关键时间点已过则移出名单")
    void job_advanceToFinished_removesFromList() {
        LocalDateTime now = LocalDateTime.now();
        ActivityEntity activity = saveActivity(newActivity("Job结束-" + System.nanoTime(), ActivityStatus.ONGOING));
        // 所有关键时间点均已过去
        saveSchedule(activity.getId(),
                now.minusHours(4), now.minusHours(3), now.minusHours(2), now.minusMinutes(30));
        seedTodayCriticalActivities(activity.getId());

        String result = activityStatusUpdateJob.run("");

        assertTrue(result.contains("前进1个"), "实际返回：" + result);
        assertTrue(result.contains("移出名单1个"), "当天已无未来关键时间点应移出名单，实际返回：" + result);
        assertEquals(ActivityStatus.FINISHED, statusOf(activity.getId()));
        assertFalse(inTodayCriticalActivities(activity.getId()), "活动应已移出名单");
    }

    @Test
    @DisplayName("状态倒退：时间表后调（报名未开始），状态回退并清空报名数据")
    void job_retreat_whenSchedulePostponed() {
        LocalDateTime now = LocalDateTime.now();
        ActivityEntity activity = saveActivity(newActivity("Job倒退-" + System.nanoTime(), ActivityStatus.ENROLLING));
        // 时间表后调：报名开始时间在未来 → 期望状态 WAIT_ENROLL < 当前 ENROLLING
        saveSchedule(activity.getId(),
                now.plusHours(1), now.plusHours(2), now.plusHours(3), now.plusHours(4));

        // 造一条报名记录并占用名额
        PortalUserEntity user = createPortalUser("job-enrollee-" + System.nanoTime());
        ActivityEnrollmentEntity enrollment = new ActivityEnrollmentEntity();
        enrollment.setActivityId(activity.getId());
        enrollment.setUserId(user.getId());
        enrollment.setSignInStatus(false);
        enrollment.setSignOutStatus(false);
        enrollment.setDeletedFlag(false);
        activityEnrollmentManager.save(enrollment);
        activityEnrollNumDao.increaseEnrollNum(activity.getId());
        assertEquals(1, enrollNumOf(activity.getId()));

        seedTodayCriticalActivities(activity.getId());
        String result = activityStatusUpdateJob.run("");

        assertTrue(result.contains("倒退1个"), "实际返回：" + result);
        assertEquals(ActivityStatus.WAIT_ENROLL, statusOf(activity.getId()));
        assertTrue(enrollmentRow(activity.getId(), user.getId()).getDeletedFlag(), "倒退应清空报名数据");
        assertEquals(0, enrollNumOf(activity.getId()), "倒退应清零报名计数");
    }

    @Test
    @DisplayName("已取消活动：不参与时间推进")
    void job_cancelledActivity_skipped() {
        LocalDateTime now = LocalDateTime.now();
        ActivityEntity activity = saveActivity(newActivity("Job已取消-" + System.nanoTime(), ActivityStatus.CANCELLED));
        saveSchedule(activity.getId(),
                now.minusMinutes(1), now.plusMinutes(2), now.plusMinutes(3), now.plusMinutes(4));
        seedTodayCriticalActivities(activity.getId());

        String result = activityStatusUpdateJob.run("");

        assertTrue(result.contains("没有需要推进状态的活动"), "实际返回：" + result);
        assertEquals(ActivityStatus.CANCELLED, statusOf(activity.getId()));
    }

    @Test
    @DisplayName("名单中的活动不存在：跳过且不误报")
    void job_activityNotFound_skipped() {
        // 名单只含一个不存在的活动 id
        seedTodayCriticalActivities(Long.MAX_VALUE);

        String result = activityStatusUpdateJob.run("");

        assertTrue(result.contains("没有需要推进状态的活动"), "实际返回：" + result);
    }

    @Test
    @DisplayName("名单缺失自愈：rebuild 从数据库重建名单")
    void rebuild_restoresListFromDatabase() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        ActivityEntity activity = saveActivity(newActivity("Job重建-" + System.nanoTime(), ActivityStatus.WAIT_ENROLL));
        // 保证报名开始时间落在"今天"区间内（rebuild 的查询窗口为今天，同事务可见测试数据）
        LocalDateTime enrollStart = now.minusMinutes(1);
        if (enrollStart.isBefore(todayStart)) {
            enrollStart = now.plusSeconds(30);
        }
        saveSchedule(activity.getId(),
                enrollStart, enrollStart.plusMinutes(1), enrollStart.plusMinutes(2), enrollStart.plusMinutes(3));

        // 模拟名单缺失
        clearTodayCriticalActivitiesCache();
        List<Long> rebuiltIds = activityStatusCacheManager.rebuild();

        assertTrue(rebuiltIds.contains(activity.getId()), "rebuild 应包含今天有关键时间点的测试活动");
        assertTrue(inTodayCriticalActivities(activity.getId()), "重建后名单应包含测试活动");
    }
}
