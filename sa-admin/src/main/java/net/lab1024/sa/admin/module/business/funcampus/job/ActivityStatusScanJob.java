package net.lab1024.sa.admin.module.business.funcampus.job;

import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.manager.ActivityScheduleManager;
import net.lab1024.sa.base.module.support.job.core.SmartJob;
import net.lab1024.sa.base.module.support.job.repository.SmartJobDao;
import net.lab1024.sa.base.module.support.job.repository.domain.SmartJobEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 活动状态扫描任务（大任务）
 * 用于检查当天是否有活动需要高频状态更新，如果有则启动高频任务
 *
 * @Author your-name
 * @Date 2025-09-07
 * @Copyright your-copyright
 */
@Service
public class ActivityStatusScanJob implements SmartJob {

    // Redis中存储当天关键活动ID的键
    private static final String TODAY_CRITICAL_ACTIVITIES_KEY = "funcampus:today_critical_activities";
    // Redis中存储高频任务是否启用的键
    private static final String HIGH_FREQUENCY_JOB_ENABLED_KEY = "funcampus:high_frequency_job_enabled";
    // Redis缓存过期时间（小时）
    private static final int CACHE_EXPIRE_HOURS = 24;

    @Resource
    private ActivityScheduleManager activityScheduleManager;
    
    @Resource
    private SmartJobDao smartJobDao;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 扫描任务，检查当天是否有需要高频更新的活动
     * 如果有，则启用高频更新任务；否则禁用高频更新任务
     *
     * @param param 可选参数
     * @return 执行结果描述
     */
    @Override
    public String run(String param) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = now.withHour(23).withMinute(59).withSecond(59);
        
        // 查询今天有关键时间点的活动（报名开始/结束、活动开始/结束）
        List<ActivityScheduleEntity> todayActivities = activityScheduleManager
                .getActivitiesWithKeyTime(startOfDay, endOfDay);
        
        // 将今天的关键活动ID列表存储到Redis中
        List<Long> activityIds = todayActivities.stream()
                .map(ActivityScheduleEntity::getActivityId)
                .toList();
        
        // 存储到Redis并设置过期时间
        redisTemplate.opsForValue().set(TODAY_CRITICAL_ACTIVITIES_KEY, activityIds, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 查找高频任务配置
        SmartJobEntity highFrequencyJob = findHighFrequencyJob();
        
        if (highFrequencyJob != null) {
            // 如果今天有关键活动，启用高频任务
            if (!todayActivities.isEmpty()) {
                if (!highFrequencyJob.getEnabledFlag()) {
                    enableJob(highFrequencyJob);
                    redisTemplate.opsForValue().set(HIGH_FREQUENCY_JOB_ENABLED_KEY, true, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
                    return String.format("检测到%d个今天的活动，已启用高频更新任务", todayActivities.size());
                }
                redisTemplate.opsForValue().set(HIGH_FREQUENCY_JOB_ENABLED_KEY, true, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
                return String.format("检测到%d个今天的活动，高频更新任务已处于启用状态", todayActivities.size());
            } 
            // 如果今天没有关键活动，禁用高频任务
            else {
                if (highFrequencyJob.getEnabledFlag()) {
                    disableJob(highFrequencyJob);
                    redisTemplate.opsForValue().set(HIGH_FREQUENCY_JOB_ENABLED_KEY, false, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
                    return "今天没有关键活动，已禁用高频更新任务";
                }
                redisTemplate.opsForValue().set(HIGH_FREQUENCY_JOB_ENABLED_KEY, false, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
                return "今天没有关键活动，高频更新任务已处于禁用状态";
            }
        }
        
        return "未找到高频更新任务配置";
    }
    
    /**
     * 查找高频更新任务配置
     * 
     * @return 高频任务配置
     */
    private SmartJobEntity findHighFrequencyJob() {
        // 根据任务类名查找高频任务
        return smartJobDao.selectByJobClass(ActivityStatusUpdateJob.class.getName());
    }
    
    /**
     * 启用任务
     * 
     * @param job 任务配置
     */
    private void enableJob(SmartJobEntity job) {
        job.setEnabledFlag(true);
        smartJobDao.updateById(job);
    }
    
    /**
     * 禁用任务
     * 
     * @param job 任务配置
     */
    private void disableJob(SmartJobEntity job) {
        job.setEnabledFlag(false);
        smartJobDao.updateById(job);
    }
}