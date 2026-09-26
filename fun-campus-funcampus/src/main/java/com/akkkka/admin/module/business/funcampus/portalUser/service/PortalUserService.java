package com.akkkka.admin.module.business.funcampus.portalUser.service;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.portalUser.dao.PortalUserDao;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.form.PortalUserAddForm;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.form.PortalUserQueryForm;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.form.PortalUserProfileUpdateForm;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.form.PortalUserUpdateForm;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.CurrentPortalUserVO;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.schoolInfo.service.SchoolInfoService;
import com.akkkka.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
import com.akkkka.admin.module.business.funcampus.gradeInfo.service.GradeInfoService;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.RequestPortalUser;
import com.akkkka.common.code.UnexpectedErrorCode;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ValidateList;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;

/**
 * 前端用户 Service
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class PortalUserService {
    private final PortalUserManager portalUserManager;
    private final SchoolInfoService schoolInfoService;
    private final CollegeInfoService collegeInfoService;
    private final GradeInfoService gradeInfoService;

    public PageResult<PortalUserVO> queryPage(PortalUserQueryForm queryForm) {
        Page<PortalUserVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<PortalUserVO> list = portalUserManager.getBaseMapper().queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    public ResponseDTO<String> add(PortalUserAddForm addForm) {
        PortalUserEntity entity = SmartBeanUtil.copy(addForm, PortalUserEntity.class);
        entity.setDeletedFlag(false);
        portalUserManager.save(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> update(PortalUserUpdateForm updateForm) {
        PortalUserEntity entity = SmartBeanUtil.copy(updateForm, PortalUserEntity.class);
        portalUserManager.updateById(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> batchDelete(ValidateList<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        portalUserManager.getBaseMapper().batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> delete(Long id) {
        if (id == null) {
            return ResponseDTO.ok();
        }
        portalUserManager.getBaseMapper().updateDeleted(id, true);
        return ResponseDTO.ok();
    }

    /**
     * 查询当前登录门户用户的个人信息
     */
    public CurrentPortalUserVO getCurrentUserInfo() {
        Long userId = getCurrentPortalUserId();
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        if (portalUser == null || portalUser.getDeletedFlag()) {
            throw new BusinessException(UnexpectedErrorCode.BUSINESS_HANDING, "用户不存在");
        }

        CurrentPortalUserVO vo = new CurrentPortalUserVO();
        vo.setId(portalUser.getId());
        vo.setUsername(portalUser.getUsername());
        vo.setAvatar(portalUser.getAvatar());
        vo.setGender(portalUser.getGender());
        vo.setPhone(portalUser.getPhone());
        vo.setSchoolId(portalUser.getSchoolId());
        vo.setCollegeId(portalUser.getCollegeId());
        vo.setGradeId(portalUser.getGradeId());
        vo.setOrganizationId(portalUser.getOrganizationId());
        vo.setGradeScore(portalUser.getGradeScore());
        vo.setCreditScore(portalUser.getCreditScore());
        vo.setCanPublishActivity(portalUser.getCanPublishActivity());

        // 组装名称（id 为空时跳过）
        if (portalUser.getSchoolId() != null) {
            vo.setSchoolName(schoolInfoService.getNameById(portalUser.getSchoolId()));
        }
        if (portalUser.getCollegeId() != null) {
            vo.setCollegeName(collegeInfoService.getNameById(portalUser.getCollegeId()));
        }
        if (portalUser.getGradeId() != null) {
            vo.setGradeName(gradeInfoService.getNameById(portalUser.getGradeId()));
        }
        return vo;
    }

    /**
     * 更新当前登录门户用户的个人资料（仅允许修改头像/手机号/性别）
     */
    public void updateProfile(PortalUserProfileUpdateForm updateForm) {
        Long userId = getCurrentPortalUserId();
        if (updateForm.getAvatar() == null && updateForm.getPhone() == null && updateForm.getGender() == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "请至少填写一项要修改的资料");
        }

        LambdaUpdateWrapper<PortalUserEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PortalUserEntity::getId, userId)
                .set(updateForm.getAvatar() != null, PortalUserEntity::getAvatar, updateForm.getAvatar())
                .set(updateForm.getPhone() != null, PortalUserEntity::getPhone, updateForm.getPhone())
                .set(updateForm.getGender() != null, PortalUserEntity::getGender, updateForm.getGender());
        portalUserManager.update(updateWrapper);
    }

    /**
     * 获取当前登录的门户用户id（个人中心接口仅允许门户用户访问）
     */
    private Long getCurrentPortalUserId() {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        if (!(requestUser instanceof RequestPortalUser)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION, "仅前端用户可操作");
        }
        return requestUser.getUserId();
    }

}