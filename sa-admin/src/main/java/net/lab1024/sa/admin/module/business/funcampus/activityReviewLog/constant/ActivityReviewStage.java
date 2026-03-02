package net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.constant;

import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * author:akkkka114514
 * create at 2026-02-15 16:00
 */
public final class ActivityReviewStage {
    //对提交的活动初审，能修改活动信息
    public static final Integer INITIAL_REVIEW= 1;
    //对提交的活动审阅，只提供意见，不能修改活动信息
    public static final Integer CHECK=2;
    //对提交的活动终审，能修改活动信息
    public static final Integer FINAL_REVIEW=3;
    //完结审核，用于活动结束后审核
    public static final Integer END_REVIEW=4;
}
