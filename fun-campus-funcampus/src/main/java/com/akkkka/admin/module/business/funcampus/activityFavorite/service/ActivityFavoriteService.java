package com.akkkka.admin.module.business.funcampus.activityFavorite.service;

import com.akkkka.admin.module.business.funcampus.activityFavorite.domain.entity.ActivityFavoriteEntity;
import com.akkkka.admin.module.business.funcampus.activityFavorite.manager.ActivityFavoriteManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartRequestUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动收藏 Service
 *
 * @Author akkkka114514
 * @Date 2026-08-16
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivityFavoriteService {

    private final ActivityFavoriteManager activityFavoriteManager;
    private final ActivityValidator activityValidator;

    /**
     * 收藏活动（幂等：已收藏则忽略）
     */
    public void addFavorite(Long activityId) {
        Long userId = getCurrentPortalUserId();
        activityValidator.validateActivityId(activityId);

        // 幂等：已存在收藏记录则直接返回
        long count = activityFavoriteManager.count(
                activityFavoriteManager.qwByActivityIdAndUserId(activityId, userId));
        if (count > 0) {
            log.info("ActivityFavoriteService.addFavorite: already favorited, activityId={}, userId={}", activityId, userId);
            return;
        }

        ActivityFavoriteEntity entity = new ActivityFavoriteEntity();
        entity.setActivityId(activityId);
        entity.setUserId(userId);
        entity.setDeletedFlag(false);
        activityFavoriteManager.save(entity);
        log.info("ActivityFavoriteService.addFavorite success: activityId={}, userId={}", activityId, userId);
    }

    /**
     * 取消收藏活动（幂等：未收藏则忽略）
     */
    public void removeFavorite(Long activityId) {
        Long userId = getCurrentPortalUserId();
        activityValidator.validateActivityId(activityId);

        ActivityFavoriteEntity existing = activityFavoriteManager.getOne(
                activityFavoriteManager.qwByActivityIdAndUserId(activityId, userId));
        if (existing == null) {
            log.info("ActivityFavoriteService.removeFavorite: not favorited, activityId={}, userId={}", activityId, userId);
            return;
        }

        existing.setDeletedFlag(true);
        activityFavoriteManager.updateById(existing);
        log.info("ActivityFavoriteService.removeFavorite success: activityId={}, userId={}", activityId, userId);
    }

    /**
     * 查询当前用户是否已收藏该活动
     */
    public boolean isFavorited(Long activityId) {
        Long userId = getCurrentPortalUserId();
        return activityFavoriteManager.count(
                activityFavoriteManager.qwByActivityIdAndUserId(activityId, userId)) > 0;
    }

    /**
     * 查询当前用户收藏的活动id列表
     */
    public List<Long> listFavoriteActivityIds() {
        Long userId = getCurrentPortalUserId();
        return activityFavoriteManager.list(
                activityFavoriteManager.qwByUserId(userId)
                        .select(ActivityFavoriteEntity::getActivityId)
        ).stream().map(ActivityFavoriteEntity::getActivityId).toList();
    }

    private Long getCurrentPortalUserId() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (!(requestUser instanceof RequestPortalUser)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION, "仅前端用户可操作收藏");
        }
        return requestUser.getUserId();
    }
}
