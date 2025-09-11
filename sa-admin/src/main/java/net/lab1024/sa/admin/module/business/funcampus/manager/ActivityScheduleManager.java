package net.lab1024.sa.admin.module.business.funcampus.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.lab1024.sa.admin.module.business.funcampus.dao.ActivityScheduleDao;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityScheduleEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
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

    @Resource
    private ActivityScheduleDao activityScheduleDao;

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
        
        return activityScheduleDao.selectByActivityIds(activityIds);
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
        return activityScheduleDao.selectActivitiesWithKeyTime(startTime, endTime);
    }
}