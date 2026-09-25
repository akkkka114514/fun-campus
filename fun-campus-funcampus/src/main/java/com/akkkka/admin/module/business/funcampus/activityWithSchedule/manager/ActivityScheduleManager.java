package com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityScheduleDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动时间表  Manager
 *
 * @Author akkkka114514
 * @Date 2025-09-06 15:54:07
 * @Copyright akkkka114514
 */
@Service
public class ActivityScheduleManager extends ServiceImpl<ActivityScheduleDao, ActivityScheduleEntity> {

    /**
     * 根据活动ID列表查询时间表
     *
     * @param activityIds 活动ID列表
     * @return 时间表列表
     */
    public List<ActivityScheduleEntity> getByActivityIds(List<Long> activityIds) {
        if (activityIds == null || activityIds.isEmpty()) {
            return List.of();
        }
        
        LambdaQueryWrapper<ActivityScheduleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ActivityScheduleEntity::getActivityId, activityIds);
        queryWrapper.eq(ActivityScheduleEntity::getDeletedFlag, false);
        return this.list(queryWrapper);
    }

    /**
     * 根据活动ID查询时间表
     *
     * @param activityId 活动ID
     * @return 时间表
     */
    public ActivityScheduleEntity getByActivityId(Long activityId) {
        if (activityId == null) {
            return null;
        }
        
        LambdaQueryWrapper<ActivityScheduleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityScheduleEntity::getActivityId, activityId);
        return this.getOne(queryWrapper);
    }

    /**
     * 查询指定时间段内有任意关键时间点到达的活动（报名开始/结束、活动开始/结束）
     * 使用半开区间 [startTime, endTime)，避免 23:59:59 这类边界写法的遗漏
     * 签到/签退时间点不参与状态机计算，故不参与筛选
     *
     * @param startTime 开始时间（含）
     * @param endTime 结束时间（不含）
     * @return 时间表列表
     */
    public List<ActivityScheduleEntity> getActivitiesWithKeyTime(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<ActivityScheduleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityScheduleEntity::getDeletedFlag, false);
        queryWrapper.and(w -> w
                .ge(ActivityScheduleEntity::getEnrollStartTime, startTime)
                .lt(ActivityScheduleEntity::getEnrollStartTime, endTime)
                .or()
                .ge(ActivityScheduleEntity::getEnrollEndTime, startTime)
                .lt(ActivityScheduleEntity::getEnrollEndTime, endTime)
                .or()
                .ge(ActivityScheduleEntity::getActivityStartTime, startTime)
                .lt(ActivityScheduleEntity::getActivityStartTime, endTime)
                .or()
                .ge(ActivityScheduleEntity::getActivityEndTime, startTime)
                .lt(ActivityScheduleEntity::getActivityEndTime, endTime));
        return this.list(queryWrapper);
    }
}