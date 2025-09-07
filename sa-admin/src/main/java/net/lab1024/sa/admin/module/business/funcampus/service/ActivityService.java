package net.lab1024.sa.admin.module.business.funcampus.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.dao.ActivityDao;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityAddForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.vo.ActivityVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 活动管理 Service
 *
 * @Author akkkka114514
 * @Date 2025-09-04 13:41:42
 * @Copyright akkkka114514
 */

@Service
public class ActivityService {

    @Resource
    private ActivityDao activityDao;

    /**
     * 分页查询
     */
    public PageResult<ActivityVO> queryPage(ActivityQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityVO> list = activityDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityAddForm addForm) {
        ActivityEntity activityEntity = SmartBeanUtil.copy(addForm, ActivityEntity.class);
        activityDao.insert(activityEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityUpdateForm updateForm) {
        ActivityEntity activityEntity = SmartBeanUtil.copy(updateForm, ActivityEntity.class);
        activityDao.updateById(activityEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        activityDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        activityDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
