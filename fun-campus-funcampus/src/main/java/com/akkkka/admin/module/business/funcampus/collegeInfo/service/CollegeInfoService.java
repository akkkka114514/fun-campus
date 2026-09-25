package com.akkkka.admin.module.business.funcampus.collegeInfo.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoAddForm;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoQueryForm;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.form.CollegeInfoUpdateForm;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.vo.CollegeInfoVO;
import com.akkkka.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import com.akkkka.common.domain.IdNameVO;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ValidateList;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.akkkka.admin.module.business.funcampus.collegeInfo.dao.CollegeInfoDao;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.entity.CollegeInfoEntity;
import com.akkkka.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * 学院信息 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:45:43
 * @Copyright akkkka114514
 */

@Service
@Slf4j
@AllArgsConstructor
public class CollegeInfoService {
    private final CollegeInfoManager collegeInfoManager;


    public String getNameById(Long id){
        CollegeInfoEntity collegeInfo = collegeInfoManager.getById(id);

        AssertUtil.ifTrueThrowParamError(Objects.isNull(collegeInfo));

        return collegeInfo.getName();
    }

    public List<IdNameVO> getIdNameBySchoolId(Long schoolId){
        List<CollegeInfoEntity> list = collegeInfoManager.list(
                Wrappers.lambdaQuery(CollegeInfoEntity.class)
                        .eq(CollegeInfoEntity::getSchoolId,schoolId)
                        .eq(CollegeInfoEntity::getDeletedFlag,false)
                        .select(CollegeInfoEntity::getId)
                        .select(CollegeInfoEntity::getName)
        );
        List<IdNameVO> result = new LinkedList<>();
        if(list.isEmpty()){
            return result;
        }
        for(CollegeInfoEntity e:list){
            IdNameVO vo = new IdNameVO();
            vo.setId(e.getId());
            vo.setName(e.getName());
            result.add(vo);
        }
        return result;
    }

    public List<Long> getIdsBySchoolId(Long schoolId){
        return collegeInfoManager.list(
                Wrappers.lambdaQuery(CollegeInfoEntity.class)
                        .eq(CollegeInfoEntity::getSchoolId,schoolId)
                        .eq(CollegeInfoEntity::getDeletedFlag,false)
                        .select(CollegeInfoEntity::getId)
        ).stream().map(CollegeInfoEntity::getId).toList();
    }

    public PageResult<CollegeInfoVO> queryPage(CollegeInfoQueryForm queryForm) {
        Page<CollegeInfoVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<CollegeInfoVO> list = collegeInfoManager.getBaseMapper().queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    public ResponseDTO<String> add(CollegeInfoAddForm addForm) {
        CollegeInfoEntity entity = SmartBeanUtil.copy(addForm, CollegeInfoEntity.class);
        entity.setDeletedFlag(false);
        collegeInfoManager.save(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> update(CollegeInfoUpdateForm updateForm) {
        CollegeInfoEntity entity = SmartBeanUtil.copy(updateForm, CollegeInfoEntity.class);
        collegeInfoManager.updateById(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> batchDelete(ValidateList<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        collegeInfoManager.getBaseMapper().batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> delete(Long id) {
        if (id == null) {
            return ResponseDTO.ok();
        }
        collegeInfoManager.getBaseMapper().updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
