package com.akkkka.admin.module.business.funcampus.activityWithSchedule.service;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 活动时间表 校验器单元测试
 * <p>
 * 覆盖：活动时间先后顺序校验（含签退开关分支）、更新表单的旧值合并逻辑
 *
 * @Author akkkka114514
 * @Date 2026-09-03
 */
@ExtendWith(MockitoExtension.class)
public class ActivityScheduleValidatorTest {

    @Mock
    private ActivityScheduleManager scheduleManager;
    @Mock
    private ActivityManager activityManager;

    @InjectMocks
    private ActivityScheduleValidator validator;

    /**
     * 构造一张完全合法的时间表：报名<活动<签到<签退，各相隔 1 小时
     */
    private ActivityScheduleEntity validSchedule() {
        LocalDateTime base = LocalDateTime.of(2026, 9, 10, 8, 0);
        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setEnrollStartTime(base);
        schedule.setEnrollEndTime(base.plusHours(1));
        schedule.setActivityStartTime(base.plusHours(2));
        schedule.setActivityEndTime(base.plusHours(3));
        schedule.setSigninStartTime(base.plusHours(4));
        schedule.setSigninEndTime(base.plusHours(5));
        schedule.setSignoutStartTime(base.plusHours(6));
        schedule.setSignoutEndTime(base.plusHours(7));
        return schedule;
    }

    @Test
    void validateActivityScheduleOrder_whenNeedSignOutAndAllOrdered_pass() {
        assertDoesNotThrow(() -> validator.validateActivityScheduleOrder(validSchedule(), true));
    }

    @Test
    void validateActivityScheduleOrder_whenNoNeedSignOutButTimeOrdered_pass() {
        assertDoesNotThrow(() -> validator.validateActivityScheduleOrder(validSchedule(), false));
    }

    @Test
    void validateActivityScheduleOrder_whenTimeOutOfOrder_throw() {
        ActivityScheduleEntity schedule = validSchedule();
        // 报名结束时间晚于活动开始时间
        schedule.setEnrollEndTime(schedule.getActivityStartTime().plusHours(1));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateActivityScheduleOrder(schedule, false));
        assertTrue(ex.getMessage().contains("活动时间不按顺序"));
    }

    @Test
    void validateActivityScheduleOrder_whenNeedSignOutButSignOutTimeEmpty_throw() {
        ActivityScheduleEntity schedule = validSchedule();
        schedule.setSignoutStartTime(null);
        schedule.setSignoutEndTime(null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateActivityScheduleOrder(schedule, true));
        assertTrue(ex.getMessage().contains("签退时间为空"));
    }

    @Test
    void validateActivityScheduleOrder_whenSignOutTimeOutOfOrder_throw() {
        ActivityScheduleEntity schedule = validSchedule();
        // 签退开始时间早于签到结束时间，即使不需要签退，同样会校验签退顺序
        schedule.setSignoutStartTime(schedule.getSigninEndTime().minusHours(1));
        schedule.setSignoutEndTime(schedule.getSigninEndTime().plusHours(1));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateActivityScheduleOrder(schedule, false));
        assertTrue(ex.getMessage().contains("活动时间不按顺序"));
    }

    @Test
    void validateUpdate_whenScheduleNull_doNothing() {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(1L);
        assertDoesNotThrow(() -> validator.validateUpdate(activity, null));
    }

    @Test
    void validateUpdate_whenFormTimeAllNull_fillFromDbSchedule() {
        ActivityScheduleEntity dbSchedule = validSchedule();
        when(scheduleManager.getById(1L)).thenReturn(dbSchedule);

        ActivityEntity dbActivity = new ActivityEntity();
        dbActivity.setNeedSignOut(false);
        when(activityManager.getById(1L)).thenReturn(dbActivity);

        ActivityEntity activity = new ActivityEntity();
        activity.setId(1L);

        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(1L);
        validator.validateUpdate(activity, schedule);

        assertEquals(dbSchedule.getEnrollStartTime(), schedule.getEnrollStartTime());
        assertEquals(dbSchedule.getEnrollEndTime(), schedule.getEnrollEndTime());
        assertEquals(dbSchedule.getActivityStartTime(), schedule.getActivityStartTime());
        assertEquals(dbSchedule.getActivityEndTime(), schedule.getActivityEndTime());
        assertEquals(dbSchedule.getSigninStartTime(), schedule.getSigninStartTime());
        assertEquals(dbSchedule.getSigninEndTime(), schedule.getSigninEndTime());
    }

    @Test
    void validateUpdate_whenFormTimeNotNull_keepFormTime() {
        when(scheduleManager.getById(1L)).thenReturn(validSchedule());
        ActivityEntity dbActivity = new ActivityEntity();
        dbActivity.setNeedSignOut(false);
        when(activityManager.getById(1L)).thenReturn(dbActivity);

        ActivityEntity activity = new ActivityEntity();
        activity.setId(1L);

        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(1L);
        LocalDateTime newEnrollStart = LocalDateTime.of(2026, 10, 1, 8, 0);
        schedule.setEnrollStartTime(newEnrollStart);
        validator.validateUpdate(activity, schedule);

        assertEquals(newEnrollStart, schedule.getEnrollStartTime());
    }

    @Test
    void validateUpdate_whenFormNeedSignOutButSignOutTimeEmpty_throw() {
        when(scheduleManager.getById(1L)).thenReturn(validSchedule());
        ActivityEntity dbActivity = new ActivityEntity();
        dbActivity.setNeedSignOut(false);
        when(activityManager.getById(1L)).thenReturn(dbActivity);

        ActivityEntity activity = new ActivityEntity();
        activity.setId(1L);
        activity.setNeedSignOut(true);

        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(1L);
        // 表单中签退时间为空
        assertThrows(BusinessException.class, () -> validator.validateUpdate(activity, schedule));
    }

    @Test
    void validateUpdate_whenFormNeedSignOutAndTimeProvided_pass() {
        when(scheduleManager.getById(1L)).thenReturn(validSchedule());
        ActivityEntity dbActivity = new ActivityEntity();
        dbActivity.setNeedSignOut(false);
        when(activityManager.getById(1L)).thenReturn(dbActivity);

        ActivityEntity activity = new ActivityEntity();
        activity.setId(1L);
        activity.setNeedSignOut(true);

        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(1L);
        schedule.setSignoutStartTime(LocalDateTime.of(2026, 9, 10, 15, 0));
        schedule.setSignoutEndTime(LocalDateTime.of(2026, 9, 10, 16, 0));
        assertDoesNotThrow(() -> validator.validateUpdate(activity, schedule));
    }

    @Test
    void validateUpdate_whenDbNeedSignOutAndFormProvidedTime_overwriteByDbTime() {
        // 表单未声明 needSignOut 时 formNeedSignOut=false；若数据库原活动需要签退，
        // 表单填写的签退时间会被数据库中的签退时间覆盖（沿用旧值）
        ActivityScheduleEntity dbSchedule = validSchedule();
        when(scheduleManager.getById(1L)).thenReturn(dbSchedule);
        ActivityEntity dbActivity = new ActivityEntity();
        dbActivity.setNeedSignOut(true);
        when(activityManager.getById(1L)).thenReturn(dbActivity);

        ActivityEntity activity = new ActivityEntity();
        activity.setId(1L);

        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(1L);
        LocalDateTime formSignOutStart = LocalDateTime.of(2030, 1, 1, 0, 0);
        schedule.setSignoutStartTime(formSignOutStart);
        schedule.setSignoutEndTime(formSignOutStart.plusHours(1));

        validator.validateUpdate(activity, schedule);

        assertEquals(dbSchedule.getSignoutStartTime(), schedule.getSignoutStartTime());
        assertEquals(dbSchedule.getSignoutEndTime(), schedule.getSignoutEndTime());
    }

    @Test
    void validateUpdate_whenDbNeedSignOutAndFormTimeEmpty_keepNull() {
        when(scheduleManager.getById(1L)).thenReturn(validSchedule());
        ActivityEntity dbActivity = new ActivityEntity();
        dbActivity.setNeedSignOut(true);
        when(activityManager.getById(1L)).thenReturn(dbActivity);

        ActivityEntity activity = new ActivityEntity();
        activity.setId(1L);

        ActivityScheduleEntity schedule = new ActivityScheduleEntity();
        schedule.setActivityId(1L);
        validator.validateUpdate(activity, schedule);

        assertNull(schedule.getSignoutStartTime());
        assertNull(schedule.getSignoutEndTime());
    }
}
