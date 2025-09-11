package net.lab1024.sa.admin.module.business.funcampus.job;

import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.manager.ActivityScheduleManager;
import net.lab1024.sa.base.module.support.job.core.SmartJob;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 活动状态扫描任务（大任务）
 * 低频执行，用于扫描当天有关键时间点的活动，并将其ID存入Redis
 *
 * @Author your-name
 * @Date 2025-09-07
 * @Copyright your-copyright
 */
@Service
public class ActivityStatusScanJob implements SmartJob {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ActivityStatusScanJob.class);

    // Redis中存储当天关键活动ID的键
    private static final String TODAY_CRITICAL_ACTIVITIES_KEY = "funcampus:today_critical_activities";

    @Resource
    private ActivityScheduleManager activityScheduleManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 执行活动状态扫描任务
     * 扫描今天的关键活动，并将结果存入Redis
     *
     * @param param 可选参数
     * @return 执行结果描述
     */
    @Override
    public String run(String param) {
        log.info("开始执行活动状态扫描任务");

        // 计算今天的时间范围
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(today, LocalTime.MAX);

        // 查询今天有关键时间点的活动（报名开始/结束、活动开始/结束）
        log.debug("开始查询今天的关键活动");
        List<ActivityScheduleEntity> todayActivities = activityScheduleManager.getActivitiesWithKeyTime(startOfDay, endOfDay);
        log.debug("查询到{}个今天的关键活动", todayActivities.size());

        // 提取今天关键活动的ID列表并存入Redis
        Set<Long> todayActivityIds = new HashSet<>();
        if (!todayActivities.isEmpty()) {
            todayActivityIds = todayActivities.stream()
                    .map(ActivityScheduleEntity::getActivityId)
                    .collect(Collectors.toSet());
        }

        // 更新Redis中的当天关键活动ID列表
        redisTemplate.opsForValue().set(TODAY_CRITICAL_ACTIVITIES_KEY, List.copyOf(todayActivityIds));
        log.info("已更新Redis中当天关键活动ID列表，共{}个活动", todayActivityIds.size());

        String result = String.format("活动状态扫描完成，今天关键活动%d个", todayActivityIds.size());
        log.info(result);
        return result;
    }
}