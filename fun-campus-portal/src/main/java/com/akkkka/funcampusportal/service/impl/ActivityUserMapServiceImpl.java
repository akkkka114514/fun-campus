package com.akkkka.funcampusportal.service.impl;

import com.akkkka.funcampusportal.domain.ActivityUserMap;
import com.akkkka.funcampusportal.mapper.ActivityUserMapMapper;
import com.akkkka.funcampusportal.service.IActivityUserMapService;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
@Service
@Slf4j
public class ActivityUserMapServiceImpl extends ServiceImpl<ActivityUserMapMapper, ActivityUserMap> implements IActivityUserMapService {
    @Override
    public boolean checkUnique(Integer userId, Integer activityId) {
        log.info("check activity user map if unique :user id={},activity id={}",userId,activityId);
        ActivityUserMap activityUserMap = this.get(userId, activityId);
        if(activityUserMap==null){
            return true;
        }
        log.info("select duplicated from db:{}",activityUserMap);
        return false;
    }

    @Override
    public ActivityUserMap get(Integer userId, Integer activityId) {
        return this.getOne(
                new QueryWrapper<ActivityUserMap>()
                        .eq("activity_id",activityId).eq("user_id",userId)
        );
    }

    @Override
    public void update(ActivityUserMap aum) {
        ExceptionUtil.throwIfNullInDb(this.get(aum.getUserId(),aum.getActivityId()),":activityUserMap");

        this.update(aum,
                new UpdateWrapper<ActivityUserMap>()
                        .eq("activity_id",aum.getActivityId())
                        .eq("user_id",aum.getUserId())
        );
    }
}
