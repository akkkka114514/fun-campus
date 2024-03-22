package com.akkkka.funcampusmainapi.api;

import com.akkkka.funcampusmainmodel.entity.ActivityUserMap;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
public interface IActivityUserMapService extends IService<ActivityUserMap> {
    boolean checkUnique(Integer userId, Integer activityId);

    ActivityUserMap get(Integer userId, Integer activityId);

    void update(ActivityUserMap activityUserMap);

}
