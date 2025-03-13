package com.akkkka.funcampusportal.service;

import com.akkkka.funcampusportal.domain.ActivityUserMap;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: Zoe Wu
 * @create: 2025-03-07 21:52
 * @Description:
 */
public interface IActivityUserMapService extends IService<ActivityUserMap> {
    boolean checkUnique(Integer userId, Integer activityId);
    ActivityUserMap get(Integer userId, Integer activityId);
    void update(ActivityUserMap aum);
}
