package com.akkkka.admin.module.business.funcampus.tribe.service;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.tribe.domain.entity.TribeEntity;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeAddForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeUpdateForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.SimpleTribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.manager.TribeManager;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.ValidateList;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.util.SmartRequestUtil;
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
