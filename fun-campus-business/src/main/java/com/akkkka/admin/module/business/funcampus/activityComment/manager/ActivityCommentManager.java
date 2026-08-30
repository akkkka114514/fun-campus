package com.akkkka.admin.module.business.funcampus.activityComment.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.akkkka.admin.module.business.funcampus.activityComment.dao.ActivityCommentDao;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.entity.ActivityCommentEntity;

import org.springframework.stereotype.Service;

/**
 * 活动评论 Manager
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */
@Service
public class ActivityCommentManager extends ServiceImpl<ActivityCommentDao, ActivityCommentEntity> {

    public LambdaQueryWrapper<ActivityCommentEntity> qwByActivityId(Long activityId) {
        return Wrappers.lambdaQuery(ActivityCommentEntity.class)
                .eq(ActivityCommentEntity::getActivityId, activityId)
                .eq(ActivityCommentEntity::getDeleted, false);
    }

    public LambdaQueryWrapper<ActivityCommentEntity> qwByActivityIdAndRootIsNull(Long activityId) {
        return Wrappers.lambdaQuery(ActivityCommentEntity.class)
                .eq(ActivityCommentEntity::getActivityId, activityId)
                .isNull(ActivityCommentEntity::getRootId)
                .eq(ActivityCommentEntity::getDeleted, false);
    }

    public LambdaQueryWrapper<ActivityCommentEntity> qwByRootId(Long rootId) {
        return Wrappers.lambdaQuery(ActivityCommentEntity.class)
                .eq(ActivityCommentEntity::getRootId, rootId)
                .eq(ActivityCommentEntity::getDeleted, false)
                .orderByAsc(ActivityCommentEntity::getCreateTime);
    }

    public LambdaQueryWrapper<ActivityCommentEntity> qwByIdAndUserId(Long id, Long userId) {
        return Wrappers.lambdaQuery(ActivityCommentEntity.class)
                .eq(ActivityCommentEntity::getId, id)
                .eq(ActivityCommentEntity::getUserId, userId)
                .eq(ActivityCommentEntity::getDeleted, false);
    }

}
