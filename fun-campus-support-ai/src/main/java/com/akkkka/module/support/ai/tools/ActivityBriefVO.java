package com.akkkka.module.support.ai.tools;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 活动摘要（AI 工具查询结果载体）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Data
public class ActivityBriefVO {

    /**
     * 活动id
     */
    private Long id;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动地点
     */
    private String position;

    /**
     * 能得到的学分
     */
    private BigDecimal scoreCanGet;

    /**
     * 是否付费活动
     */
    private Boolean paidFlag;

    /**
     * 报名费（单位：分）
     */
    private Integer priceFen;

    /**
     * 报名截止时间
     */
    private LocalDateTime enrollEndTime;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityStartTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEndTime;
}