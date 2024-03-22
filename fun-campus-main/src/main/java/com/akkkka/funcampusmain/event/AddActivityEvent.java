package com.akkkka.funcampusmain.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class AddActivityEvent extends ApplicationEvent {
    Integer activityId;
    public AddActivityEvent(Object source,Integer activityId) {
        super(source);
        this.activityId = activityId;
    }
}
