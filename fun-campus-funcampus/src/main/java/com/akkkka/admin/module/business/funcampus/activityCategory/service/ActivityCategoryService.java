package com.akkkka.admin.module.business.funcampus.activityCategory.service;

import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.activityCategory.dao.ActivityCategoryDao;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.entity.ActivityCategoryEntity;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryAddForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.vo.ActivityCategoryVO;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.vo.SimpleActivityCategoryVO;
import com.akkkka.admin.module.business.funcampus.activityCategory.manager.ActivityCategoryManager;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.IdNameVO;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ValidateList;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 活动分类 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-10 19:46:44
 * @Copyright akkkka114514
 */

@Slf4j
@Service
@AllArgsConstructor
public class ActivityCategoryService {
    private final ActivityCategoryManager categoryManager;

    public String getNameById(Long id){
        ActivityCategoryEntity entity = categoryManager.getById(id);
        AssertUtil.ifTrueThrowParamError(Objects.isNull(entity));
        return entity.getName();
    }

    public List<IdNameVO> getAll() {
        return categoryManager.list().stream().map(entity->{
            IdNameVO vo = new IdNameVO();
            vo.setId(entity.getId());
            vo.setName(entity.getName());
            return vo;
        }).toList();
    }

    /**
     * 分页查询
     */
    public PageResult<ActivityCategoryVO> queryPage(ActivityCategoryQueryForm queryForm) {
        Page<ActivityCategoryVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<ActivityCategoryVO> list = categoryManager.getBaseMapper().queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityCategoryAddForm addForm) {
        ActivityCategoryEntity entity = SmartBeanUtil.copy(addForm, ActivityCategoryEntity.class);
        entity.setId(null);
        entity.setDeletedFlag(false);
        categoryManager.save(entity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     */
    public ResponseDTO<String> update(ActivityCategoryUpdateForm updateForm) {
        ActivityCategoryEntity entity = SmartBeanUtil.copy(updateForm, ActivityCategoryEntity.class);
        categoryManager.updateById(entity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(ValidateList<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        categoryManager.getBaseMapper().batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (id == null) {
            return ResponseDTO.ok();
        }
        categoryManager.getBaseMapper().updateDeleted(id, true);
        return ResponseDTO.ok();
    }

}
