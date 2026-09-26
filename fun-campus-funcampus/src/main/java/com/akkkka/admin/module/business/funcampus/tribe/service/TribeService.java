package com.akkkka.admin.module.business.funcampus.tribe.service;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.tribe.domain.entity.TribeApplicationEntity;
import com.akkkka.admin.module.business.funcampus.tribe.domain.entity.TribeEntity;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeActivityQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeAddForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeMemberQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribePortalQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeUpdateForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.SimpleTribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeActivityVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeDetailVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeMemberVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribePortalVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.manager.TribeApplicationManager;
import com.akkkka.admin.module.business.funcampus.tribe.manager.TribeManager;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.entity.TribeUserEntity;
import com.akkkka.admin.module.business.funcampus.tribeUser.manager.TribeUserManager;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.ValidateList;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 部落 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@Service
@Slf4j
@AllArgsConstructor
public class TribeService {
    private final TribeManager tribeManager;
    private final PortalUserManager portalUserManager;
    private final TribeUserManager tribeUserManager;
    private final TribeApplicationManager tribeApplicationManager;

    public String getNameById(Long id){
        TribeEntity entity = tribeManager.getById(id);
        AssertUtil.ifTrueThrowParamError(Objects.isNull(entity));
        return entity.getName();
    }



    public List<SimpleTribeVO> querySimpleList(String keyword) {
        Long userId = SmartRequestUtil.getRequestUserId();
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        Long schoolId = portalUser.getSchoolId();
        return tribeManager.getBaseMapper().querySimpleList(schoolId, keyword);
    }

    /**
     * 管理端：按学校查询部落简要列表（schoolId 必传，keyword 可空模糊匹配）
     */
    public List<SimpleTribeVO> querySimpleListBySchool(Long schoolId, String keyword) {
        if (schoolId == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "学校id不能为空");
        }
        return tribeManager.getBaseMapper().querySimpleList(schoolId, keyword);
    }

    /**
     * 门户：本校部落分页（带成员数、主席名、我是否已加入）
     */
    public PageResult<TribePortalVO> queryPortalPage(TribePortalQueryForm queryForm) {
        Long userId = SmartRequestUtil.getRequestUserId();
        PortalUserEntity portalUser = portalUserManager.getById(userId);
        Long schoolId = portalUser == null ? null : portalUser.getSchoolId();
        Page<TribePortalVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<TribePortalVO> list = tribeManager.getBaseMapper().queryPortalPage(page, schoolId, userId, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 门户：部落详情（含成员数、我是否已加入、最近一次申请状态）
     */
    public TribeDetailVO detail(Long tribeId) {
        TribeEntity tribe = getExistingTribe(tribeId);
        Long userId = SmartRequestUtil.getRequestUserId();

        TribeDetailVO vo = new TribeDetailVO();
        vo.setId(tribe.getId());
        vo.setName(tribe.getName());
        vo.setIcon(tribe.getIcon());
        vo.setDescription(tribe.getDescription());
        vo.setCategoryId(tribe.getCategoryId());
        vo.setPresidentId(tribe.getPresidentId());
        vo.setBelongTo(tribe.getBelongTo());
        vo.setSchoolId(tribe.getSchoolId());

        // 主席名
        if (tribe.getPresidentId() != null) {
            PortalUserEntity president = portalUserManager.getById(tribe.getPresidentId());
            if (president != null) {
                vo.setPresidentName(president.getUsername());
            }
        }
        // 成员数（热度）
        vo.setMemberNum(tribeUserManager.count(
                Wrappers.lambdaQuery(TribeUserEntity.class)
                        .eq(TribeUserEntity::getTribeId, tribeId)
                        .eq(TribeUserEntity::getDeletedFlag, false)));
        // 我是否已加入
        vo.setJoinedFlag(tribeUserManager.exists(
                Wrappers.lambdaQuery(TribeUserEntity.class)
                        .eq(TribeUserEntity::getTribeId, tribeId)
                        .eq(TribeUserEntity::getPortalUserId, userId)
                        .eq(TribeUserEntity::getDeletedFlag, false)));
        // 我最近一次申请状态
        TribeApplicationEntity latestApplication = tribeApplicationManager.getOne(
                Wrappers.lambdaQuery(TribeApplicationEntity.class)
                        .eq(TribeApplicationEntity::getTribeId, tribeId)
                        .eq(TribeApplicationEntity::getPortalUserId, userId)
                        .eq(TribeApplicationEntity::getDeletedFlag, false)
                        .orderByDesc(TribeApplicationEntity::getId)
                        .last("limit 1"));
        vo.setMyApplicationStatus(latestApplication == null || latestApplication.getStatus() == null
                ? null : latestApplication.getStatus().getCode());
        return vo;
    }

    /**
     * 门户：部落成员分页
     */
    public PageResult<TribeMemberVO> queryMemberPage(TribeMemberQueryForm queryForm) {
        getExistingTribe(queryForm.getTribeId());
        Page<TribeMemberVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<TribeMemberVO> list = tribeUserManager.getBaseMapper().queryMemberPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 门户：部落发起的活动分页（通过活动可报名部落关联）
     */
    public PageResult<TribeActivityVO> queryActivityPage(TribeActivityQueryForm queryForm) {
        getExistingTribe(queryForm.getTribeId());
        Page<TribeActivityVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<TribeActivityVO> list = tribeManager.getBaseMapper().queryTribeActivityPage(page, queryForm.getTribeId());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 校验部落存在且未删除
     */
    private TribeEntity getExistingTribe(Long tribeId) {
        TribeEntity tribe = tribeManager.getById(tribeId);
        if (tribe == null || Boolean.TRUE.equals(tribe.getDeletedFlag())) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "部落不存在");
        }
        return tribe;
    }

    public PageResult<TribeVO> queryPage(TribeQueryForm queryForm) {
        Page<TribeVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<TribeVO> list = tribeManager.getBaseMapper().queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    public ResponseDTO<String> add(TribeAddForm addForm) {
        TribeEntity entity = SmartBeanUtil.copy(addForm, TribeEntity.class);
        entity.setDeletedFlag(false);
        tribeManager.save(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> update(TribeUpdateForm updateForm) {
        TribeEntity entity = SmartBeanUtil.copy(updateForm, TribeEntity.class);
        tribeManager.updateById(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> batchDelete(ValidateList<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        tribeManager.getBaseMapper().batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> delete(Long id) {
        if (id == null) {
            return ResponseDTO.ok();
        }
        tribeManager.getBaseMapper().updateDeleted(id, true);
        return ResponseDTO.ok();
    }

}
