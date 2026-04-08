package com.akkkka.admin.module.business.funcampus.activityCategory.service;

import java.util.List;
import com.akkkka.admin.module.business.funcampus.activityCategory.dao.ActivityCategoryDao;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.entity.ActivityCategoryEntity;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryAddForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.vo.ActivityCategoryVO;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.vo.SimpleActivityCategoryVO;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class ActivityCategoryService {

    @Resource
    private ActivityCategoryDao activityCategoryDao;

    /**
     * 分页查询
     */
    public PageResult<ActivityCategoryVO> queryPage(ActivityCategoryQueryForm queryForm) {
        log.info("ActivityCategoryService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityCategoryVO> list = activityCategoryDao.queryPage(page, queryForm);
        log.info("ActivityCategoryService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityCategoryAddForm addForm) {
        log.info("ActivityCategoryService.add called, addForm={}", addForm);
        ActivityCategoryEntity activityCategoryEntity = SmartBeanUtil.copy(addForm, ActivityCategoryEntity.class);
        int result = activityCategoryDao.insert(activityCategoryEntity);
        if (result > 0) {
            log.info("ActivityCategoryService.add success: new record created with id={}", activityCategoryEntity.getId());
        } else {
            log.error("ActivityCategoryService.add failed: no record created");
        }
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityCategoryUpdateForm updateForm) {
        log.info("ActivityCategoryService.update called, updateForm={}", updateForm);
        ActivityCategoryEntity activityCategoryEntity = SmartBeanUtil.copy(updateForm, ActivityCategoryEntity.class);
        int result = activityCategoryDao.updateById(activityCategoryEntity);
        if (result > 0) {
            log.info("ActivityCategoryService.update success: record updated with id={}", updateForm.getId());
        } else {
            log.warn("ActivityCategoryService.update warning: no records updated");
        }
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        log.info("ActivityCategoryService.batchDelete called, idList size={}", CollectionUtils.isNotEmpty(idList) ? idList.size() : 0);
        if (CollectionUtils.isEmpty(idList)){
            log.debug("ActivityCategoryService.batchDelete: idList is empty");
            return ResponseDTO.ok();
        }

        int result = activityCategoryDao.batchUpdateDeleted(idList, true);
        log.info("ActivityCategoryService.batchDelete result: {} records marked as deleted", result);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        log.info("ActivityCategoryService.delete called, id={}", id);
        if (null == id){
            log.warn("ActivityCategoryService.delete: id is null");
            return ResponseDTO.ok();
        }

        int result = activityCategoryDao.updateDeleted(id, true);
        if (result > 0) {
            log.info("ActivityCategoryService.delete success: record with id={} marked as deleted", id);
        } else {
            log.warn("ActivityCategoryService.delete warning: no records deleted");
        }
        return ResponseDTO.ok();
    }

    public List<SimpleActivityCategoryVO> getAll() {
        log.debug("ActivityCategoryService.getAll called");
        List<SimpleActivityCategoryVO> result = activityCategoryDao.getAll();
        log.info("ActivityCategoryService.getAll result: count={}", result.size());
        return result;
    }
}
