package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;

import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;

/**
 * author:akkkka114514
 * create at 2026-04-15 20:03
 */
public class ActivityReviewStateMachineBuilder {
    public static StateMachine<ActivityReviewStage, ActivityReviewEvent, ActivityReviewStateMachineContext> build() {
        StateMachineBuilder<ActivityReviewStage, ActivityReviewEvent, ActivityReviewStateMachineContext> builder =
                StateMachineBuilderFactory.create();

        // 1. 草稿 -> 初审
        builder.externalTransition()
                .from(ActivityReviewStage.DRAFT)
                .to(ActivityReviewStage.INITIAL_CONTENT_REVIEW)
                .on(ActivityReviewEvent.SUBMIT)
                .when(checkSubmitCondition())
                .perform(doSubmit());

        // 2. 初审 -> 审阅（通过）
        builder.externalTransition()
                .from(ActivityReviewStage.INITIAL_CONTENT_REVIEW)
                .to(ActivityReviewStage.CONTENT_CHECK)
                .on(ActivityReviewEvent.INITIAL_REVIEW_PASS)
                .perform(logAction("初审通过"));

        // 3. 初审 -> 草稿（退回）
        builder.externalTransition()
                .from(ActivityReviewStage.INITIAL_CONTENT_REVIEW)
                .to(ActivityReviewStage.DRAFT)
                .on(ActivityReviewEvent.INITIAL_REVIEW_REJECT)
                .perform(logAction("初审退回"));

        // 4. 审阅 -> 终审（通过）
        builder.externalTransition()
                .from(ActivityReviewStage.CONTENT_CHECK)
                .to(ActivityReviewStage.FINAL_CONTENT_REVIEW)
                .on(ActivityReviewEvent.CHECK_PASS)
                .perform(logAction("审阅通过"));


        // 6. 终审 -> 报名审核 或 完结（根据是否有报名字段）
        builder.externalTransition()
                .from(ActivityReviewStage.FINAL_CONTENT_REVIEW)
                .to(ActivityReviewStage.ENROLLMENT_REVIEW)
                .on(ActivityReviewEvent.FINAL_REVIEW_PASS)
                .when(ctx -> ctx != null && ctx.hasRegistrationField())
                .perform(logAction("进入报名审核"));

        builder.externalTransition()
                .from(ActivityReviewStage.FINAL_CONTENT_REVIEW)
                .to(ActivityReviewStage.COMPLETED)
                .on(ActivityReviewEvent.FINAL_REVIEW_PASS)
                .when(ctx -> ctx != null && !ctx.hasRegistrationField())
                .perform(logAction("无报名字段，直接完结"));


        return builder.build();
    }

    // 条件：提交时必须有内容等
    private static Condition<ActivityContext> checkSubmitCondition() {
        return ctx -> ctx != null; // 可扩展校验
    }

    // 动作：执行提交逻辑（如保存、记录日志等）
    private static Action<ActivityReviewStage, ActivityReviewEvent, ActivityContext> doSubmit() {
        return (from, to, event, ctx) -> {
            System.out.println("执行提交动作，活动ID: " + ctx.getActivityId());
            // 可调用 service 更新状态、记录操作人等
        };
    }

    // 通用日志动作
    private static Action<ActivityReviewStage, ActivityReviewEvent, ActivityContext> logAction(String msg) {
        return (from, to, event, ctx) -> {
            System.out.println(msg + " | 活动ID: " + ctx.getActivityId());
        };
    }
}

public class ActivityService {

    private StateMachine<ActivityReviewStage, ActivityReviewEvent, ActivityContext> stateMachine;

    public ActivityService() {
        this.stateMachine = ActivityReviewStageMachineBuilder.build();
    }

    public void handleEvent(Long activityId, ActivityReviewStage currentState, ActivityReviewEvent event, boolean hasRegField) {
        ActivityContext ctx = new ActivityContext();
        ctx.setActivityId(activityId);
        ctx.setHasRegistrationField(hasRegField);

        ActivityReviewStage nextState = stateMachine.fireEvent(currentState, event, ctx);
        if (nextState == null) {
            throw new IllegalStateException("非法状态转移: " + currentState + " --" + event + "--> ?");
        }

        // 保存 nextState 到数据库
        System.out.println("状态变更: " + currentState + " -> " + nextState);
    }
}
