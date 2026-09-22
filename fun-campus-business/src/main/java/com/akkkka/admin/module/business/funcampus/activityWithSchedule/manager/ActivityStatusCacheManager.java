package com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 当天关键活动名单 缓存管理器
 * 语义约定：缓存只做加速提示，数据库才是事实。
 * - 名单新鲜（今天生成过）时直接使用；
 * - 名单缺失或过期时回源查库重建，各类异常最终都收敛到这条自愈路径；
 * - 写入端（新建/编辑时间表）尽力投递，失败不影响业务，漏投由重建与回源兜底；
 * - 消费端处理完成（当天已无未来关键时间点）后移除条目，避免后续轮次重复检查
 *
 * @Author akkkka114514
 * @Date 2025-09-21
 * @Copyright akkkka114514
 */
@Slf4j
@Service
public class ActivityStatusCacheManager {

    /**
     * 当天关键活动ID集合
     */
    private static final String TODAY_CRITICAL_ACTIVITIES_KEY = "funcampus:today_critical_activities";

    /**
     * 名单生成日期（yyyy-MM-dd），用于新鲜度判断
     */
    private static final String TODAY_CRITICAL_ACTIVITIES_DATE_KEY = "funcampus:today_critical_activities:generated_date";

    /**
     * 缓存兜底过期时间（小时），仅用于防止垃圾堆积，正确性不依赖它
     */
    private static final long CACHE_EXPIRE_HOURS = 48;

    @Resource
    private ActivityScheduleManager activityScheduleManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取当天关键活动ID名单（保证新鲜）
     * 名单新鲜直接返回；缺失或过期时回源查库重建后返回
     *
     * @return 当天关键活动ID列表
     */
    public List<Long> getTodayCriticalActivityIds() {
        if (isFresh()) {
            return readTodayCriticalActivityIds();
        }
        return rebuild();
    }

    /**
     * 回源数据库全量重建名单，并写入生成日期
     * 先删日期标记再重建，中间态会被读取方识别为过期并触发回源，避免拿到半成品
     *
     * @return 重建后的活动ID列表
     */
    public List<Long> rebuild() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime startOfNextDay = startOfDay.plusDays(1);

        List<Long> activityIds = activityScheduleManager
                .getActivitiesWithKeyTime(startOfDay, startOfNextDay)
                .stream()
                .map(ActivityScheduleEntity::getActivityId)
                .toList();

        redisTemplate.delete(TODAY_CRITICAL_ACTIVITIES_DATE_KEY);
        redisTemplate.delete(TODAY_CRITICAL_ACTIVITIES_KEY);
        if (!activityIds.isEmpty()) {
            redisTemplate.opsForSet().add(TODAY_CRITICAL_ACTIVITIES_KEY, activityIds.toArray());
            redisTemplate.expire(TODAY_CRITICAL_ACTIVITIES_KEY, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
        redisTemplate.opsForValue().set(TODAY_CRITICAL_ACTIVITIES_DATE_KEY,
                LocalDate.now().toString(), CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return activityIds;
    }

    /**
     * 尽力投递：时间表今天有关键时间点时把活动加入名单
     * 失败仅记录日志，绝不影响业务；漏投由重建与回源兜底
     *
     * @param schedule 活动时间表
     */
    public void tryAddToTodayCache(ActivityScheduleEntity schedule) {
        if (schedule == null || schedule.getActivityId() == null) {
            return;
        }
        try {
            if (!hasKeyTimeToday(schedule)) {
                return;
            }
            redisTemplate.opsForSet().add(TODAY_CRITICAL_ACTIVITIES_KEY, schedule.getActivityId());
            redisTemplate.expire(TODAY_CRITICAL_ACTIVITIES_KEY, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("投递当天关键活动名单失败，activityId={}", schedule.getActivityId(), e);
        }
    }

    /**
     * 消费确认：从当天名单移除活动
     * 失败仅记录日志，条目残留在下一轮重建时自然收敛
     *
     * @param activityId 活动ID
     */
    public void removeFromTodayCache(Long activityId) {
        if (activityId == null) {
            return;
        }
        try {
            redisTemplate.opsForSet().remove(TODAY_CRITICAL_ACTIVITIES_KEY, activityId);
        } catch (Exception e) {
            log.warn("从当天名单移除活动失败，activityId={}", activityId, e);
        }
    }

    /**
     * 判断时间表今天是否还有未来的关键时间点（报名开始/结束、活动开始/结束）
     * 已无未来关键时间点，说明该活动今天不会再触发状态变更，可以从名单移除
     *
     * @param schedule 活动时间表
     * @return 是否还有未来关键时间点
     */
    public boolean hasFutureKeyTimeToday(ActivityScheduleEntity schedule) {
        if (schedule == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfNextDay = LocalDate.now().plusDays(1).atStartOfDay();
        return isFutureKeyTime(schedule.getEnrollStartTime(), now, startOfNextDay)
                || isFutureKeyTime(schedule.getEnrollEndTime(), now, startOfNextDay)
                || isFutureKeyTime(schedule.getActivityStartTime(), now, startOfNextDay)
                || isFutureKeyTime(schedule.getActivityEndTime(), now, startOfNextDay);
    }

    /**
     * 判断时间表今天是否有关键时间点（含已过去的时间点，用于投递判断）
     */
    private boolean hasKeyTimeToday(ActivityScheduleEntity schedule) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime startOfNextDay = startOfDay.plusDays(1);
        return inTimeRange(schedule.getEnrollStartTime(), startOfDay, startOfNextDay)
                || inTimeRange(schedule.getEnrollEndTime(), startOfDay, startOfNextDay)
                || inTimeRange(schedule.getActivityStartTime(), startOfDay, startOfNextDay)
                || inTimeRange(schedule.getActivityEndTime(), startOfDay, startOfNextDay);
    }

    private boolean inTimeRange(LocalDateTime time, LocalDateTime start, LocalDateTime end) {
        return time != null && !time.isBefore(start) && time.isBefore(end);
    }

    private boolean isFutureKeyTime(LocalDateTime time, LocalDateTime now, LocalDateTime end) {
        return time != null && time.isAfter(now) && time.isBefore(end);
    }

    /**
     * 名单是否为当天生成
     */
    private boolean isFresh() {
        Object value = redisTemplate.opsForValue().get(TODAY_CRITICAL_ACTIVITIES_DATE_KEY);
        return value != null && LocalDate.now().toString().equals(value.toString());
    }

    /**
     * 读取名单；元素可能被反序列化为 Integer 等数字类型，统一转 Long
     */
    private List<Long> readTodayCriticalActivityIds() {
        Set<Object> members = redisTemplate.opsForSet().members(TODAY_CRITICAL_ACTIVITIES_KEY);
        if (members == null || members.isEmpty()) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>(members.size());
        for (Object member : members) {
            if (member instanceof Number number) {
                ids.add(number.longValue());
            } else if (member != null) {
                try {
                    ids.add(Long.parseLong(member.toString()));
                } catch (NumberFormatException e) {
                    log.warn("当天关键活动名单中存在无法解析的元素：{}", member);
                }
            }
        }
        return ids;
    }
}
