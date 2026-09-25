package com.akkkka.admin.module.business.funcampus.activityWithSchedule.job;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityStatusCacheManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 活动状态更新任务 单元测试
 * <p>
 * 覆盖：当天关键活动名单消费、状态前进步进（0~4 时间边界）、时间表后调时的状态倒退（回退 + 清报名）、
 * 并发条件更新失败跳过、消费确认（移出名单）、单条异常隔离。
 * <p>
 * 说明：任务内部以 {@link LocalDateTime#now()} 判定当前时刻，因此本类所有时间表均以
 * 运行时真实当前时间为锚点构造相对窗口（偏移量 >= 1 小时），保证任意时刻执行结果确定。
 *
 * @Author akkkka114514
 * @Date 2026-09-04
 */
@ExtendWith(MockitoExtension.class)
public class ActivityStatusUpdateJobTest {

    @Mock
    private ActivityStatusCacheManager activityStatusCacheManager;
    @Mock
    private ActivityManager activityManager;
    @Mock
    private ActivityScheduleManager activityScheduleManager;
    @Mock
    private ActivityWithScheduleService activityWithScheduleService;

    @InjectMocks
    private ActivityStatusUpdateJob job;

    /**
     * 以真实当前时间为锚点（任务内部同样取 LocalDateTime.now() 判断）
     */
    private static LocalDateTime now() {
        return LocalDateTime.now();
    }

    private ActivityEntity activity(Long id, ActivityStatus status) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(id);
        activity.setStatus(status);
        return activity;
    }

    /**
     * 四个关键时间全部早于当前时刻（活动结束时间已过），期望状态为 4 已结束
     */
    private ActivityScheduleEntity passedSchedule(Long activityId, LocalDateTime base) {
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(activityId);
        schedule.setEnrollStartTime(base.minusHours(2));
        schedule.setEnrollEndTime(base.minusHours(1));
        schedule.setActivityStartTime(base.minusHours(1));
        schedule.setActivityEndTime(base);
        return schedule;
    }

    /**
     * 当前时刻处于报名窗口内，期望状态为 1 报名中
     */
    private ActivityScheduleEntity enrollingSchedule(Long activityId, LocalDateTime base) {
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(activityId);
        schedule.setEnrollStartTime(base.minusHours(1));
        schedule.setEnrollEndTime(base.plusHours(1));
        schedule.setActivityStartTime(base.plusHours(2));
        schedule.setActivityEndTime(base.plusHours(3));
        return schedule;
    }

    /**
     * 四个关键时间全部在未来，期望状态为 0 等待报名（用于时间表后调的倒退场景）
     */
    private ActivityScheduleEntity futureSchedule(Long activityId, LocalDateTime base) {
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(activityId);
        schedule.setEnrollStartTime(base.plusHours(1));
        schedule.setEnrollEndTime(base.plusHours(2));
        schedule.setActivityStartTime(base.plusHours(3));
        schedule.setActivityEndTime(base.plusHours(4));
        return schedule;
    }

    private void mockTodayList(List<Long> ids) {
        when(activityStatusCacheManager.getTodayCriticalActivityIds()).thenReturn(ids);
    }

    private void mockUpdatable(List<ActivityEntity> activities) {
        when(activityManager.list(ArgumentMatchers.<Wrapper<ActivityEntity>>any())).thenReturn(activities);
    }

    private void mockSchedules(ActivityScheduleEntity... schedules) {
        when(activityScheduleManager.getByActivityIds(any())).thenReturn(List.of(schedules));
    }

    @Test
    void run_whenTodayListEmpty_returnEarly() {
        mockTodayList(List.of());

        String result = job.run(null);

        assertTrue(result.contains("今天没有关键时间点的活动"));
        verify(activityManager, never()).list(ArgumentMatchers.<Wrapper<ActivityEntity>>any());
    }

    @Test
    void run_whenNoUpdatableActivity_returnEarly() {
        mockTodayList(List.of(1L));
        mockUpdatable(List.of());

        String result = job.run(null);

        assertTrue(result.contains("当天关键活动中没有需要推进状态的活动"));
        verify(activityScheduleManager, never()).getByActivityIds(any());
    }

    @Test
    void run_whenAllTimePassed_advanceToFinishedAndAck() {
        mockTodayList(List.of(1L));
        mockUpdatable(List.of(activity(1L, ActivityStatus.ONGOING)));
        mockSchedules(passedSchedule(1L, now()));
        when(activityManager.updateStatusIfMatch(1L, ActivityStatus.FINISHED, ActivityStatus.ONGOING)).thenReturn(true);
        when(activityStatusCacheManager.hasFutureKeyTimeToday(any())).thenReturn(false);

        String result = job.run(null);

        verify(activityManager).updateStatusIfMatch(1L, ActivityStatus.FINISHED, ActivityStatus.ONGOING);
        verify(activityStatusCacheManager).removeFromTodayCache(1L);
        assertTrue(result.contains("前进1个"));
        assertTrue(result.contains("移出名单1个"));
    }

    @Test
    void run_whenStatusAlreadyExpected_noUpdateButAck() {
        mockTodayList(List.of(1L));
        mockUpdatable(List.of(activity(1L, ActivityStatus.ENROLLING)));
        mockSchedules(enrollingSchedule(1L, now()));
        when(activityStatusCacheManager.hasFutureKeyTimeToday(any())).thenReturn(false);

        String result = job.run(null);

        verify(activityManager, never()).updateStatusIfMatch(anyLong(), any(), any());
        verify(activityWithScheduleService, never()).retreatActivityStatusTransaction(anyLong(), any(), any());
        verify(activityStatusCacheManager).removeFromTodayCache(1L);
        assertTrue(result.contains("前进0个"));
        assertTrue(result.contains("倒退0个"));
        assertTrue(result.contains("移出名单1个"));
    }

    @Test
    void run_whenScheduleMovedLater_retreatAndClearEnrollment() {
        // 时间表被整体后调（四个关键时间都在未来），应回退状态并清空报名数据
        mockTodayList(List.of(1L));
        mockUpdatable(List.of(activity(1L, ActivityStatus.ONGOING)));
        mockSchedules(futureSchedule(1L, now()));
        when(activityWithScheduleService.retreatActivityStatusTransaction(
                1L, ActivityStatus.ONGOING, ActivityStatus.WAIT_ENROLL)).thenReturn(true);
        when(activityStatusCacheManager.hasFutureKeyTimeToday(any())).thenReturn(true);

        String result = job.run(null);

        verify(activityWithScheduleService).retreatActivityStatusTransaction(
                1L, ActivityStatus.ONGOING, ActivityStatus.WAIT_ENROLL);
        verify(activityManager, never()).updateStatusIfMatch(anyLong(), any(), any());
        verify(activityStatusCacheManager, never()).removeFromTodayCache(anyLong());
        assertTrue(result.contains("倒退1个"));
        assertTrue(result.contains("移出名单0个"));
    }

    @Test
    void run_whenScheduleMissing_skip() {
        mockTodayList(List.of(1L));
        mockUpdatable(List.of(activity(1L, ActivityStatus.ONGOING)));
        // 时间表查询不到该活动
        mockSchedules();

        String result = job.run(null);

        verify(activityManager, never()).updateStatusIfMatch(anyLong(), any(), any());
        verify(activityStatusCacheManager, never()).removeFromTodayCache(anyLong());
        assertTrue(result.contains("跳过1个"));
        assertTrue(result.contains("移出名单0个"));
    }

    @Test
    void run_whenScheduleTimeIncomplete_skip() {
        mockTodayList(List.of(1L));
        mockUpdatable(List.of(activity(1L, ActivityStatus.ENROLLING)));
        ActivityScheduleEntity schedule = passedSchedule(1L, now());
        schedule.setEnrollStartTime(null); // 关键时间缺失，无法计算期望状态
        mockSchedules(schedule);

        String result = job.run(null);

        verify(activityManager, never()).updateStatusIfMatch(anyLong(), any(), any());
        assertTrue(result.contains("跳过1个"));
    }

    @Test
    void run_whenRetreatFails_stayInList() {
        mockTodayList(List.of(1L));
        mockUpdatable(List.of(activity(1L, ActivityStatus.ONGOING)));
        mockSchedules(futureSchedule(1L, now()));
        when(activityWithScheduleService.retreatActivityStatusTransaction(
                1L, ActivityStatus.ONGOING, ActivityStatus.WAIT_ENROLL)).thenReturn(false);

        String result = job.run(null);

        // 倒退条件不满足（状态被并发修改）：跳过且保留在名单，下轮重试
        verify(activityStatusCacheManager, never()).removeFromTodayCache(anyLong());
        assertTrue(result.contains("跳过1个"));
        assertTrue(result.contains("移出名单0个"));
    }

    @Test
    void run_whenAdvanceLosesRace_skip() {
        mockTodayList(List.of(1L));
        mockUpdatable(List.of(activity(1L, ActivityStatus.ONGOING)));
        mockSchedules(passedSchedule(1L, now()));
        when(activityManager.updateStatusIfMatch(1L, ActivityStatus.FINISHED, ActivityStatus.ONGOING)).thenReturn(false);

        String result = job.run(null);

        // 条件更新失败（状态被并发修改）：跳过且保留在名单，下轮重试
        verify(activityStatusCacheManager, never()).removeFromTodayCache(anyLong());
        assertTrue(result.contains("跳过1个"));
        assertTrue(result.contains("移出名单0个"));
    }

    @Test
    void run_whenFutureKeyTimeToday_keepInList() {
        mockTodayList(List.of(1L));
        mockUpdatable(List.of(activity(1L, ActivityStatus.ONGOING)));
        mockSchedules(passedSchedule(1L, now()));
        when(activityManager.updateStatusIfMatch(1L, ActivityStatus.FINISHED, ActivityStatus.ONGOING)).thenReturn(true);
        // 当天仍有未来的关键时间点：处理成功也不移出名单
        when(activityStatusCacheManager.hasFutureKeyTimeToday(any())).thenReturn(true);

        String result = job.run(null);

        verify(activityStatusCacheManager, never()).removeFromTodayCache(anyLong());
        assertTrue(result.contains("前进1个"));
        assertTrue(result.contains("移出名单0个"));
    }

    @Test
    void run_whenOneThrows_otherActivitiesStillProcessed() {
        LocalDateTime base = now();
        mockTodayList(List.of(1L, 2L));
        mockUpdatable(List.of(activity(1L, ActivityStatus.ONGOING),
                activity(2L, ActivityStatus.ONGOING)));
        mockSchedules(passedSchedule(1L, base), passedSchedule(2L, base));
        when(activityManager.updateStatusIfMatch(1L, ActivityStatus.FINISHED, ActivityStatus.ONGOING))
                .thenThrow(new RuntimeException("db down"));
        when(activityManager.updateStatusIfMatch(2L, ActivityStatus.FINISHED, ActivityStatus.ONGOING)).thenReturn(true);
        when(activityStatusCacheManager.hasFutureKeyTimeToday(any())).thenReturn(false);

        String result = job.run(null);

        // 活动1更新抛异常计入跳过，活动2仍被正常推进
        verify(activityManager).updateStatusIfMatch(2L, ActivityStatus.FINISHED, ActivityStatus.ONGOING);
        verify(activityStatusCacheManager, never()).removeFromTodayCache(1L);
        verify(activityStatusCacheManager).removeFromTodayCache(2L);
        assertTrue(result.contains("跳过1个"));
        assertTrue(result.contains("前进1个"));
    }

    @Test
    void run_whenNowAtBoundaryTime_forwardToNextStage() {
        // 构造 4 个活动，其下一状态切换点恰好都在当前时刻，应各自推进一级
        LocalDateTime base = now();
        ActivityScheduleEntity enrollStartAtNow = passedSchedule(1L, base);
        enrollStartAtNow.setEnrollStartTime(base); // 此刻起报名 -> 状态 1
        enrollStartAtNow.setEnrollEndTime(base.plusHours(2));
        enrollStartAtNow.setActivityStartTime(base.plusHours(3));
        enrollStartAtNow.setActivityEndTime(base.plusHours(4));

        ActivityScheduleEntity enrollEndAtNow = passedSchedule(2L, base);
        enrollEndAtNow.setEnrollStartTime(base.minusHours(2));
        enrollEndAtNow.setEnrollEndTime(base); // 此刻报名截止 -> 状态 2
        enrollEndAtNow.setActivityStartTime(base.plusHours(2));
        enrollEndAtNow.setActivityEndTime(base.plusHours(3));

        ActivityScheduleEntity activityStartAtNow = passedSchedule(3L, base);
        activityStartAtNow.setEnrollStartTime(base.minusHours(4));
        activityStartAtNow.setEnrollEndTime(base.minusHours(3));
        activityStartAtNow.setActivityStartTime(base); // 此刻活动开始 -> 状态 3
        activityStartAtNow.setActivityEndTime(base.plusHours(2));

        ActivityScheduleEntity activityEndAtNow = passedSchedule(4L, base);
        activityEndAtNow.setEnrollStartTime(base.minusHours(5));
        activityEndAtNow.setEnrollEndTime(base.minusHours(4));
        activityEndAtNow.setActivityStartTime(base.minusHours(3));
        activityEndAtNow.setActivityEndTime(base); // 此刻活动结束 -> 状态 4

        mockTodayList(List.of(1L, 2L, 3L, 4L));
        mockUpdatable(List.of(
                activity(1L, ActivityStatus.WAIT_ENROLL),
                activity(2L, ActivityStatus.ENROLLING),
                activity(3L, ActivityStatus.ENROLL_ENDED),
                activity(4L, ActivityStatus.ONGOING)));
        mockSchedules(enrollStartAtNow, enrollEndAtNow, activityStartAtNow, activityEndAtNow);
        when(activityManager.updateStatusIfMatch(anyLong(), any(), any())).thenReturn(true);
        when(activityStatusCacheManager.hasFutureKeyTimeToday(any())).thenReturn(true);

        job.run(null);

        verify(activityManager).updateStatusIfMatch(1L, ActivityStatus.ENROLLING, ActivityStatus.WAIT_ENROLL);
        verify(activityManager).updateStatusIfMatch(2L, ActivityStatus.ENROLL_ENDED, ActivityStatus.ENROLLING);
        verify(activityManager).updateStatusIfMatch(3L, ActivityStatus.ONGOING, ActivityStatus.ENROLL_ENDED);
        verify(activityManager).updateStatusIfMatch(4L, ActivityStatus.FINISHED, ActivityStatus.ONGOING);
    }
}
