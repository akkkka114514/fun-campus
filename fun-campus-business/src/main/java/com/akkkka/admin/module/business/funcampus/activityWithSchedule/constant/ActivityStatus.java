package com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant;

import java.util.List;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动状态
 * <p>
 * 按维度分为两类：
 * 1. 时间阶段（0~4）：活动主线，由 ActivityStatusUpdateJob 依据时间表自动推进/回退；
 *    其中 4-已结束为时间阶段终点，不作为推进候选（防止完成后被自动回退）；
 * 2. 业务状态（9）：报名需审核时的等待审核状态，预留，当前审核流程未写入 activity.status。
 * <p>
 * 签到/签退不设状态值，是否开放由时间窗口实时校验（见 ActivityEnrollmentService 的签到/签退窗口校验）。
 * <p>
 * code 与字典 t_dict_data（dict_id=4，ACTIVITY_STATUS）的 data_value 一一对应，
 * label 与 data_label 一一对应；时间阶段先后顺序由 {@link #TIME_LINE} 显式定义，
 * 流转判断使用 isAfter/isBefore，禁止直接比较 code 大小。
 *
 * author:akkkka114514
 * create at 2025-09-07 19:49
 */
@Getter
@AllArgsConstructor
public enum ActivityStatus {

    WAIT_ENROLL(0, "等待报名"),
    ENROLLING(1, "报名中"),
    ENROLL_ENDED(2, "报名结束"),
    ONGOING(3, "进行中"),
    FINISHED(4, "已结束"),

    /**
     * 业务状态：等待审核（预留，审核流程暂未写入）
     */
    WAIT_ENROLLMENT_REVIEW(9, "待审核");

    /**
     * 数据库存储值（tinyint），与字典 ACTIVITY_STATUS 的 data_value 对应
     */
    @EnumValue
    private final int code;

    /**
     * 展示名称，与字典 ACTIVITY_STATUS 的 data_label 对应
     */
    private final String label;

    /**
     * 时间阶段显式顺序链：顺序即流程先后，禁止依赖 code 大小比较
     */
    private static final List<ActivityStatus> TIME_LINE = List.of(
            WAIT_ENROLL, ENROLLING, ENROLL_ENDED, ONGOING, FINISHED);

    /**
     * 是否属于定时任务驱动的时间阶段
     */
    public boolean isTimeDriven() {
        return TIME_LINE.contains(this);
    }

    /**
     * 是否晚于另一时间阶段（按 TIME_LINE 顺序）
     */
    public boolean isAfter(ActivityStatus other) {
        return timeOrder() > other.timeOrder();
    }

    /**
     * 是否早于另一时间阶段（按 TIME_LINE 顺序）
     */
    public boolean isBefore(ActivityStatus other) {
        return timeOrder() < other.timeOrder();
    }

    /**
     * 时间阶段顺序（0 起）；非时间阶段状态调用即抛异常，防止误用
     */
    private int timeOrder() {
        int index = TIME_LINE.indexOf(this);
        if (index < 0) {
            throw new IllegalStateException("非时间阶段状态，无顺序：" + this);
        }
        return index;
    }

    /**
     * 全部时间阶段 code，供查询条件使用
     */
    public static List<Integer> timeLineCodes() {
        return TIME_LINE.stream().map(ActivityStatus::getCode).toList();
    }

    /**
     * 待推进时间阶段 code（0~3），供定时任务查询候选活动使用：
     * 4-已结束是时间阶段终点，不作为扫描起点（防止完成后被自动回退）；
     * 9-待审核是业务状态，同样不由时间任务驱动
     */
    public static List<Integer> pendingTimeLineCodes() {
        return TIME_LINE.stream()
                .filter(status -> status != FINISHED)
                .map(ActivityStatus::getCode)
                .toList();
    }

    /**
     * 按 code 反查枚举；无匹配返回 null
     */
    public static ActivityStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ActivityStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
