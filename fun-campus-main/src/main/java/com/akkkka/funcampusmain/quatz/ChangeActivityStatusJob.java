package com.akkkka.funcampusmain.quatz;

import com.akkkka.funcampusmainmodel.entity.Activity;
import com.akkkka.funcampusmainapi.api.IActivityService;
import com.akkkka.funcampusutil.util.ExceptionUtil;

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

        ExceptionUtil.throwIfNullInDb(activity);
        ExceptionUtil.throwIfIsDeletedInDb(activity.getIsDeleted());

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
