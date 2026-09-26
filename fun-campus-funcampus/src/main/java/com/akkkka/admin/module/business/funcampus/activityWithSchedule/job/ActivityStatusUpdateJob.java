package com.akkkka.admin.module.business.funcampus.activityWithSchedule.job;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityStatusCacheManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import com.akkkka.module.support.job.core.SmartJob;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 活动状态更新任务（小任务）
 * 高频执行：消费当天关键活动名单，按时间表推进状态。
 * - 名单只做提示：缺失或过期时回源查库重建（自愈）；数据缺失、单条异常均隔离跳过；
 * - 状态允许倒退：时间表后调导致期望状态小于当前状态时，回退状态并清空报名数据
 *   （同一事务 + 原状态条件更新，失败可重试），倒退后报名窗口重开即可重新报名；
 * - 状态更新带原状态条件（乐观并发），与业务并发改状态时互不覆盖；
 * - 消费确认：处理妥当且当天已无未来关键时间点的活动移出名单，后续轮次不再重复检查；
 *   处理失败的保留在名单里，下一轮重试
 *
 * @Author akkkka
 * @Date 2025-09-07
 * @Copyright your-copyright
 */
@Slf4j
@Service
public class ActivityStatusUpdateJob implements SmartJob {

    @Resource
    private ActivityStatusCacheManager activityStatusCacheManager;

    @Resource
    private ActivityManager activityManager;

    @Resource
    private ActivityScheduleManager activityScheduleManager;

    @Resource
    private ActivityWithScheduleService activityWithScheduleService;

    /**
     * 执行活动状态更新
     * 名单缺失/过期时由缓存层自动回源重建，返回的名单保证新鲜
     *
     * @param param 可选参数
     * @return 执行结果描述
     */
    @Override
    public String run(String param) {
        LocalDateTime now = LocalDateTime.now();

        List<Long> todayCriticalActivityIds = activityStatusCacheManager.getTodayCriticalActivityIds();
        if (todayCriticalActivityIds.isEmpty()) {
            return "今天没有关键时间点的活动，无需更新";
        }

        List<ActivityEntity> activities = queryStatusUpdatableActivities(todayCriticalActivityIds);
        if (activities.isEmpty()) {
            return "当天关键活动中没有需要推进状态的活动";
        }

        Map<Long, ActivityScheduleEntity> scheduleMap = activityScheduleManager
                .getByActivityIds(activities.stream().map(ActivityEntity::getId).toList())
                .stream()
                .collect(Collectors.toMap(ActivityScheduleEntity::getActivityId, Function.identity()));

        int advanced = 0;
        int retreated = 0;
        int removed = 0;
        int skipped = 0;

        for (ActivityEntity activity : activities) {
            try {
                ActivityScheduleEntity schedule = scheduleMap.get(activity.getId());
                if (schedule == null) {
                    skipped++;
                    continue;
                }
                ActivityStatus expectedStatus = calculateActivityStatus(now, schedule);
                if (expectedStatus == null) {
                    skipped++;
                    continue;
                }
                ActivityStatus currentStatus = activity.getStatus();
                boolean handled = true;
                if (expectedStatus.isAfter(currentStatus)) {
                    // 前进：仅当状态未被并发修改时更新
                    handled = activityManager.updateStatusIfMatch(activity.getId(), expectedStatus, currentStatus);
                    if (handled) {
                        advanced++;
                    } else {
                        skipped++;
                    }
                } else if (expectedStatus.isBefore(currentStatus)) {
                    // 倒退：状态回退 + 清空报名数据（同一事务，条件不满足时放弃，下轮重试）
                    handled = activityWithScheduleService.retreatActivityStatusTransaction(
                            activity.getId(), currentStatus, expectedStatus);
                    if (handled) {
                        retreated++;
                    } else {
                        skipped++;
                    }
                }
                // 消费确认：处理妥当且当天已无未来关键时间点时移出名单，后续轮次不再重复检查；
                // 处理失败的保留在名单里，下一轮重试
                if (handled && !activityStatusCacheManager.hasFutureKeyTimeToday(schedule)) {
                    activityStatusCacheManager.removeFromTodayCache(activity.getId());
                    removed++;
                }
            } catch (Exception e) {
                // 单条隔离：一条失败不影响其他活动
                log.error("活动状态更新失败，activityId={}", activity.getId(), e);
                skipped++;
            }
        }

        return String.format("活动状态更新完成：前进%d个，倒退%d个，跳过%d个，移出名单%d个",
                advanced, retreated, skipped, removed);
    }

    /**
     * 查询名单中需要推进状态的活动
     * 只保留未删除且状态在 0~3 的活动；待审核(9)、已结束(4)等不由本任务驱动
     *
     * @param activityIds 名单中的活动ID
     * @return 待推进状态的活动列表
     */
    private List<ActivityEntity> queryStatusUpdatableActivities(List<Long> activityIds) {
        LambdaQueryWrapper<ActivityEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ActivityEntity::getId, activityIds);
        queryWrapper.eq(ActivityEntity::getDeletedFlag, false);
        queryWrapper.in(ActivityEntity::getStatus, ActivityStatus.pendingTimeLineCodes());
        return activityManager.list(queryWrapper);
    }

    /**
     * 根据当前时间和活动时间表计算活动应该处于的状态
     * 计算逻辑委托 {@link ActivityStatus#calculate}，与新建活动初始化状态同源
     * 时间字段缺失时返回 null，由调用方跳过
     *
     * @param now 当前时间
     * @param schedule 活动时间表
     * @return 活动状态，无法计算时返回 null
     */
    private ActivityStatus calculateActivityStatus(LocalDateTime now, ActivityScheduleEntity schedule) {
        return ActivityStatus.calculate(now,
                schedule.getEnrollStartTime(),
                schedule.getEnrollEndTime(),
                schedule.getActivityStartTime(),
                schedule.getActivityEndTime());
    }
}
