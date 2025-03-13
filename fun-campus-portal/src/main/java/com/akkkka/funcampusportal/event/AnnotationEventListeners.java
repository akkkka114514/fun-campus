package com.akkkka.funcampusportal.event;

import javax.annotation.Resource;

import com.akkkka.funcampusportal.service.IActivityService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author Zoe Wu
 */
@Component
@Slf4j
public class AnnotationEventListeners {
    @Resource
    private IActivityService activityService;
    @EventListener(AddActivityEvent.class)
    public void onAddActivity(AddActivityEvent event) throws Exception {
        log.info("received event :{}",event.getSource());
        activityService.changeStatusOnTime(event.getActivityId());
    }
}
