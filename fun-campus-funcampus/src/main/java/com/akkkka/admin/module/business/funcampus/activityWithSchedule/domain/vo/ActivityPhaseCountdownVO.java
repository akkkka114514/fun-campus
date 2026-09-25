package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import lombok.Data;

/**
 * 活动阶段倒计时VO
 *
 * @Author akkkka114514
 * @Date 2026-08-16
 * @Copyright akkkka114514
 */
@Data
public class ActivityPhaseCountdownVO {

    /** 当前阶段名称 */
    private String currentPhase;

    /** 下一阶段名称 */
    private String nextPhase;

    /** 下一阶段开始时间 */
    private String nextPhaseStartTime;

    /** 距离下一阶段剩余秒数，-1表示活动已结束 */
    private Long remainingSeconds;

}
