package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;

import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import com.alibaba.cola.statemachine.Action;
import com.alibaba.cola.statemachine.Condition;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * author:akkkka114514
 * create at 2026-04-15 20:03
 */
@Component
@AllArgsConstructor
public class ActivityReviewStateMachineBuilder {
    private ActivityWithScheduleService activityWithScheduleService;

    private ApplicationEventPublisher eventPublisher;

    public static StateMachine<ActivityReviewStage, ActivityReviewEvent, ActivityReviewStateMachineContext> build() {
        StateMachineBuilder<ActivityReviewStage, ActivityReviewEvent, ActivityReviewStateMachineContext> builder =
                StateMachineBuilderFactory.create();

        // 1. 草稿 -> 初审
        builder.externalTransition()
                .from(ActivityReviewStage.DRAFT)
                .to(ActivityReviewStage.INITIAL_CONTENT_REVIEW)
                .on(ActivityReviewEvent.SUBMIT)
                .perform(
                        (activityReviewStage, s1, activityReviewEvent, activityReviewStateMachineContext) -> {

                        });

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


        // 6. 终审 -> 报名审核
        builder.externalTransition()
                .from(ActivityReviewStage.FINAL_CONTENT_REVIEW)
                .to(ActivityReviewStage.ENROLLMENT_REVIEW)
                .on(ActivityReviewEvent.FINAL_REVIEW_PASS)
                .perform(logAction("进入报名审核"));


        return builder.build("activityReviewStateMachine");
    }

    // 条件：提交时必须有内容等
    private static Condition<ActivityReviewStateMachineContext> checkSubmitCondition() {
        return ctx -> ctx != null; // 可扩展校验
    }

    // 动作：执行提交逻辑（如保存、记录日志等）
    private static Action<ActivityReviewStage, ActivityReviewEvent, ActivityReviewStateMachineContext> doSubmit() {
        return (from, to, event, ctx) -> {
            System.out.println("执行提交动作");
            // 可调用 service 更新状态、记录操作人等
        };
    }

    // 通用日志动作
    private static Action<ActivityReviewStage, ActivityReviewEvent, ActivityReviewStateMachineContext> logAction(String msg) {
        return (from, to, event, ctx) -> {
            System.out.println(msg);
        };
    }
}
