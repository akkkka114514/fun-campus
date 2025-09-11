package net.lab1024.sa.admin.module.business.funcampus.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.funcampus.constant.ActivityStatus;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.manager.ActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.manager.ActivityScheduleManager;
import net.lab1024.sa.base.config.AsyncConfig;
import net.lab1024.sa.base.module.support.job.core.SmartJob;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
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

    @Resource(name = AsyncConfig.ASYNC_EXECUTOR_THREAD_NAME)
    private AsyncTaskExecutor taskExecutor;

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
        log.info("开始执行活动状态更新任务");
        LocalDateTime now = LocalDateTime.now();
        int totalUpdated = 0;
        int currentPage = 1;
        boolean hasMore = true;

        // 获取Redis中存储的当天关键活动ID列表
        List<Long> todayCriticalActivityIds = getTodayCriticalActivityIds();
        
        // 如果没有关键活动，直接返回
        if (todayCriticalActivityIds == null || todayCriticalActivityIds.isEmpty()) {
            log.info("今天没有关键活动，无需更新");
            return "今天没有关键活动，无需更新";
        }
        
        log.info("获取到{}个关键活动需要检查状态", todayCriticalActivityIds.size());

        // 分批处理所有需要检查状态的活动
        while (hasMore) {
            log.debug("正在处理第{}页活动数据", currentPage);
            // 分页查询需要检查状态的活动
            IPage<ActivityEntity> page = queryActivitiesForStatusCheck(currentPage, PAGE_SIZE);
            
            List<ActivityEntity> activities = page.getRecords();
            if (activities.isEmpty()) {
                log.debug("第{}页没有活动数据，结束处理", currentPage);
                break;
            }
            
            log.debug("第{}页获取到{}个活动", currentPage, activities.size());

            // 过滤出当天的关键活动
            List<ActivityEntity> criticalActivities = activities.stream()
                    .filter(activity -> todayCriticalActivityIds.contains(activity.getId()))
                    .toList();
            
            log.debug("第{}页过滤出{}个关键活动", currentPage, criticalActivities.size());

            // 批量处理当前页的关键活动
            if (!criticalActivities.isEmpty()) {
                int updatedInThisPage = processActivitiesBatch(criticalActivities, now);
                totalUpdated += updatedInThisPage;
                log.debug("第{}页更新了{}个活动状态", currentPage, updatedInThisPage);
            }

            // 检查是否还有更多数据
            hasMore = currentPage < page.getPages();
            currentPage++;
        }

        String result = String.format("活动状态更新完成，总共更新%d个活动", totalUpdated);
        log.info(result);
        return result;
    }

    /**
     * 从Redis获取当天关键活动ID列表
     *
     * @return 当天关键活动ID列表
     */
    @SuppressWarnings("unchecked")
    private List<Long> getTodayCriticalActivityIds() {
        log.debug("从Redis获取当天关键活动ID列表");
        Object obj = redisTemplate.opsForValue().get(TODAY_CRITICAL_ACTIVITIES_KEY);
        if (obj instanceof List) {
            return (List<Long>) obj;
        }
        log.debug("Redis中未找到当天关键活动ID列表");
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
        log.debug("分页查询活动数据，页码:{}，每页大小:{}", pageNum, pageSize);
        Page<ActivityEntity> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<ActivityEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityEntity::getDeletedFlag, false);  // 只查询未删除的活动
        queryWrapper.ne(ActivityEntity::getStatus, ActivityStatus.END_SIGNIN);  // 排除已结束的活动

        IPage<ActivityEntity> result = activityManager.page(page, queryWrapper);
        log.debug("查询到{}个活动，共{}页", result.getRecords().size(), result.getPages());
        return result;
    }

    /**
     * 批量处理活动状态更新
     *
     * @param activities 活动列表
     * @param now 当前时间
     * @return 更新的活动数量
     */
    private int processActivitiesBatch(List<ActivityEntity> activities, LocalDateTime now) {
        log.debug("开始批量处理{}个活动的状态更新", activities.size());
        
        // 批量获取这些活动的时间表信息
        List<Long> activityIds = activities.stream()
                .map(ActivityEntity::getId)
                .toList();
        
        log.debug("批量获取{}个活动的时间表信息", activityIds.size());
        List<ActivityScheduleEntity> schedules = activityScheduleManager.getByActivityIds(activityIds);
        log.debug("获取到{}个活动的时间表信息", schedules.size());
        
        // 将时间表信息转换为Map方便查找
        Map<Long, ActivityScheduleEntity> scheduleMap = schedules.stream()
                .collect(Collectors.toMap(
                        ActivityScheduleEntity::getActivityId, 
                        schedule -> schedule
                ));

        // 构建需要更新的活动状态映射
        Map<Long, Integer> statusToUpdate = new HashMap<>();
        
        for (ActivityEntity activity : activities) {
            ActivityScheduleEntity schedule = scheduleMap.get(activity.getId());
            if (schedule == null) {
                log.warn("活动ID:{}未找到时间表信息，跳过处理", activity.getId());
                continue; // 没有时间表信息，跳过
            }

            // 根据时间表计算活动应该处于的状态
            Integer newStatus = calculateActivityStatus(activity.getId(), now, schedule);
            log.debug("活动ID:{}计算出的新状态为:{}", activity.getId(), newStatus);

            // 如果状态有变化，则添加到更新映射中
            if (!newStatus.equals(activity.getStatus())) {
                log.debug("活动ID:{}状态从{}更新为{}", activity.getId(), activity.getStatus(), newStatus);
                statusToUpdate.put(activity.getId(), newStatus);
            } else {
                log.debug("活动ID:{}状态未发生变化，无需更新", activity.getId());
            }
        }
        
        // 执行批量更新
        if (!statusToUpdate.isEmpty()) {
            log.debug("开始批量更新{}个活动的状态", statusToUpdate.size());
            int updatedCount = activityManager.updateStatusBatch(statusToUpdate);
            log.debug("批量更新完成，成功更新{}个活动", updatedCount);
            return updatedCount;
        }
        
        return 0;
    }

    /**
     * 根据当前时间和活动时间表计算活动应该处于的状态
     *
     * @param activityId 活动ID
     * @param now 当前时间
     * @param schedule 活动时间表
     * @return 活动状态
     */
    private Integer calculateActivityStatus(Long activityId, LocalDateTime now, ActivityScheduleEntity schedule) {
        log.debug("活动ID:{} 计算活动状态，当前时间:{}，报名开始时间:{}，报名结束时间:{}，活动开始时间:{}，活动结束时间:{}，签到开始时间:{}，签到结束时间:{}", 
                activityId, now, schedule.getEnrollStartTime(), schedule.getEnrollEndTime(), 
                schedule.getActivityStartTime(), schedule.getActivityEndTime(),
                schedule.getSigninStartTime(), schedule.getSigninEndTime());
        
        // 活动状态定义：
        // ActivityStatus.START_ENROLL(1) - 报名中
        // ActivityStatus.END_ENROLL(2) - 报名结束
        // ActivityStatus.START_ACTIVITY(3) - 活动进行中
        // ActivityStatus.END_ACTIVITY(4) - 活动结束
        // ActivityStatus.START_SIGNIN(5) - 签到开始
        // ActivityStatus.END_SIGNIN(6) - 签到结束

        if (now.isBefore(schedule.getEnrollStartTime())) {
            // 当前时间在报名开始时间之前
            log.debug("活动ID:{} 当前时间在报名开始时间之前，状态为等待报名", activityId);
            return ActivityStatus.START_ENROLL; // 报名中
        } else if (now.isBefore(schedule.getEnrollEndTime())) {
            // 当前时间在报名时间内
            log.debug("活动ID:{} 当前时间在报名时间内，状态为报名中", activityId);
            return ActivityStatus.START_ENROLL; // 报名中
        } else if (now.isBefore(schedule.getActivityStartTime())) {
            // 当前时间在报名结束和活动开始之间
            log.debug("活动ID:{} 当前时间在报名结束和活动开始之间，状态为报名结束", activityId);
            return ActivityStatus.END_ENROLL; // 报名结束
        } else if (now.isBefore(schedule.getActivityEndTime())) {
            // 当前时间在活动时间内
            log.debug("活动ID:{} 当前时间在活动时间内，状态为活动进行中", activityId);
            return ActivityStatus.START_ACTIVITY; // 活动进行中
        } else if (schedule.getSigninStartTime() != null && now.isBefore(schedule.getSigninStartTime())) {
            // 当前时间在活动结束时间和签到开始时间之间
            log.debug("活动ID:{} 当前时间在活动结束时间和签到开始时间之间，状态为活动结束", activityId);
            return ActivityStatus.END_ACTIVITY; // 活动结束
        } else if (schedule.getSigninStartTime() != null && schedule.getSigninEndTime() != null 
                && !now.isBefore(schedule.getSigninStartTime()) && now.isBefore(schedule.getSigninEndTime())) {
            // 当前时间在签到时间内
            log.debug("活动ID:{} 当前时间在签到时间内，状态为签到开始", activityId);
            return ActivityStatus.START_SIGNIN; // 签到开始
        } else {
            // 当前时间在签到结束时间之后，或没有签到时间安排
            log.debug("活动ID:{} 当前时间在签到结束时间之后或无签到安排，状态为签到结束", activityId);
            return ActivityStatus.END_SIGNIN; // 签到结束
        }
    }
}