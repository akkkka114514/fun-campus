package com.akkkka.admin.module.business.funcampus.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityScheduleEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityScheduleManager;
import com.akkkka.module.support.ai.tools.ActivityBriefVO;
import com.akkkka.module.support.ai.tools.ActivityQueryPort;
import com.akkkka.module.support.ai.tools.EnrollmentBriefVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 活动查询端口实现（供 AI 工具调用）
 * <p>
 * 只读查询：可报名活动（报名中状态、按报名截止时间升序）、
 * 我的报名（含活动信息、时间表与签到状态）。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivityQueryPortImpl implements ActivityQueryPort {

    /**
     * 报名活动粗筛上限（防止全量扫描；先按创建时间取一批，再按报名截止时间精排）
     */
    private static final int MAX_SCAN = 50;

    /**
     * 活动最大列表上限保护
     */
    private static final int MAX_LIMIT = 20;

    private final ActivityManager activityManager;
    private final ActivityScheduleManager activityScheduleManager;
    private final ActivityEnrollmentManager activityEnrollmentManager;

    @Override
    public List<ActivityBriefVO> queryEnrollableActivities(String keyword, int limit) {
        LambdaQueryWrapper<ActivityEntity> wrapper = Wrappers.lambdaQuery(ActivityEntity.class)
                .eq(ActivityEntity::getDeletedFlag, false)
                .eq(ActivityEntity::getStatus, ActivityStatus.ENROLLING)
                .orderByDesc(ActivityEntity::getCreateTime)
                .last("LIMIT " + MAX_SCAN);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ActivityEntity::getTitle, keyword);
        }
        List<ActivityEntity> activities = activityManager.list(wrapper);
        if (activities.isEmpty()) {
            return List.of();
        }

        List<Long> activityIds = activities.stream().map(ActivityEntity::getId).toList();
        Map<Long, ActivityScheduleEntity> scheduleMap = activityScheduleManager.listByIds(activityIds).stream()
                .collect(Collectors.toMap(ActivityScheduleEntity::getActivityId, Function.identity(), (a, b) -> a));

        int safeLimit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        return activities.stream()
                .sorted(Comparator.comparing(
                        (ActivityEntity activity) -> {
                            ActivityScheduleEntity schedule = scheduleMap.get(activity.getId());
                            return schedule == null ? null : schedule.getEnrollEndTime();
                        },
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(safeLimit)
                .map(activity -> buildActivityBrief(activity, scheduleMap.get(activity.getId())))
                .toList();
    }

    @Override
    public List<EnrollmentBriefVO> queryUserEnrollments(Long userId, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        List<ActivityEnrollmentEntity> enrollments = activityEnrollmentManager.list(
                Wrappers.lambdaQuery(ActivityEnrollmentEntity.class)
                        .eq(ActivityEnrollmentEntity::getUserId, userId)
                        .eq(ActivityEnrollmentEntity::getDeletedFlag, false)
                        .orderByDesc(ActivityEnrollmentEntity::getCreateTime)
                        .last("LIMIT " + safeLimit));
        if (enrollments.isEmpty()) {
            return List.of();
        }

        List<Long> activityIds = enrollments.stream().map(ActivityEnrollmentEntity::getActivityId).distinct().toList();
        Map<Long, ActivityEntity> activityMap = activityManager.listByIds(activityIds).stream()
                .collect(Collectors.toMap(ActivityEntity::getId, Function.identity(), (a, b) -> a));
        Map<Long, ActivityScheduleEntity> scheduleMap = activityScheduleManager.listByIds(activityIds).stream()
                .collect(Collectors.toMap(ActivityScheduleEntity::getActivityId, Function.identity(), (a, b) -> a));

        List<EnrollmentBriefVO> result = new ArrayList<>(enrollments.size());
        for (ActivityEnrollmentEntity enrollment : enrollments) {
            ActivityEntity activity = activityMap.get(enrollment.getActivityId());
            if (activity == null) {
                continue;
            }
            EnrollmentBriefVO vo = new EnrollmentBriefVO();
            vo.setActivityId(activity.getId());
            vo.setActivityTitle(activity.getTitle());
            vo.setEnrollTime(enrollment.getCreateTime());
            vo.setSignInStatus(Boolean.TRUE.equals(enrollment.getSignInStatus()));
            vo.setSignOutStatus(Boolean.TRUE.equals(enrollment.getSignOutStatus()));
            if (activity.getStatus() != null) {
                vo.setActivityStatusLabel(activity.getStatus().getLabel());
            }
            ActivityScheduleEntity schedule = scheduleMap.get(activity.getId());
            if (schedule != null) {
                vo.setActivityStartTime(schedule.getActivityStartTime());
                vo.setActivityEndTime(schedule.getActivityEndTime());
            }
            result.add(vo);
        }
        return result;
    }

    private ActivityBriefVO buildActivityBrief(ActivityEntity activity, ActivityScheduleEntity schedule) {
        ActivityBriefVO vo = new ActivityBriefVO();
        vo.setId(activity.getId());
        vo.setTitle(activity.getTitle());
        vo.setPosition(activity.getPosition());
        vo.setScoreCanGet(activity.getScoreCanGet());
        vo.setPaidFlag(activity.getPaidFlag());
        vo.setPriceFen(activity.getPriceFen());
        if (schedule != null) {
            vo.setEnrollEndTime(schedule.getEnrollEndTime());
            vo.setActivityStartTime(schedule.getActivityStartTime());
            vo.setActivityEndTime(schedule.getActivityEndTime());
        }
        return vo;
    }
}