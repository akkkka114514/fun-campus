package com.akkkka.funcampusportal.quatz;

import com.akkkka.common.core.enums.ResponseEnum;
import com.akkkka.common.core.exception.GlobalException;
import com.akkkka.funcampusportal.domain.Activity;
import com.akkkka.funcampusportal.service.IActivityService;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
public class ChangeActivityStatusJob implements Job {
    @Resource
    private IActivityService activityService;
    public void execute(JobExecutionContext context) {
        Integer activityId = (Integer) context.getJobDetail().getJobDataMap().get("activityId");

        Activity activity = activityService.getById(activityId);

        if(activity==null){
            throw new GlobalException(ResponseEnum.NO_SUCH_RECORD_IN_DB);
        }
        if(activity.getIsDeleted()==1){
            throw new GlobalException(ResponseEnum.RECORD_DELETED_LOGICALLY);
        }

        if (activity.getStatus() == 1 && activity.getEnrollEndTime().isBefore(LocalDateTime.now())){
            activity.setStatus((byte) 2);
            log.info("activity:{} status turn 1:enroll to 2:sign in",activityId);
            activityService.updateById(activity);
        }else if (activity.getStatus() == 2 && activity.getActivityEndTime().isBefore(LocalDateTime.now())){
            activity.setStatus((byte) 3);
            log.info("activity:{} status turn 2:sign in to 3:activity end",activityId);
            activityService.updateById(activity);
            //活动结束后给分
            activityService.allGiveScore(activityId);
            try {
                context.getScheduler().shutdown();
            } catch (SchedulerException e) {
                log.info("time scheduler shutdown activityId:{}",activityId);
            }
        }

    }
}
