package com.akkkka.admin.module.business.funcampus.activityReviewLog.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动审核行为（写入 activity_review_log.action）
 * <p>
 * code 与 SQL 列注释一一对应：0-提交 1-初审通过 2-初审退回 3-审阅通过
 * 4-终审通过 5-终审退回 6-报名审核通过 7-完结审核通过 8-完结审核退回
 *
 * author:akkkka114514
 * create at 2026-02-15 16:07
 */
@Getter
@AllArgsConstructor
public enum ActivityReviewEvent {
    SUBMIT(0, "提交"),
    INITIAL_REVIEW_PASS(1, "初审通过"),
    INITIAL_REVIEW_REJECT(2, "初审退回"),
    CHECK_PASS(3, "审阅通过"),
    FINAL_REVIEW_PASS(4, "终审通过"),
    FINAL_REVIEW_REJECT(5, "终审退回"),
    ENROLL_REVIEW_PASS(6, "报名审核通过"),
    COMPLETION_REVIEW_PASS(7, "完结审核通过"),
    COMPLETION_REVIEW_REJECT(8, "完结审核退回");

    /**
     * 数据库存储值（int），与 activity_review_log.action 列一一对应
     */
    @EnumValue
    private final int code;

    /**
     * 展示名称
     */
    private final String label;
}
