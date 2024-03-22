package com.akkkka.funcampusmainapi.api;

import com.akkkka.funcampusmainmodel.entity.Activity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
public interface IActivityService extends IService<Activity> {
    void addActivity(Activity activity);

    void updateActivity(Activity activity);

    void deleteActivity(Integer activityId);

    List<Activity> listByUserId(Integer userId);

    void enroll(Integer userId, Integer activityId);
//    void giveScore(Integer activityId, Integer userId);
    void allGiveScore(Integer activityId);
    Activity getByTitle(String title);
    void changeStatusOnTime(Integer activityId) throws SchedulerException;
    void signIn(Integer activityId, Integer userId);
    IPage<Activity> pageBySchoolId(Integer pageNum, Integer pageSize, Integer schoolId);
    void cancelEnroll(Integer userId, Integer activityId);
}
