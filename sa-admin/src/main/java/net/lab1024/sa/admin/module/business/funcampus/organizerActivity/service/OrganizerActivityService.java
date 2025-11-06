package net.lab1024.sa.admin.module.business.funcampus.organizerActivity.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.dao.OrganizerActivityDao;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.entity.OrganizerActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityAddForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.vo.OrganizerActivityVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;

/**
 * 运营者发布活动的对应关系 Service
 *
 * @Author akkkka114514
 * @Date 2025-09-19 14:08:56
 * @Copyright akkkka114514
 */
@Slf4j
@Service
public class OrganizerActivityService {

    @Resource
    private OrganizerActivityDao organizerActivityDao;

    /**
     * 分页查询
     */
    public PageResult<OrganizerActivityVO> queryPage(OrganizerActivityQueryForm queryForm) {
        log.info("OrganizerActivityService.queryPage called, queryForm={}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<OrganizerActivityVO> list = organizerActivityDao.queryPage(page, queryForm);
        log.info("OrganizerActivityService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(OrganizerActivityAddForm addForm) {
        log.info("OrganizerActivityService.add called, organizerId={}, activityId={}", addForm.getOrganizerId(), addForm.getActivityId());
        OrganizerActivityEntity organizerActivityEntity = SmartBeanUtil.copy(addForm, OrganizerActivityEntity.class);
        int result = organizerActivityDao.insert(organizerActivityEntity);
        if (result > 0) {
            log.info("OrganizerActivityService.add success: organizerActivity created, id={}", organizerActivityEntity.getId());
        } else {
            log.error("OrganizerActivityService.add failed: failed to insert record");
        }
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(OrganizerActivityUpdateForm updateForm) {
        log.info("OrganizerActivityService.update called, id={}", updateForm.getId());
        OrganizerActivityEntity organizerActivityEntity = SmartBeanUtil.copy(updateForm, OrganizerActivityEntity.class);
        int result = organizerActivityDao.updateById(organizerActivityEntity);
        if (result > 0) {
            log.info("OrganizerActivityService.update success: organizerActivity updated, id={}", updateForm.getId());
        } else {
            log.error("OrganizerActivityService.update failed: failed to update record, id={}", updateForm.getId());
        }
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        log.info("OrganizerActivityService.batchDelete called, idListSize={}", idList != null ? idList.size() : 0);
        if (CollectionUtils.isEmpty(idList)){
            log.info("OrganizerActivityService.batchDelete skipped: empty idList");
            return ResponseDTO.ok();
        }

        int result = organizerActivityDao.batchUpdateDeleted(idList, true);
        log.info("OrganizerActivityService.batchDelete result: updated {} records", result);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        log.info("OrganizerActivityService.delete called, id={}", id);
        if (null == id){
            log.info("OrganizerActivityService.delete skipped: null id");
            return ResponseDTO.ok();
        }

        int result = organizerActivityDao.updateDeleted(id, true);
        log.info("OrganizerActivityService.delete result: updated {} records", result);
        return ResponseDTO.ok();
    }
}