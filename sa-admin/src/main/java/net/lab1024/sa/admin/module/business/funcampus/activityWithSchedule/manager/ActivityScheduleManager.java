package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao.ActivityScheduleDao;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;

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
     * 查询指定时间范围内有关键时间点的活动
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 活动时间表列表
     */
    public List<ActivityScheduleEntity> getActivitiesWithKeyTime(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<ActivityScheduleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityScheduleEntity::getDeletedFlag, false);
        
        // 查询在指定时间范围内有关键时间点的活动
        queryWrapper
            .and(wrapper -> wrapper.between(ActivityScheduleEntity::getEnrollStartTime, startTime, endTime)
                    .or()
                    .between(ActivityScheduleEntity::getEnrollEndTime, startTime, endTime)
                    .or()
                    .between(ActivityScheduleEntity::getActivityStartTime, startTime, endTime)
                    .or()
                    .between(ActivityScheduleEntity::getActivityEndTime, startTime, endTime));
        
        return this.list(queryWrapper);
    }
}