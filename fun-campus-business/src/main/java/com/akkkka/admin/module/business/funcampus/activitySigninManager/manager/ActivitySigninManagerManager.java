package com.akkkka.admin.module.business.funcampus.activitySigninManager.manager;

import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.dao.ActivitySigninManagerDao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 活动签到管理员  Manager
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:10:19
 * @Copyright akkkka114514
 */
@Service
public class ActivitySigninManagerManager extends ServiceImpl<ActivitySigninManagerDao, ActivitySigninManagerEntity> {

    public LambdaQueryWrapper<ActivitySigninManagerEntity> qwByActivityId(Long activityId) {
        return Wrappers.lambdaQuery(ActivitySigninManagerEntity.class)
                .eq(ActivitySigninManagerEntity::getActivityId, activityId)
                .eq(ActivitySigninManagerEntity::getDeletedFlag, false);
    }

}
