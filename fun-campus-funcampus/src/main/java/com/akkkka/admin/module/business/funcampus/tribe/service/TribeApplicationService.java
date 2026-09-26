package com.akkkka.admin.module.business.funcampus.tribe.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.tribe.constant.TribeApplicationStatus;
import com.akkkka.admin.module.business.funcampus.tribe.domain.entity.TribeApplicationEntity;
import com.akkkka.admin.module.business.funcampus.tribe.domain.entity.TribeEntity;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeApplicationApplyForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeApplicationQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeApplicationReviewForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeApplicationVO;
import com.akkkka.admin.module.business.funcampus.tribe.manager.TribeApplicationManager;
import com.akkkka.admin.module.business.funcampus.tribe.manager.TribeManager;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.entity.TribeUserEntity;
import com.akkkka.admin.module.business.funcampus.tribeUser.manager.TribeUserManager;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 部落加入申请 Service
 * <p>
 * - 门户：申请加入（防重复）、我的申请列表；
 * - 管理端：申请分页、审核（CAS 流转 + 通过时写入部落成员）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Slf4j
@Service
@AllArgsConstructor
public class TribeApplicationService {

    private final TribeApplicationManager tribeApplicationManager;
    private final TribeManager tribeManager;
    private final TribeUserManager tribeUserManager;
    private final PortalUserManager portalUserManager;
    private final TransactionTemplate transactionTemplate;

    /**
     * 门户：申请加入部落
     */
    public void apply(TribeApplicationApplyForm applyForm) {
        Long userId = getCurrentPortalUserId();
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if (portalUser == null || portalUser.getDeletedFlag()) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        TribeEntity tribe = tribeManager.getById(applyForm.getTribeId());
        if (tribe == null || Boolean.TRUE.equals(tribe.getDeletedFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "部落不存在");
        }
        // 仅可申请本校部落
        if (tribe.getSchoolId() != null && !Objects.equals(tribe.getSchoolId(), portalUser.getSchoolId())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "仅可申请本校部落");
        }
        // 已加入不允许再申请
        if (existsTribeUser(tribe.getId(), userId)) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "您已加入该部落");
        }
        // 已有待审核申请不允许重复申请
        boolean hasWaiting = tribeApplicationManager.exists(
                Wrappers.lambdaQuery(TribeApplicationEntity.class)
                        .eq(TribeApplicationEntity::getTribeId, tribe.getId())
                        .eq(TribeApplicationEntity::getPortalUserId, userId)
                        .eq(TribeApplicationEntity::getStatus, TribeApplicationStatus.WAIT_REVIEW)
                        .eq(TribeApplicationEntity::getDeletedFlag, false));
        if (hasWaiting) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "已有待审核的申请，请勿重复申请");
        }

        TribeApplicationEntity entity = new TribeApplicationEntity();
        entity.setTribeId(tribe.getId());
        entity.setPortalUserId(userId);
        entity.setUsername(portalUser.getUsername());
        entity.setReason(applyForm.getReason());
        entity.setStatus(TribeApplicationStatus.WAIT_REVIEW);
        entity.setDeletedFlag(false);
        tribeApplicationManager.save(entity);
        log.info("TribeApplicationService.apply success: tribeId={}, userId={}", tribe.getId(), userId);
    }

    /**
     * 门户：我的申请列表
     */
    public List<TribeApplicationVO> queryMyList() {
        Long userId = getCurrentPortalUserId();
        return tribeApplicationManager.getBaseMapper().queryMyList(userId);
    }

    /**
     * 管理端：申请分页查询
     */
    public PageResult<TribeApplicationVO> queryPage(TribeApplicationQueryForm queryForm) {
        Page<TribeApplicationVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<TribeApplicationVO> list = tribeApplicationManager.getBaseMapper().queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 管理端：审核申请
     * <p>
     * CAS 仅待审核可流转（并发下只有一次生效）；通过时同事务写入部落成员（幂等）
     */
    public void review(TribeApplicationReviewForm reviewForm) {
        Long reviewUserId = SmartRequestUtil.getRequestUserId();
        TribeApplicationEntity application = tribeApplicationManager.getById(reviewForm.getId());
        if (application == null || Boolean.TRUE.equals(application.getDeletedFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "申请不存在");
        }
        if (application.getStatus() != TribeApplicationStatus.WAIT_REVIEW) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "该申请已审核，请勿重复操作");
        }
        TribeApplicationStatus targetStatus = Boolean.TRUE.equals(reviewForm.getApproved())
                ? TribeApplicationStatus.APPROVED : TribeApplicationStatus.REJECTED;

        transactionTemplate.executeWithoutResult(status -> {
            boolean cas = tribeApplicationManager.update(
                    Wrappers.lambdaUpdate(TribeApplicationEntity.class)
                            .eq(TribeApplicationEntity::getId, application.getId())
                            .eq(TribeApplicationEntity::getStatus, TribeApplicationStatus.WAIT_REVIEW)
                            .set(TribeApplicationEntity::getStatus, targetStatus)
                            .set(TribeApplicationEntity::getReviewRemark, reviewForm.getReviewRemark())
                            .set(TribeApplicationEntity::getReviewUserId, reviewUserId)
                            .set(TribeApplicationEntity::getReviewTime, LocalDateTime.now()));
            if (!cas) {
                status.setRollbackOnly();
                throw new BusinessException(UserErrorCode.SERVICE_BUSY, "申请状态已变更，请刷新后重试");
            }
            if (targetStatus == TribeApplicationStatus.APPROVED) {
                saveTribeUserIfAbsent(application);
            }
        });
        log.info("TribeApplicationService.review success: id={}, status={}, reviewUserId={}",
                application.getId(), targetStatus.getCode(), reviewUserId);
    }

    /**
     * 审核通过：写入部落成员（已存在有效成员记录时跳过，保证幂等）
     */
    private void saveTribeUserIfAbsent(TribeApplicationEntity application) {
        boolean exists = tribeUserManager.exists(
                Wrappers.lambdaQuery(TribeUserEntity.class)
                        .eq(TribeUserEntity::getTribeId, application.getTribeId())
                        .eq(TribeUserEntity::getPortalUserId, application.getPortalUserId())
                        .eq(TribeUserEntity::getDeletedFlag, false));
        if (exists) {
            return;
        }
        TribeUserEntity tribeUser = new TribeUserEntity();
        tribeUser.setTribeId(application.getTribeId());
        tribeUser.setPortalUserId(application.getPortalUserId());
        tribeUser.setUsername(application.getUsername());
        tribeUser.setDeletedFlag(false);
        tribeUserManager.save(tribeUser);
    }

    /**
     * 用户是否已是该部落的有效成员
     */
    private boolean existsTribeUser(Long tribeId, Long userId) {
        return tribeUserManager.exists(
                Wrappers.lambdaQuery(TribeUserEntity.class)
                        .eq(TribeUserEntity::getTribeId, tribeId)
                        .eq(TribeUserEntity::getPortalUserId, userId)
                        .eq(TribeUserEntity::getDeletedFlag, false));
    }

    /**
     * 获取当前登录的门户用户id（申请相关接口仅允许门户用户访问）
     */
    private Long getCurrentPortalUserId() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (!(requestUser instanceof RequestPortalUser)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION, "仅前端用户可操作");
        }
        return requestUser.getUserId();
    }

}
