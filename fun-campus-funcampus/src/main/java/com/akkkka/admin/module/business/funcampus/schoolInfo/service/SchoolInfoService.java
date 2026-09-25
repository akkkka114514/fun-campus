package com.akkkka.admin.module.business.funcampus.schoolInfo.service;

import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.schoolInfo.dao.SchoolInfoDao;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.entity.SchoolInfoEntity;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoAddForm;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoQueryForm;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoUpdateForm;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.vo.SchoolInfoVO;
import com.akkkka.admin.module.business.funcampus.schoolInfo.manager.SchoolInfoManager;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
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
 * 学校信息表 Service
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class SchoolInfoService {
    private final SchoolInfoManager schoolInfoManager;

    public String getNameById(Long id){
        SchoolInfoEntity entity = schoolInfoManager.getById(id);

        AssertUtil.ifTrueThrowParamError(Objects.isNull(entity));

        return entity.getName();
    }

    public PageResult<SchoolInfoVO> queryPage(SchoolInfoQueryForm queryForm) {
        Page<SchoolInfoVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<SchoolInfoVO> list = schoolInfoManager.getBaseMapper().queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    public ResponseDTO<String> add(SchoolInfoAddForm addForm) {
        SchoolInfoEntity entity = SmartBeanUtil.copy(addForm, SchoolInfoEntity.class);
        entity.setDeletedFlag(false);
        schoolInfoManager.save(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> update(SchoolInfoUpdateForm updateForm) {
        SchoolInfoEntity entity = SmartBeanUtil.copy(updateForm, SchoolInfoEntity.class);
        schoolInfoManager.updateById(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> batchDelete(ValidateList<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        schoolInfoManager.getBaseMapper().batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> delete(Long id) {
        if (id == null) {
            return ResponseDTO.ok();
        }
        schoolInfoManager.getBaseMapper().updateDeleted(id, true);
        return ResponseDTO.ok();
    }

}