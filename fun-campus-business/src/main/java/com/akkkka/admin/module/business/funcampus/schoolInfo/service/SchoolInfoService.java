package com.akkkka.admin.module.business.funcampus.schoolInfo.service;

import java.util.List;
import com.akkkka.admin.module.business.funcampus.schoolInfo.dao.SchoolInfoDao;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.entity.SchoolInfoEntity;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoAddForm;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoQueryForm;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoUpdateForm;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.vo.SchoolInfoVO;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;

/**
 * 学校信息表 Service
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */
@Slf4j
@Service
public class SchoolInfoService {

    @Resource
    private SchoolInfoDao schoolInfoDao;

    /**
     * 分页查询
     */
    public PageResult<SchoolInfoVO> queryPage(SchoolInfoQueryForm queryForm) {
        log.info("SchoolInfoService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SchoolInfoVO> list = schoolInfoDao.queryPage(page, queryForm);
        log.info("SchoolInfoService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(SchoolInfoAddForm addForm) {
        log.info("SchoolInfoService.add called, schoolName={}", addForm.getName());
        SchoolInfoEntity schoolInfoEntity = SmartBeanUtil.copy(addForm, SchoolInfoEntity.class);
        int result = schoolInfoDao.insert(schoolInfoEntity);
        if (result > 0) {
            log.info("SchoolInfoService.add success: school created, id={}", schoolInfoEntity.getId());
        } else {
            log.error("SchoolInfoService.add failed: failed to insert record");
        }
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(SchoolInfoUpdateForm updateForm) {
        log.info("SchoolInfoService.update called, id={}", updateForm.getId());
        SchoolInfoEntity schoolInfoEntity = SmartBeanUtil.copy(updateForm, SchoolInfoEntity.class);
        int result = schoolInfoDao.updateById(schoolInfoEntity);
        if (result > 0) {
            log.info("SchoolInfoService.update success: school updated, id={}", updateForm.getId());
        } else {
            log.error("SchoolInfoService.update failed: failed to update record, id={}", updateForm.getId());
        }
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        log.info("SchoolInfoService.batchDelete called, idListSize={}", idList != null ? idList.size() : 0);
        if (CollectionUtils.isEmpty(idList)){
            log.info("SchoolInfoService.batchDelete skipped: empty idList");
            return ResponseDTO.ok();
        }

        int result = schoolInfoDao.batchUpdateDeleted(idList, true);
        log.info("SchoolInfoService.batchDelete result: updated {} records", result);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        log.info("SchoolInfoService.delete called, id={}", id);
        if (null == id){
            log.info("SchoolInfoService.delete skipped: null id");
            return ResponseDTO.ok();
        }

        Long result = schoolInfoDao.updateDeleted(id, true);
        log.info("SchoolInfoService.delete result: updated id = {}", result);
        return ResponseDTO.ok();
    }
}