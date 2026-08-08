package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;

import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.alibaba.cola.statemachine.Action;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * author:akkkka114514
 * create at 2026-04-20 14:23
 */
public class ActivityReviewStateMachineActions {

    @Component
    @AllArgsConstructor
    public static class DraftSubmitAction implements Action<ActivityReviewStage, ActivityReviewEvent, ActivityReviewStateMachineContext> {
        private final ApplicationEventPublisher eventPublisher;

        @Override
        public void execute(ActivityReviewStage activityReviewStage, ActivityReviewStage s1, ActivityReviewEvent activityReviewEvent, ActivityReviewStateMachineContext activityReviewStateMachineContext) {
            eventPublisher.publishEvent(new ActivityReviewEvents.DraftSubmitEvent(this));
        }
    }
}
