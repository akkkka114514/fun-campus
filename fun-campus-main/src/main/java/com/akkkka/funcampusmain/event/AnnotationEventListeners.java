package com.akkkka.funcampusmain.event;

import com.akkkka.funcampusmainapi.api.IActivityService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnnotationEventListeners {
    @Resource
    private IActivityService activityService;
    @EventListener(AddActivityEvent.class)
    public void onAddActivity(AddActivityEvent event) throws SchedulerException {
        log.info("received event :{}",event.getSource());
        activityService.changeStatusOnTime(event.getActivityId());
    }
}
