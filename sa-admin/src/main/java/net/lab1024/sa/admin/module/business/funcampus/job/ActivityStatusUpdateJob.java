package net.lab1024.sa.admin.module.business.funcampus.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.manager.ActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.manager.ActivityScheduleManager;
import net.lab1024.sa.base.module.support.job.core.SmartJob;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活动状态更新任务（小任务）
 * 高频执行，用于实时更新活动状态
 *
 * @Author your-name
 * @Date 2025-09-07
 * @Copyright your-copyright
 */
@Service
public class ActivityStatusUpdateJob implements SmartJob {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ActivityStatusUpdateJob.class);

    // Redis中存储当天关键活动ID的键
    private static final String TODAY_CRITICAL_ACTIVITIES_KEY = "funcampus:today_critical_activities";
    @Resource
    private ActivityManager activityManager;

    @Resource
    private ActivityScheduleManager activityScheduleManager;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页大小，用于分批处理大量活动
     */
    private static final int PAGE_SIZE = 100;

    /**
     * 执行活动状态更新任务
     * 采用分批处理方式，避免一次性加载大量数据导致内存问题
     *
     * @param param 可选参数
     * @return 执行结果描述
     */
    @Override
    public String run(String param) {
        LocalDateTime now = LocalDateTime.now();
        int totalUpdated = 0;
        int currentPage = 1;
        boolean hasMore = true;

        // 获取Redis中存储的当天关键活动ID列表
        List<Long> todayCriticalActivityIds = getTodayCriticalActivityIds();
        
        // 如果没有关键活动，直接返回
        if (todayCriticalActivityIds == null || todayCriticalActivityIds.isEmpty()) {
            return "今天没有关键活动，无需更新";
        }

        // 分批处理所有需要检查状态的活动
        while (hasMore) {
            // 分页查询需要检查状态的活动
            IPage<ActivityEntity> page = queryActivitiesForStatusCheck(currentPage, PAGE_SIZE);
            
            List<ActivityEntity> activities = page.getRecords();
            if (activities.isEmpty()) {
                break;
            }

            // 过滤出当天的关键活动
            List<ActivityEntity> criticalActivities = activities.stream()
                    .filter(activity -> todayCriticalActivityIds.contains(activity.getId()))
                    .toList();

            // 批量处理当前页的关键活动
            if (!criticalActivities.isEmpty()) {
                int updatedInThisPage = processActivitiesBatch(criticalActivities, now);
                totalUpdated += updatedInThisPage;
            }

            // 检查是否还有更多数据
            hasMore = currentPage < page.getPages();
            currentPage++;
        }

        return String.format("活动状态更新完成，总共更新%d个活动", totalUpdated);
    }

    /**
     * 从Redis获取当天关键活动ID列表
     *
     * @return 当天关键活动ID列表
     */
    @SuppressWarnings("unchecked")
    private List<Long> getTodayCriticalActivityIds() {
        Object obj = redisTemplate.opsForValue().get(TODAY_CRITICAL_ACTIVITIES_KEY);
        if (obj instanceof List) {
            return (List<Long>) obj;
        }
        return List.of();
    }

    /**
     * 分页查询需要检查状态的活动
     * 只查询未删除且未结束的活动，减少不必要的数据加载
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    private IPage<ActivityEntity> queryActivitiesForStatusCheck(int pageNum, int pageSize) {
        Page<ActivityEntity> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<ActivityEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEntity::getDeletedFlag, false);  // 只查询未删除的活动
        queryWrapper.ne(ActivityEntity::getStatus, 4);  // 排除已结束的活动（状态4）
        
        return activityManager.page(page, queryWrapper);
    }

    /**
     * 批量处理活动状态更新
     *
     * @param activities 活动列表
     * @param now 当前时间
     * @return 更新的活动数量
     */
    private int processActivitiesBatch(List<ActivityEntity> activities, LocalDateTime now) {
        int updatedCount = 0;
        
        // 批量获取这些活动的时间表信息
        List<Long> activityIds = activities.stream()
                .map(ActivityEntity::getId)
                .toList();
        
        List<ActivityScheduleEntity> schedules = activityScheduleManager.getByActivityIds(activityIds);
        
        // 将时间表信息转换为Map方便查找
        Map<Long, ActivityScheduleEntity> scheduleMap = schedules.stream()
                .collect(Collectors.toMap(
                        ActivityScheduleEntity::getActivityId, 
                        schedule -> schedule
                ));

        // 逐个检查并更新活动状态
        for (ActivityEntity activity : activities) {
            ActivityScheduleEntity schedule = scheduleMap.get(activity.getId());
            if (schedule == null) {
                continue; // 没有时间表信息，跳过
            }

            // 根据时间表计算活动应该处于的状态
            Integer newStatus = calculateActivityStatus(now, schedule);
            
            // 如果状态有变化，则更新
            if (!newStatus.equals(activity.getStatus())) {
                boolean updated = activityManager.updateStatus(activity.getId(), newStatus);
                if (updated) {
                    updatedCount++;
                }
            }
        }
        
        return updatedCount;
    }

    /**
     * 根据当前时间和活动时间表计算活动应该处于的状态
     *
     * @param now 当前时间
     * @param schedule 活动时间表
     * @return 活动状态
     */
    private Integer calculateActivityStatus(LocalDateTime now, ActivityScheduleEntity schedule) {
        // 活动状态定义：
        // 0 - 等待报名
        // 1 - 报名中
        // 2 - 报名结束
        // 3 - 活动进行中
        // 4 - 活动结束

        if (now.isBefore(schedule.getEnrollStartTime())) {
            // 当前时间在报名开始时间之前
            return 0; // 等待报名
        } else if (now.isBefore(schedule.getEnrollEndTime())) {
            // 当前时间在报名时间内
            return 1; // 报名中
        } else if (now.isBefore(schedule.getActivityStartTime())) {
            // 当前时间在报名结束和活动开始之间
            return 2; // 报名结束
        } else if (now.isBefore(schedule.getActivityEndTime())) {
            // 当前时间在活动时间内
            return 3; // 活动进行中
        } else {
            // 当前时间在活动结束时间之后
            return 4; // 活动结束
        }
    }
}