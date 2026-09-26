package com.akkkka.admin.module.business.funcampus.activityEvaluation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.manager.ActivityEnrollmentManager;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.entity.ActivityEvaluationEntity;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.form.ActivityEvaluationQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.form.ActivityEvaluationSubmitForm;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.form.MyEvaluationQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.vo.ActivityEvaluationVO;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.vo.PendingEvaluationVO;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.manager.ActivityEvaluationManager;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 活动评价 Service
 * <p>
 * 提交资格：已报名 + 已签到 + 活动已结束 + 未评价（一人一活动仅一条评价）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Slf4j
@Service
@AllArgsConstructor
public class ActivityEvaluationService {

    private final ActivityEvaluationManager activityEvaluationManager;
    private final ActivityEnrollmentManager activityEnrollmentManager;
    private final PortalUserManager portalUserManager;
    private final ActivityValidator activityValidator;

    /**
     * 门户：提交活动评价
     */
    public void submit(ActivityEvaluationSubmitForm submitForm) {
        Long userId = getCurrentPortalUserId();
        ActivityEntity activity = activityValidator.validateActivityId(submitForm.getActivityId());
        if (activity.getStatus() != ActivityStatus.FINISHED) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动结束后才能评价");
        }
        ActivityEnrollmentEntity enrollment = activityEnrollmentManager.getOne(
                activityEnrollmentManager.qwByActivityId(submitForm.getActivityId())
                        .eq(ActivityEnrollmentEntity::getUserId, userId));
        if (enrollment == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "仅活动参与者可评价");
        }
        if (!Boolean.TRUE.equals(enrollment.getSignInStatus())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "完成签到后才可评价");
        }
        boolean evaluated = activityEvaluationManager.exists(
                Wrappers.lambdaQuery(ActivityEvaluationEntity.class)
                        .eq(ActivityEvaluationEntity::getActivityId, submitForm.getActivityId())
                        .eq(ActivityEvaluationEntity::getUserId, userId)
                        .eq(ActivityEvaluationEntity::getDeletedFlag, false));
        if (evaluated) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "您已评价过该活动");
        }

        PortalUserEntity portalUser = portalUserManager.getById(userId);
        ActivityEvaluationEntity entity = new ActivityEvaluationEntity();
        entity.setActivityId(submitForm.getActivityId());
        entity.setUserId(userId);
        entity.setUsername(portalUser == null ? null : portalUser.getUsername());
        entity.setScore(submitForm.getScore());
        entity.setContent(submitForm.getContent());
        entity.setDeletedFlag(false);
        activityEvaluationManager.save(entity);
        log.info("ActivityEvaluationService.submit success: activityId={}, userId={}, score={}",
                submitForm.getActivityId(), userId, submitForm.getScore());
    }

    /**
     * 门户：某活动的评价分页列表（活动详情页）
     */
    public PageResult<ActivityEvaluationVO> queryByActivity(ActivityEvaluationQueryForm queryForm) {
        activityValidator.validateActivityId(queryForm.getActivityId());
        Page<ActivityEvaluationVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<ActivityEvaluationVO> list = activityEvaluationManager.getBaseMapper()
                .queryByActivity(page, queryForm.getActivityId());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 门户：我的评价分页列表
     */
    public PageResult<ActivityEvaluationVO> queryMy(MyEvaluationQueryForm queryForm) {
        Long userId = getCurrentPortalUserId();
        Page<ActivityEvaluationVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<ActivityEvaluationVO> list = activityEvaluationManager.getBaseMapper().queryMyPage(page, userId);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 门户：我对某活动的评价（未评价返回 null）
     */
    public ActivityEvaluationVO myEvaluation(Long activityId) {
        Long userId = getCurrentPortalUserId();
        ActivityEvaluationEntity entity = activityEvaluationManager.getOne(
                Wrappers.lambdaQuery(ActivityEvaluationEntity.class)
                        .eq(ActivityEvaluationEntity::getActivityId, activityId)
                        .eq(ActivityEvaluationEntity::getUserId, userId)
                        .eq(ActivityEvaluationEntity::getDeletedFlag, false)
                        .last("limit 1"));
        if (entity == null) {
            return null;
        }
        return SmartBeanUtil.copy(entity, ActivityEvaluationVO.class);
    }

    /**
     * 门户：我的待评价活动列表
     */
    public List<PendingEvaluationVO> queryPendingList() {
        Long userId = getCurrentPortalUserId();
        return activityEvaluationManager.getBaseMapper().queryPendingList(userId);
    }

    /**
     * 门户：我的待评价数量（我的页面统计卡片）
     */
    public Long countPending() {
        Long userId = getCurrentPortalUserId();
        Long count = activityEvaluationManager.getBaseMapper().countPending(userId);
        return count == null ? 0L : count;
    }

    /**
     * 获取当前登录的门户用户id（门户接口仅允许前端用户访问）
     */
    private Long getCurrentPortalUserId() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (!(requestUser instanceof RequestPortalUser)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION, "仅前端用户可操作");
        }
        return requestUser.getUserId();
    }

}
