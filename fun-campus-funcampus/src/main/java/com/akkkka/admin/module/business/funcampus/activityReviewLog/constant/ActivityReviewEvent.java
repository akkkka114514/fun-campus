package com.akkkka.admin.module.business.funcampus.activityReviewLog.constant;

/**
 * author:akkkka114514
 * create at 2026-02-15 16:07
 */
public enum ActivityReviewEvent {
    SUBMIT,
    INITIAL_REVIEW_PASS,         // 初审通过
    INITIAL_REVIEW_REJECT,       // 初审退回
    CHECK_PASS,
    FINAL_REVIEW_PASS,           // 终审通过
    FINAL_REVIEW_REJECT,         // 终审退回
    ENROLL_REVIEW_PASS,    // 报名审核通过
    COMPLETION_REVIEW_PASS,//完结审核通过
    COMPLETION_REVIEW_REJECT//完结审核退回
}
