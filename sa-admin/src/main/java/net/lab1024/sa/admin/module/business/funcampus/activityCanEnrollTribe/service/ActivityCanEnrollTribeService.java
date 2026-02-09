package net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.dao.ActivityCanEnrollTribeDao;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.entity.ActivityCanEnrollTribeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.vo.ActivityCanEnrollTribeVO;
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
 * 活动能报名的部落 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:18:01
 * @Copyright akkkka114514
 */

@Slf4j
@Service
public class ActivityCanEnrollTribeService {

    @Resource
    private ActivityCanEnrollTribeDao activityCanEnrollTribeDao;

    /**
     * 分页查询
     */
    public PageResult<ActivityCanEnrollTribeVO> queryPage(ActivityCanEnrollTribeQueryForm queryForm) {
        log.info("ActivityCanEnrollTribeService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityCanEnrollTribeVO> list = activityCanEnrollTribeDao.queryPage(page, queryForm);
        log.info("ActivityCanEnrollTribeService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityCanEnrollTribeAddForm addForm) {
        log.info("ActivityCanEnrollTribeService.add called, addForm={}", addForm);
        ActivityCanEnrollTribeEntity activityCanEnrollTribeEntity = SmartBeanUtil.copy(addForm, ActivityCanEnrollTribeEntity.class);
        int result = activityCanEnrollTribeDao.insert(activityCanEnrollTribeEntity);
        if (result > 0) {
            log.info("ActivityCanEnrollTribeService.add success: new record created with id={}", activityCanEnrollTribeEntity.getId());
        } else {
            log.error("ActivityCanEnrollTribeService.add failed: no record created");
        }
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityCanEnrollTribeUpdateForm updateForm) {
        log.info("ActivityCanEnrollTribeService.update called, updateForm={}", updateForm);
        ActivityCanEnrollTribeEntity activityCanEnrollTribeEntity = SmartBeanUtil.copy(updateForm, ActivityCanEnrollTribeEntity.class);
        int result = activityCanEnrollTribeDao.updateById(activityCanEnrollTribeEntity);
        if (result > 0) {
            log.info("ActivityCanEnrollTribeService.update success: record updated with id={}", updateForm.getId());
        } else {
            log.warn("ActivityCanEnrollTribeService.update warning: no records updated");
        }
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        log.info("ActivityCanEnrollTribeService.batchDelete called, idList size={}", CollectionUtils.isNotEmpty(idList) ? idList.size() : 0);
        if (CollectionUtils.isEmpty(idList)){
            log.debug("ActivityCanEnrollTribeService.batchDelete: idList is empty");
            return ResponseDTO.ok();
        }

        int result = activityCanEnrollTribeDao.batchUpdateDeleted(idList, true);
        log.info("ActivityCanEnrollTribeService.batchDelete result: {} records marked as deleted", result);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        log.info("ActivityCanEnrollTribeService.delete called, id={}", id);
        if (null == id){
            log.warn("ActivityCanEnrollTribeService.delete: id is null");
            return ResponseDTO.ok();
        }

        int result = activityCanEnrollTribeDao.updateDeleted(id, true);
        if (result > 0) {
            log.info("ActivityCanEnrollTribeService.delete success: record with id={} marked as deleted", id);
        } else {
            log.warn("ActivityCanEnrollTribeService.delete warning: no records deleted");
        }
        return ResponseDTO.ok();
    }
}
