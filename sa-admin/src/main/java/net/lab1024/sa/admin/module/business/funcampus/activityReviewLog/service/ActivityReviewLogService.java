package net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.dao.ActivityReviewLogDao;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.form.ActivityReviewLogUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.vo.ActivityReviewLogVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 活动审核日志 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-10 18:34:56
 * @Copyright akkkka114514
 */

@Service
public class ActivityReviewLogService {

    @Resource
    private ActivityReviewLogDao activityReviewLogDao;

    /**
     * 分页查询
     */
    public PageResult<ActivityReviewLogVO> queryPage(ActivityReviewLogQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityReviewLogVO> list = activityReviewLogDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityReviewLogAddForm addForm) {
        ActivityReviewLogEntity activityReviewLogEntity = SmartBeanUtil.copy(addForm, ActivityReviewLogEntity.class);
        activityReviewLogDao.insert(activityReviewLogEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityReviewLogUpdateForm updateForm) {
        ActivityReviewLogEntity activityReviewLogEntity = SmartBeanUtil.copy(updateForm, ActivityReviewLogEntity.class);
        activityReviewLogDao.updateById(activityReviewLogEntity);
        return ResponseDTO.ok();
    }

}
