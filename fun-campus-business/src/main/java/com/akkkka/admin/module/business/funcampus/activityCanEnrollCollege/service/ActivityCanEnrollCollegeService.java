package com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.service;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.dao.ActivityCanEnrollCollegeDao;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.entity.ActivityCanEnrollCollegeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeAddForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.vo.ActivityCanEnrollCollegeVO;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.manager.ActivityCanEnrollCollegeManager;
import com.akkkka.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
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
 * 活动能报名的学院 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-01 20:24:18
 * @Copyright akkkka114514
 */

@Slf4j
@Service
public class ActivityCanEnrollCollegeService {

    @Resource
    private ActivityCanEnrollCollegeDao activityCanEnrollCollegeDao;
    @Resource
    private ActivityCanEnrollCollegeManager canEnrollCollegeManager;
    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 分页查询
     */
    public PageResult<ActivityCanEnrollCollegeVO> queryPage(ActivityCanEnrollCollegeQueryForm queryForm) {
        log.info("ActivityCanEnrollCollegeService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityCanEnrollCollegeVO> list = activityCanEnrollCollegeDao.queryPage(page, queryForm);
        log.info("ActivityCanEnrollCollegeService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityCanEnrollCollegeAddForm addForm) {
        log.info("ActivityCanEnrollCollegeService.add called, addForm={}", addForm);
        ActivityCanEnrollCollegeEntity activityCanEnrollCollegeEntity = SmartBeanUtil.copy(addForm, ActivityCanEnrollCollegeEntity.class);
        int result = activityCanEnrollCollegeDao.insert(activityCanEnrollCollegeEntity);
        if (result > 0) {
            log.info("ActivityCanEnrollCollegeService.add success: new record created with id={}", activityCanEnrollCollegeEntity.getId());
        } else {
            log.error("ActivityCanEnrollCollegeService.add failed: no record created");
        }
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityCanEnrollCollegeUpdateForm updateForm) {
        log.info("ActivityCanEnrollCollegeService.update called, updateForm={}", updateForm);
        ActivityCanEnrollCollegeEntity activityCanEnrollCollegeEntity = SmartBeanUtil.copy(updateForm, ActivityCanEnrollCollegeEntity.class);
        int result = activityCanEnrollCollegeDao.updateById(activityCanEnrollCollegeEntity);
        if (result > 0) {
            log.info("ActivityCanEnrollCollegeService.update success: record updated with id={}", updateForm.getId());
        } else {
            log.warn("ActivityCanEnrollCollegeService.update warning: no records updated");
        }
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        log.info("ActivityCanEnrollCollegeService.batchDelete called, idList size={}", CollectionUtils.isNotEmpty(idList) ? idList.size() : 0);
        if (CollectionUtils.isEmpty(idList)){
            log.debug("ActivityCanEnrollCollegeService.batchDelete: idList is empty");
            return ResponseDTO.ok();
        }

        int result = activityCanEnrollCollegeDao.batchUpdateDeleted(idList, true);
        log.info("ActivityCanEnrollCollegeService.batchDelete result: {} records marked as deleted", result);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        log.info("ActivityCanEnrollCollegeService.delete called, id={}", id);
        if (null == id){
            log.warn("ActivityCanEnrollCollegeService.delete: id is null");
            return ResponseDTO.ok();
        }

        int result = activityCanEnrollCollegeDao.updateDeleted(id, true);
        if (result > 0) {
            log.info("ActivityCanEnrollCollegeService.delete success: record with id={} marked as deleted", id);
        } else {
            log.warn("ActivityCanEnrollCollegeService.delete warning: no records deleted");
        }
        return ResponseDTO.ok();
    }
    //取出所有canEnrollCollege表的collegeId，然后获取所有college的name
    public List<Long> getCollegeIdsByActivityId(Long activityId){
        LambdaQueryWrapper<ActivityCanEnrollCollegeEntity> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(ActivityCanEnrollCollegeEntity::getActivityId,activityId)
                .eq(ActivityCanEnrollCollegeEntity::getDeletedFlag,false)
                .select(ActivityCanEnrollCollegeEntity::getCanEnrollCollege);
        List<ActivityCanEnrollCollegeEntity> collegeEntities = canEnrollCollegeManager.list(lqw1);
        List<Long> collegeIds = null;
        if(collegeEntities!=null&&!collegeEntities.isEmpty()){
            collegeIds = collegeEntities
                    .stream()
                    .map(ActivityCanEnrollCollegeEntity::getCanEnrollCollege)
                    .toList();
        }
        return collegeIds;
    }
    public void doSaveBatchTransaction(List<ActivityCanEnrollCollegeEntity> list){
        transactionTemplate.executeWithoutResult(status -> {
            if(!canEnrollCollegeManager.saveBatch(list)){
                log.warn("事务失败：插入能报名的学院失败：List<ActivityCanEnrollCollegeEntity>={}",list);
                status.setRollbackOnly();
            }
        });
    }
}
