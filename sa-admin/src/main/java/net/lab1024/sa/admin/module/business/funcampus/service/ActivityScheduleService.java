package net.lab1024.sa.admin.module.business.funcampus.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.dao.ActivityScheduleDao;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityScheduleAddForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityScheduleQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityScheduleUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.vo.ActivityScheduleVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 活动时间表 Service
 *
 * @Author akkkka114514
 * @Date 2025-09-06 15:54:07
 * @Copyright akkkka114514
 */

@Service
public class ActivityScheduleService {

    @Resource
    private ActivityScheduleDao activityScheduleDao;

    /**
     * 分页查询
     */
    public PageResult<ActivityScheduleVO> queryPage(ActivityScheduleQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityScheduleVO> list = activityScheduleDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityScheduleAddForm addForm) {
        ActivityScheduleEntity activityScheduleEntity = SmartBeanUtil.copy(addForm, ActivityScheduleEntity.class);
        activityScheduleDao.insert(activityScheduleEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityScheduleUpdateForm updateForm) {
        ActivityScheduleEntity activityScheduleEntity = SmartBeanUtil.copy(updateForm, ActivityScheduleEntity.class);
        activityScheduleDao.updateById(activityScheduleEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        activityScheduleDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long activityId) {
        if (null == activityId){
            return ResponseDTO.ok();
        }

        activityScheduleDao.updateDeleted(activityId, true);
        return ResponseDTO.ok();
    }
}
