package com.akkkka.funcampusportal.service;

import com.akkkka.funcampusportal.domain.Activity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author: Zoe Wu
 * @create: 2025-03-07 21:40
 * @Description:
 */
public interface IActivityService extends IService<Activity> {

    void add(Activity activity);

    void update(Activity activity);

    void delete(Integer activityId);

    List<Activity> listByUserId(Integer userId);

    void enroll(Integer userId, Integer activityId);

    void changeStatusOnTime(Integer activityId) throws Exception;

    void signIn(Integer activityId, Integer userId);

    void cancelEnroll(Integer userId, Integer activityId);

    void allGiveScore(Integer activityId);

    Activity getByTitle(String title);

    IPage<Activity> pageBySchoolId(Integer pageNum, Integer pageSize, Integer schoolId);
}
