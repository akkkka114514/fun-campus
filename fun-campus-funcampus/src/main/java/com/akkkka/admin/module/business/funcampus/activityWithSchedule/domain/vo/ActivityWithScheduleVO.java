package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import lombok.Data;

/**
 * 活动与时间表 综合VO
 *
 * @Author akkkka114514
 * @Date 2025-09-07
 * @Copyright akkkka114514
 */
@Data
public class ActivityWithScheduleVO {

    /** 活动基本信息 */
    private ActivityVO activity;

    /** 活动时间表 */
    private ActivityScheduleVO schedule;
}
