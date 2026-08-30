package com.akkkka.admin.module.business.funcampus.activityComment.service;

import com.akkkka.admin.module.business.funcampus.activityComment.dao.ActivityCommentHotDao;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.entity.ActivityCommentEntity;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.form.ActivityCommentAddForm;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.form.ActivityCommentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.vo.ActivityCommentHotVO;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.vo.ActivityCommentVO;
import com.akkkka.admin.module.business.funcampus.activityComment.manager.ActivityCommentHotManager;
import com.akkkka.admin.module.business.funcampus.activityComment.manager.ActivityCommentManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 活动评论 Service
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivityCommentService {

    private final ActivityCommentManager activityCommentManager;
    private final ActivityCommentHotManager activityCommentHotManager;
    private final ActivityCommentHotDao activityCommentHotDao;
    private final ActivityValidator activityValidator;

    /**
     * 发表评论
     */
    public void addComment(ActivityCommentAddForm form) {
        Long userId = getCurrentPortalUserId();
        activityValidator.validateActivityId(form.getActivityId());

        // 如果是回复评论，校验被回复的评论是否存在
        Long rootId = null;
        if (form.getToCommentId() != null) {
            ActivityCommentEntity parentComment = activityCommentManager.getById(form.getToCommentId());
            if (parentComment == null || parentComment.getDeleted()) {
                throw new BusinessException(UserErrorCode.PARAM_ERROR, "被回复的评论不存在");
            }
            // 确定根评论id：如果父评论本身就是根评论，rootId就是父评论id；否则继承父评论的rootId
            rootId = parentComment.getRootId() == null ? parentComment.getId() : parentComment.getRootId();
        }

        ActivityCommentEntity entity = new ActivityCommentEntity();
        entity.setUserId(userId);
        entity.setActivityId(form.getActivityId());
        entity.setToCommentId(form.getToCommentId());
        entity.setContent(form.getContent());
        entity.setRootId(rootId);
        entity.setDeleted(false);
        activityCommentManager.save(entity);

        log.info("ActivityCommentService.addComment success: commentId={}, activityId={}, userId={}",
                entity.getId(), form.getActivityId(), userId);
    }

    /**
     * 删除评论（仅评论作者可删除，内容替换为"内容已删除"并逻辑删除，不影响子评论）
     */
    public void deleteComment(Long commentId) {
        Long userId = getCurrentPortalUserId();

        ActivityCommentEntity comment = activityCommentManager.getOne(
                activityCommentManager.qwByIdAndUserId(commentId, userId));
        if (comment == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "评论不存在或无权删除");
        }

        comment.setContent("内容已删除");
        comment.setDeleted(true);
        activityCommentManager.updateById(comment);

        log.info("ActivityCommentService.deleteComment success: commentId={}, userId={}", commentId, userId);
    }

    /**
     * 点赞评论
     */
    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Long commentId) {
        // 校验评论是否存在
        ActivityCommentEntity comment = activityCommentManager.getById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "评论不存在");
        }

        // 更新热度（INSERT ON DUPLICATE KEY UPDATE）
        activityCommentHotDao.likeComment(commentId);

        log.info("ActivityCommentService.likeComment success: commentId={}", commentId);
    }

    /**
     * 撤销点赞评论
     */
    @Transactional(rollbackFor = Exception.class)
    public void unlikeComment(Long commentId) {
        // 校验评论是否存在
        ActivityCommentEntity comment = activityCommentManager.getById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "评论不存在");
        }

        // 撤销点赞（热度-1，归零时删除记录）
        activityCommentHotDao.unlikeComment(commentId);

        log.info("ActivityCommentService.unlikeComment success: commentId={}", commentId);
    }

    /**
     * 热门评论排行（按热度降序）
     */
    public PageResult<ActivityCommentHotVO> queryHotComments(Long activityId, Page<?> page) {
        activityValidator.validateActivityId(activityId);
        List<ActivityCommentHotVO> list = activityCommentHotDao.queryHotCommentsByActivityId(page, activityId);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 评论分页查询（仅查询根评论，附带子评论列表）
     */
    public PageResult<ActivityCommentVO> queryComments(ActivityCommentQueryForm queryForm) {
        activityValidator.validateActivityId(queryForm.getActivityId());

        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityCommentVO> rootComments = activityCommentManager.getBaseMapper()
                .queryPageByActivityId(page, queryForm.getActivityId());

        // 为每个根评论加载子评论
        for (ActivityCommentVO rootComment : rootComments) {
            List<ActivityCommentVO> children = activityCommentManager.getBaseMapper()
                    .queryByRootId(rootComment.getId());
            rootComment.setChildren(children);
        }

        return SmartPageUtil.convert2PageResult(page, rootComments);
    }

    private Long getCurrentPortalUserId() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (!(requestUser instanceof RequestPortalUser)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION, "仅前端用户可操作评论");
        }
        return requestUser.getUserId();
    }
}
