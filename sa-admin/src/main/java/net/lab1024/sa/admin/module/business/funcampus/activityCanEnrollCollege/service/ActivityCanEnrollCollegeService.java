package net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.service;

import java.util.List;

import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.dao.ActivityCanEnrollCollegeDao;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.domain.entity.ActivityCanEnrollCollegeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.domain.vo.ActivityCanEnrollCollegeVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

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
}
