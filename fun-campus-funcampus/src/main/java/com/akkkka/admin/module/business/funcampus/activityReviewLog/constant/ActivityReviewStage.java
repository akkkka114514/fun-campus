package com.akkkka.admin.module.business.funcampus.activityReviewLog.constant;


import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * author:akkkka114514
 * create at 2026-02-15 16:00
 */
@Getter
@AllArgsConstructor
public enum ActivityReviewStage {
    DRAFT(0),
    INITIAL_CONTENT_REVIEW(1),
    CONTENT_CHECK(2),
    FINAL_CONTENT_REVIEW(3),
    ENROLLMENT_REVIEW(4),
    COMPLETION_REVIEW(5);

    /**
     * 数据库存储值（int），与 activity_review_log.review_stage 列一一对应
     */
    @EnumValue
    private final int order;

}
