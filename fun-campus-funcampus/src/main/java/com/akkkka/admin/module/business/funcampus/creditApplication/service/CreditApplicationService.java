package com.akkkka.admin.module.business.funcampus.creditApplication.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.akkkka.admin.module.business.funcampus.creditApplication.constant.CreditApplicationStatus;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.entity.CreditApplicationEntity;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.form.CreditApplicationApplyForm;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.form.CreditApplicationQueryForm;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.form.CreditApplicationReviewForm;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.form.CreditApplicationUpdateForm;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.vo.CreditApplicationVO;
import com.akkkka.admin.module.business.funcampus.creditApplication.domain.vo.CreditReviewerVO;
import com.akkkka.admin.module.business.funcampus.creditApplication.manager.CreditApplicationManager;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.entity.OrganizationInfoEntity;
import com.akkkka.admin.module.business.funcampus.organizationInfo.manager.OrganizationInfoManager;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.manager.BackendUserManager;
import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 学分认定申请 Service
 * <p>
 * - 门户：提交申请（选择审核人）、编辑/删除（仅待审核）、我的申请列表、详情、审核人候选列表；
 * - 管理端：指派给我的申请分页、审核（CAS 仅待审核可流转）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Slf4j
@Service
@AllArgsConstructor
public class CreditApplicationService {

    private final CreditApplicationManager creditApplicationManager;
    private final PortalUserManager portalUserManager;
    private final BackendUserManager backendUserManager;
    private final OrganizationInfoManager organizationInfoManager;

    /**
     * 门户：提交学分认定申请
     */
    public void apply(CreditApplicationApplyForm applyForm) {
        Long userId = getCurrentPortalUserId();
        PortalUserEntity portalUser = getValidPortalUser(userId);

        BackendUserEntity reviewer = validateReviewer(applyForm.getReviewUserId(), portalUser.getSchoolId());

        CreditApplicationEntity entity = new CreditApplicationEntity();
        entity.setTitle(applyForm.getTitle());
        entity.setSemester(applyForm.getSemester());
        entity.setContent(applyForm.getContent());
        entity.setImageList(joinImageList(applyForm.getImageList()));
        entity.setApplicantUserId(userId);
        entity.setApplicantUsername(portalUser.getUsername());
        entity.setApplicantSchoolId(portalUser.getSchoolId());
        fillReviewerSnapshot(entity, reviewer);
        entity.setStatus(CreditApplicationStatus.WAIT_REVIEW);
        entity.setDeletedFlag(false);
        creditApplicationManager.save(entity);
        log.info("CreditApplicationService.apply success: id={}, userId={}, reviewUserId={}",
                entity.getId(), userId, reviewer.getId());
    }

    /**
     * 门户：编辑申请（仅本人 + 待审核；更换审核人时重新校验）
     */
    public void update(CreditApplicationUpdateForm updateForm) {
        Long userId = getCurrentPortalUserId();
        CreditApplicationEntity application = getOwnApplication(updateForm.getId(), userId);
        if (application.getStatus() != CreditApplicationStatus.WAIT_REVIEW) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "仅待审核的申请可编辑");
        }

        // 更换审核人时重新校验
        BackendUserEntity reviewer = null;
        if (!Objects.equals(application.getReviewUserId(), updateForm.getReviewUserId())) {
            reviewer = validateReviewer(updateForm.getReviewUserId(), application.getApplicantSchoolId());
        }

        LambdaUpdateWrapper<CreditApplicationEntity> updateWrapper = Wrappers.lambdaUpdate(CreditApplicationEntity.class)
                .eq(CreditApplicationEntity::getId, application.getId())
                .eq(CreditApplicationEntity::getApplicantUserId, userId)
                .eq(CreditApplicationEntity::getStatus, CreditApplicationStatus.WAIT_REVIEW)
                .eq(CreditApplicationEntity::getDeletedFlag, false)
                .set(CreditApplicationEntity::getTitle, updateForm.getTitle())
                .set(CreditApplicationEntity::getSemester, updateForm.getSemester())
                .set(CreditApplicationEntity::getContent, updateForm.getContent())
                .set(CreditApplicationEntity::getImageList, joinImageList(updateForm.getImageList()));
        if (reviewer != null) {
            updateWrapper
                    .set(CreditApplicationEntity::getReviewUserId, reviewer.getId())
                    .set(CreditApplicationEntity::getReviewUserName, reviewer.getUsername())
                    .set(CreditApplicationEntity::getReviewOrganizationId, reviewer.getOrganizationId())
                    .set(CreditApplicationEntity::getReviewOrganizationName,
                            resolveOrganizationName(reviewer.getOrganizationId()));
        }
        // CAS：仅待审核状态可更新（并发审核时避免覆盖审核结果）
        if (!creditApplicationManager.update(updateWrapper)) {
            throw new BusinessException(UserErrorCode.SERVICE_BUSY, "申请状态已变更，请刷新后重试");
        }
        log.info("CreditApplicationService.update success: id={}, userId={}", application.getId(), userId);
    }

    /**
     * 门户：删除申请（仅本人 + 待审核，逻辑删除）
     */
    public void delete(Long id) {
        Long userId = getCurrentPortalUserId();
        boolean cas = creditApplicationManager.update(
                Wrappers.lambdaUpdate(CreditApplicationEntity.class)
                        .eq(CreditApplicationEntity::getId, id)
                        .eq(CreditApplicationEntity::getApplicantUserId, userId)
                        .eq(CreditApplicationEntity::getStatus, CreditApplicationStatus.WAIT_REVIEW)
                        .eq(CreditApplicationEntity::getDeletedFlag, false)
                        .set(CreditApplicationEntity::getDeletedFlag, true));
        if (!cas) {
            throw new BusinessException(UserErrorCode.SERVICE_BUSY, "仅待审核的申请可删除，请刷新后重试");
        }
        log.info("CreditApplicationService.delete success: id={}, userId={}", id, userId);
    }

    /**
     * 门户：申请详情（仅本人）
     */
    public CreditApplicationVO detail(Long id) {
        Long userId = getCurrentPortalUserId();
        return toVO(getOwnApplication(id, userId));
    }

    /**
     * 门户：我的申请列表（可选状态筛选）
     */
    public List<CreditApplicationVO> queryMyList(Integer status) {
        Long userId = getCurrentPortalUserId();
        CreditApplicationStatus statusEnum = parseStatus(status);
        List<CreditApplicationEntity> list = creditApplicationManager.list(
                Wrappers.lambdaQuery(CreditApplicationEntity.class)
                        .eq(CreditApplicationEntity::getApplicantUserId, userId)
                        .eq(CreditApplicationEntity::getDeletedFlag, false)
                        .eq(statusEnum != null, CreditApplicationEntity::getStatus, statusEnum)
                        .orderByDesc(CreditApplicationEntity::getCreateTime));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 门户：审核人候选列表（本校 + 具审核权限，可按院系/组织筛选）
     */
    public List<CreditReviewerVO> queryReviewerList(Long organizationId) {
        Long userId = getCurrentPortalUserId();
        PortalUserEntity portalUser = getValidPortalUser(userId);

        List<BackendUserEntity> reviewers = backendUserManager.list(
                Wrappers.lambdaQuery(BackendUserEntity.class)
                        .eq(BackendUserEntity::getDeletedFlag, false)
                        .eq(BackendUserEntity::getDisabledFlag, false)
                        .eq(BackendUserEntity::getCanReview, true)
                        .eq(BackendUserEntity::getSchoolId, portalUser.getSchoolId())
                        .eq(organizationId != null, BackendUserEntity::getOrganizationId, organizationId)
                        .orderByAsc(BackendUserEntity::getOrganizationId));

        Map<Long, String> orgNameCache = new HashMap<>();
        List<CreditReviewerVO> result = new ArrayList<>();
        for (BackendUserEntity reviewer : reviewers) {
            CreditReviewerVO vo = new CreditReviewerVO();
            vo.setId(reviewer.getId());
            vo.setUsername(reviewer.getUsername());
            vo.setOrganizationId(reviewer.getOrganizationId());
            vo.setOrganizationName(orgNameCache.computeIfAbsent(
                    reviewer.getOrganizationId() == null ? -1L : reviewer.getOrganizationId(),
                    key -> resolveOrganizationName(reviewer.getOrganizationId())));
            result.add(vo);
        }
        return result;
    }

    /**
     * 管理端：指派给我的申请分页（可选状态/关键词筛选）
     */
    public PageResult<CreditApplicationVO> queryPage(CreditApplicationQueryForm queryForm) {
        Long reviewerId = getCurrentBackendUserId();
        CreditApplicationStatus statusEnum = parseStatus(queryForm.getStatus());
        Page<CreditApplicationEntity> page = creditApplicationManager.page(
                new Page<>(queryForm.getPageNum(), queryForm.getPageSize()),
                Wrappers.lambdaQuery(CreditApplicationEntity.class)
                        .eq(CreditApplicationEntity::getDeletedFlag, false)
                        .eq(CreditApplicationEntity::getReviewUserId, reviewerId)
                        .eq(statusEnum != null, CreditApplicationEntity::getStatus, statusEnum)
                        .and(StringUtils.isNotBlank(queryForm.getKeyword()), w -> w
                                .like(CreditApplicationEntity::getTitle, queryForm.getKeyword())
                                .or()
                                .like(CreditApplicationEntity::getApplicantUsername, queryForm.getKeyword()))
                        .orderByAsc(CreditApplicationEntity::getStatus)
                        .orderByDesc(CreditApplicationEntity::getCreateTime));
        List<CreditApplicationVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return SmartPageUtil.convert2PageResult(page, voList);
    }

    /**
     * 管理端：审核申请（仅被指派的审核人可操作，CAS 仅待审核可流转）
     */
    public void review(CreditApplicationReviewForm reviewForm) {
        Long reviewUserId = getCurrentBackendUserId();
        CreditApplicationEntity application = creditApplicationManager.getById(reviewForm.getId());
        if (application == null || Boolean.TRUE.equals(application.getDeletedFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "申请不存在");
        }
        if (!Objects.equals(application.getReviewUserId(), reviewUserId)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION, "该申请未指派给您审核");
        }
        if (application.getStatus() != CreditApplicationStatus.WAIT_REVIEW) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "该申请已审核，请勿重复操作");
        }
        CreditApplicationStatus targetStatus = Boolean.TRUE.equals(reviewForm.getApproved())
                ? CreditApplicationStatus.APPROVED : CreditApplicationStatus.REJECTED;
        boolean cas = creditApplicationManager.update(
                Wrappers.lambdaUpdate(CreditApplicationEntity.class)
                        .eq(CreditApplicationEntity::getId, application.getId())
                        .eq(CreditApplicationEntity::getStatus, CreditApplicationStatus.WAIT_REVIEW)
                        .set(CreditApplicationEntity::getStatus, targetStatus)
                        .set(CreditApplicationEntity::getReviewRemark, reviewForm.getReviewRemark())
                        .set(CreditApplicationEntity::getReviewTime, LocalDateTime.now()));
        if (!cas) {
            throw new BusinessException(UserErrorCode.SERVICE_BUSY, "申请状态已变更，请刷新后重试");
        }
        log.info("CreditApplicationService.review success: id={}, status={}, reviewUserId={}",
                application.getId(), targetStatus.getCode(), reviewUserId);
    }

    /**
     * 查询本人申请（不存在/已删除/非本人均抛异常）
     */
    private CreditApplicationEntity getOwnApplication(Long id, Long userId) {
        CreditApplicationEntity application = creditApplicationManager.getById(id);
        if (application == null || Boolean.TRUE.equals(application.getDeletedFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "申请不存在");
        }
        if (!Objects.equals(application.getApplicantUserId(), userId)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION, "仅可查看自己的申请");
        }
        return application;
    }

    /**
     * 查询有效的门户用户（不存在/已删除/未绑定学校均抛异常）
     */
    private PortalUserEntity getValidPortalUser(Long userId) {
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if (portalUser == null || Boolean.TRUE.equals(portalUser.getDeletedFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "用户不存在");
        }
        if (portalUser.getSchoolId() == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "当前用户未绑定学校，无法发起学分认定");
        }
        return portalUser;
    }

    /**
     * 校验审核人：存在、未删除未禁用、具审核权限、与申请人同校
     */
    private BackendUserEntity validateReviewer(Long reviewUserId, Long applicantSchoolId) {
        BackendUserEntity reviewer = backendUserManager.getById(reviewUserId);
        if (reviewer == null || Boolean.TRUE.equals(reviewer.getDeletedFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "审核人不存在");
        }
        if (Boolean.TRUE.equals(reviewer.getDisabledFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "审核人已被禁用");
        }
        if (!Boolean.TRUE.equals(reviewer.getCanReview())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "所选用户不具有审核权限");
        }
        if (!Objects.equals(reviewer.getSchoolId(), applicantSchoolId)) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "仅可选择本校审核人");
        }
        return reviewer;
    }

    /**
     * 填充审核人快照（id/姓名/院系组织）
     */
    private void fillReviewerSnapshot(CreditApplicationEntity entity, BackendUserEntity reviewer) {
        entity.setReviewUserId(reviewer.getId());
        entity.setReviewUserName(reviewer.getUsername());
        entity.setReviewOrganizationId(reviewer.getOrganizationId());
        entity.setReviewOrganizationName(resolveOrganizationName(reviewer.getOrganizationId()));
    }

    /**
     * 解析院系/组织名称（不存在返回 null）
     */
    private String resolveOrganizationName(Long organizationId) {
        if (organizationId == null) {
            return null;
        }
        OrganizationInfoEntity organization = organizationInfoManager.getById(organizationId);
        if (organization == null || Boolean.TRUE.equals(organization.getDeletedFlag())) {
            return null;
        }
        return organization.getName();
    }

    /**
     * 状态码解析（null 透传；非法值抛参数错误）
     */
    private CreditApplicationStatus parseStatus(Integer status) {
        if (status == null) {
            return null;
        }
        CreditApplicationStatus statusEnum = CreditApplicationStatus.fromCode(status);
        if (statusEnum == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "状态参数错误");
        }
        return statusEnum;
    }

    /**
     * 图片key列表 -> 逗号分隔存储串（去空去重）
     */
    private String joinImageList(List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            return null;
        }
        String joined = imageList.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .distinct()
                .collect(Collectors.joining(","));
        return joined.isEmpty() ? null : joined;
    }

    /**
     * 逗号分隔存储串 -> 图片key列表
     */
    private List<String> parseImageList(String imageList) {
        if (StringUtils.isBlank(imageList)) {
            return Collections.emptyList();
        }
        return Arrays.stream(imageList.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 实体 -> VO（枚举转 code+名称，图片串转列表）
     */
    private CreditApplicationVO toVO(CreditApplicationEntity application) {
        CreditApplicationVO vo = SmartBeanUtil.copy(application, CreditApplicationVO.class);
        if (vo == null) {
            return null;
        }
        if (application.getStatus() != null) {
            vo.setStatus(application.getStatus().getCode());
            vo.setStatusName(application.getStatus().getLabel());
        }
        vo.setImageList(parseImageList(application.getImageList()));
        return vo;
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

    /**
     * 获取当前登录的管理端用户id（管理端接口仅允许后台用户访问）
     */
    private Long getCurrentBackendUserId() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (!(requestUser instanceof RequestBackendUser)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION, "仅管理端用户可操作");
        }
        return requestUser.getUserId();
    }

}
