package com.akkkka.admin.module.business.funcampus.organizationInfo.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.organizationInfo.dao.OrganizationInfoDao;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.entity.OrganizationInfoEntity;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoAddForm;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoQueryForm;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoUpdateForm;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.vo.OrganizationInfoVO;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.vo.SimpleOrganizationInfoVO;
import com.akkkka.admin.module.business.funcampus.organizationInfo.manager.OrganizationInfoManager;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import com.akkkka.common.domain.IdNameVO;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.ValidateList;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akkkka.common.util.SmartRequestUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 各学校组织信息 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:24:05
 * @Copyright akkkka114514
 */

@Slf4j
@Service
@AllArgsConstructor
public class OrganizationInfoService {
    private final OrganizationInfoManager organizationInfoManager;

    public String getNameById(Long id){
        OrganizationInfoEntity entity = organizationInfoManager.getById(id);
        AssertUtil.ifTrueThrowParamError(Objects.isNull(entity));
        return entity.getName();
    }

    public List<IdNameVO> getIdNameBySchoolId(Long schoolId){
        List<OrganizationInfoEntity> list = organizationInfoManager.list(
                Wrappers.lambdaQuery(OrganizationInfoEntity.class)
                        .eq(OrganizationInfoEntity::getSchoolId,schoolId)
                        .eq(OrganizationInfoEntity::getDeletedFlag,false)
                        .select(OrganizationInfoEntity::getId)
                        .select(OrganizationInfoEntity::getName)
        );
        List<IdNameVO> result = new LinkedList<>();
        if(list.isEmpty()){
            return result;
        }
        for(OrganizationInfoEntity e:list){
            IdNameVO vo = new IdNameVO();
            vo.setId(e.getId());
            vo.setName(e.getName());
            result.add(vo);
        }
        return result;
    }
    public List<Long> getIdsBySchoolId(Long schoolId){
        return organizationInfoManager.list(
                Wrappers.lambdaQuery(OrganizationInfoEntity.class)
                        .eq(OrganizationInfoEntity::getSchoolId,schoolId)
                        .eq(OrganizationInfoEntity::getDeletedFlag,false)
                        .select(OrganizationInfoEntity::getId)
        ).stream().map(OrganizationInfoEntity::getId).toList();
    }

    public PageResult<OrganizationInfoVO> queryPage(OrganizationInfoQueryForm queryForm) {
        Page<OrganizationInfoVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<OrganizationInfoVO> list = organizationInfoManager.getBaseMapper().queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    public ResponseDTO<String> add(OrganizationInfoAddForm addForm) {
        OrganizationInfoEntity entity = SmartBeanUtil.copy(addForm, OrganizationInfoEntity.class);
        entity.setDeletedFlag(false);
        organizationInfoManager.save(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> update(OrganizationInfoUpdateForm updateForm) {
        OrganizationInfoEntity entity = SmartBeanUtil.copy(updateForm, OrganizationInfoEntity.class);
        organizationInfoManager.updateById(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> batchDelete(ValidateList<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        organizationInfoManager.getBaseMapper().batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> delete(Long id) {
        if (id == null) {
            return ResponseDTO.ok();
        }
        organizationInfoManager.getBaseMapper().updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
