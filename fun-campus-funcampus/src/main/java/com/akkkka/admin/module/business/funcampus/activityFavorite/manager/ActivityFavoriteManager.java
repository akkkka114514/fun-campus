package com.akkkka.admin.module.business.funcampus.activityFavorite.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.akkkka.admin.module.business.funcampus.activityFavorite.dao.ActivityFavoriteDao;
import com.akkkka.admin.module.business.funcampus.activityFavorite.domain.entity.ActivityFavoriteEntity;

import org.springframework.stereotype.Service;

/**
 * 活动收藏 Manager
 *
 * @Author akkkka114514
 * @Date 2026-08-16
 * @Copyright akkkka114514
 */
@Service
public class ActivityFavoriteManager extends ServiceImpl<ActivityFavoriteDao, ActivityFavoriteEntity> {

    public LambdaQueryWrapper<ActivityFavoriteEntity> qwByActivityIdAndUserId(Long activityId, Long userId) {
        return Wrappers.lambdaQuery(ActivityFavoriteEntity.class)
                .eq(ActivityFavoriteEntity::getActivityId, activityId)
                .eq(ActivityFavoriteEntity::getUserId, userId)
                .eq(ActivityFavoriteEntity::getDeletedFlag, false);
    }

    /**
     * 按活动查询收藏记录（未删除），供状态变更通知取收藏用户列表
     */
    public LambdaQueryWrapper<ActivityFavoriteEntity> qwByActivityId(Long activityId) {
        return Wrappers.lambdaQuery(ActivityFavoriteEntity.class)
                .eq(ActivityFavoriteEntity::getActivityId, activityId)
                .eq(ActivityFavoriteEntity::getDeletedFlag, false);
    }

    public LambdaQueryWrapper<ActivityFavoriteEntity> qwByUserId(Long userId) {
        return Wrappers.lambdaQuery(ActivityFavoriteEntity.class)
                .eq(ActivityFavoriteEntity::getUserId, userId)
                .eq(ActivityFavoriteEntity::getDeletedFlag, false);
    }

}
