package com.akkkka.admin.module.business.funcampus.activityComment.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.akkkka.admin.module.business.funcampus.activityComment.dao.ActivityCommentHotDao;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.entity.ActivityCommentHotEntity;

import org.springframework.stereotype.Service;

/**
 * 活动评论热度 Manager
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */
@Service
public class ActivityCommentHotManager extends ServiceImpl<ActivityCommentHotDao, ActivityCommentHotEntity> {

    public LambdaQueryWrapper<ActivityCommentHotEntity> qwByCommentId(Long commentId) {
        return Wrappers.lambdaQuery(ActivityCommentHotEntity.class)
                .eq(ActivityCommentHotEntity::getCommentId, commentId);
    }

}
