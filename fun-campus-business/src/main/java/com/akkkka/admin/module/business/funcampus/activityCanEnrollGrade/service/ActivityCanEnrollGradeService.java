package com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.dao.ActivityCanEnrollGradeDao;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeAddForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.vo.ActivityCanEnrollGradeVO;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.manager.ActivityCanEnrollGradeManager;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 活动能报名的年级 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:42:17
 * @Copyright akkkka114514
 */

@Slf4j
@Service
public class ActivityCanEnrollGradeService {

    @Resource
    private ActivityCanEnrollGradeDao activityCanEnrollGradeDao;
    @Resource
    private ActivityCanEnrollGradeManager canEnrollGradeManager;
    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 分页查询
     */
    public PageResult<ActivityCanEnrollGradeVO> queryPage(ActivityCanEnrollGradeQueryForm queryForm) {
        log.info("ActivityCanEnrollGradeService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityCanEnrollGradeVO> list = activityCanEnrollGradeDao.queryPage(page, queryForm);
        log.info("ActivityCanEnrollGradeService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityCanEnrollGradeAddForm addForm) {
        log.info("ActivityCanEnrollGradeService.add called, addForm={}", addForm);
        ActivityCanEnrollGradeEntity activityCanEnrollGradeEntity = SmartBeanUtil.copy(addForm, ActivityCanEnrollGradeEntity.class);
        int result = activityCanEnrollGradeDao.insert(activityCanEnrollGradeEntity);
        if (result > 0) {
            log.info("ActivityCanEnrollGradeService.add success: new record created with id={}", activityCanEnrollGradeEntity.getId());
        } else {
            log.error("ActivityCanEnrollGradeService.add failed: no record created");
        }
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityCanEnrollGradeUpdateForm updateForm) {
        log.info("ActivityCanEnrollGradeService.update called, updateForm={}", updateForm);
        ActivityCanEnrollGradeEntity activityCanEnrollGradeEntity = SmartBeanUtil.copy(updateForm, ActivityCanEnrollGradeEntity.class);
        int result = activityCanEnrollGradeDao.updateById(activityCanEnrollGradeEntity);
        if (result > 0) {
            log.info("ActivityCanEnrollGradeService.update success: record updated with id={}", updateForm.getId());
        } else {
            log.warn("ActivityCanEnrollGradeService.update warning: no records updated");
        }
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        log.info("ActivityCanEnrollGradeService.batchDelete called, idList size={}", CollectionUtils.isNotEmpty(idList) ? idList.size() : 0);
        if (CollectionUtils.isEmpty(idList)){
            log.debug("ActivityCanEnrollGradeService.batchDelete: idList is empty");
            return ResponseDTO.ok();
        }

        int result = activityCanEnrollGradeDao.batchUpdateDeleted(idList, true);
        log.info("ActivityCanEnrollGradeService.batchDelete result: {} records marked as deleted", result);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        log.info("ActivityCanEnrollGradeService.delete called, id={}", id);
        if (null == id){
            log.warn("ActivityCanEnrollGradeService.delete: id is null");
            return ResponseDTO.ok();
        }

        int result = activityCanEnrollGradeDao.updateDeleted(id, true);
        if (result > 0) {
            log.info("ActivityCanEnrollGradeService.delete success: record with id={} marked as deleted", id);
        } else {
            log.warn("ActivityCanEnrollGradeService.delete warning: no records deleted");
        }
        return ResponseDTO.ok();
    }

    public List<Long> getGradeIdByActivityId(Long activityId){
        LambdaQueryWrapper<ActivityCanEnrollGradeEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ActivityCanEnrollGradeEntity::getActivityId,activityId)
            .eq(ActivityCanEnrollGradeEntity::getDeletedFlag,false)
            .select(ActivityCanEnrollGradeEntity::getCanEnrollGrade);
        List<ActivityCanEnrollGradeEntity> list = canEnrollGradeManager.list(qw);
        if(list==null || list.isEmpty()){
            return null;
        }
        return list
                .stream()
                .map(ActivityCanEnrollGradeEntity::getCanEnrollGrade)
                .toList();
    }

    public void doSaveBatchTransaction(List<ActivityCanEnrollGradeEntity> list){
        transactionTemplate.executeWithoutResult(status -> {
            if(!canEnrollGradeManager.saveBatch(list)){
                log.warn("事务失败：插入能报名的年级失败：List<ActivityCanEnrollGradeEntity>={}",list);
                status.setRollbackOnly();
            }
        });
    }
}
