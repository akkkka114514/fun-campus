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
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 活动能报名的部落 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:18:01
 * @Copyright akkkka114514
 */

@Service
public class ActivityCanEnrollTribeService {

    @Resource
    private ActivityCanEnrollTribeDao activityCanEnrollTribeDao;

    /**
     * 分页查询
     */
    public PageResult<ActivityCanEnrollTribeVO> queryPage(ActivityCanEnrollTribeQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityCanEnrollTribeVO> list = activityCanEnrollTribeDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityCanEnrollTribeAddForm addForm) {
        ActivityCanEnrollTribeEntity activityCanEnrollTribeEntity = SmartBeanUtil.copy(addForm, ActivityCanEnrollTribeEntity.class);
        activityCanEnrollTribeDao.insert(activityCanEnrollTribeEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityCanEnrollTribeUpdateForm updateForm) {
        ActivityCanEnrollTribeEntity activityCanEnrollTribeEntity = SmartBeanUtil.copy(updateForm, ActivityCanEnrollTribeEntity.class);
        activityCanEnrollTribeDao.updateById(activityCanEnrollTribeEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        activityCanEnrollTribeDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        activityCanEnrollTribeDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
