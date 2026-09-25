package com.akkkka.admin.module.business.funcampus.activityReviewLog.service;

import org.springframework.context.ApplicationEvent;

/**
 * author:akkkka114514
 * create at 2026-04-20 14:12
 */
public class ActivityReviewEvents {
    public static class DraftSubmitEvent extends ApplicationEvent {

        public DraftSubmitEvent(Object source) {
            super(source);
        }
    }
}
