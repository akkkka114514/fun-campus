package com.akkkka.module.support.ai.tools;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 报名记录摘要（AI 工具查询结果载体）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Data
public class EnrollmentBriefVO {

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 活动标题
     */
    private String activityTitle;

    /**
     * 报名时间
     */
    private LocalDateTime enrollTime;

    /**
     * 是否已签到
     */
    private Boolean signInStatus;

    /**
     * 是否已签退
     */
    private Boolean signOutStatus;

    /**
     * 活动当前状态文案（如：报名中/进行中/已结束）
     */
    private String activityStatusLabel;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityStartTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEndTime;
}