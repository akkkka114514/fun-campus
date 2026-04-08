package com.akkkka.admin.module.business.funcampus.organizationInfo.service;

import java.util.List;
import com.akkkka.admin.module.business.funcampus.organizationInfo.dao.OrganizationInfoDao;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.entity.OrganizationInfoEntity;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoAddForm;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoQueryForm;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoUpdateForm;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.vo.OrganizationInfoVO;
import com.akkkka.admin.module.business.funcampus.organizationInfo.domain.vo.SimpleOrganizationInfoVO;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akkkka.common.util.SmartRequestUtil;
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
public class OrganizationInfoService {

    @Resource
    private OrganizationInfoDao organizationInfoDao;

    /**
     * 分页查询
     */
    public PageResult<OrganizationInfoVO> queryPage(OrganizationInfoQueryForm queryForm) {
        log.info("OrganizationInfoService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<OrganizationInfoVO> list = organizationInfoDao.queryPage(page, queryForm);
        log.info("OrganizationInfoService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(OrganizationInfoAddForm addForm) {
        log.info("OrganizationInfoService.add called, addForm={}", addForm);
        OrganizationInfoEntity organizationInfoEntity = SmartBeanUtil.copy(addForm, OrganizationInfoEntity.class);
        int result = organizationInfoDao.insert(organizationInfoEntity);
        if (result > 0) {
            log.info("OrganizationInfoService.add success: new record created with id={}", organizationInfoEntity.getId());
        } else {
            log.error("OrganizationInfoService.add failed: no record created");
        }
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(OrganizationInfoUpdateForm updateForm) {
        log.info("OrganizationInfoService.update called, updateForm={}", updateForm);
        OrganizationInfoEntity organizationInfoEntity = SmartBeanUtil.copy(updateForm, OrganizationInfoEntity.class);
        int result = organizationInfoDao.updateById(organizationInfoEntity);
        if (result > 0) {
            log.info("OrganizationInfoService.update success: record updated with id={}", updateForm.getId());
        } else {
            log.warn("OrganizationInfoService.update warning: no records updated");
        }
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        log.info("OrganizationInfoService.batchDelete called, idList size={}", CollectionUtils.isNotEmpty(idList) ? idList.size() : 0);
        if (CollectionUtils.isEmpty(idList)){
            log.debug("OrganizationInfoService.batchDelete: idList is empty");
            return ResponseDTO.ok();
        }

        int result = organizationInfoDao.batchUpdateDeleted(idList, true);
        log.info("OrganizationInfoService.batchDelete result: {} records marked as deleted", result);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        log.info("OrganizationInfoService.delete called, id={}", id);
        if (null == id){
            log.warn("OrganizationInfoService.delete: id is null");
            return ResponseDTO.ok();
        }

        int result = organizationInfoDao.updateDeleted(id, true);
        if (result > 0) {
            log.info("OrganizationInfoService.delete success: record with id={} marked as deleted", id);
        } else {
            log.warn("OrganizationInfoService.delete warning: no records deleted");
        }
        return ResponseDTO.ok();
    }

    public List<SimpleOrganizationInfoVO> getOrganizationInfoByUserId() {
        Long userId = SmartRequestUtil.getRequestUserId();
        log.info("OrganizationInfoService.getOrganizationInfoByUserId called, userId={}", userId);
        List<SimpleOrganizationInfoVO> result = organizationInfoDao.getOrganizationInfoByUserId(userId);
        log.info("OrganizationInfoService.getOrganizationInfoByUserId result: count={}", result.size());
        return result;
    }
}
