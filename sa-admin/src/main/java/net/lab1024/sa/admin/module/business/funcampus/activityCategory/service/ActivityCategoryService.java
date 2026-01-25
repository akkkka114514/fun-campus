package net.lab1024.sa.admin.module.business.funcampus.activityCategory.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.dao.ActivityCategoryDao;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.entity.ActivityCategoryEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.vo.ActivityCategoryVO;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.vo.SimpleActivityCategoryVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 活动分类 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-10 19:46:44
 * @Copyright akkkka114514
 */

@Service
public class ActivityCategoryService {

    @Resource
    private ActivityCategoryDao activityCategoryDao;

    /**
     * 分页查询
     */
    public PageResult<ActivityCategoryVO> queryPage(ActivityCategoryQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityCategoryVO> list = activityCategoryDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityCategoryAddForm addForm) {
        ActivityCategoryEntity activityCategoryEntity = SmartBeanUtil.copy(addForm, ActivityCategoryEntity.class);
        activityCategoryDao.insert(activityCategoryEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityCategoryUpdateForm updateForm) {
        ActivityCategoryEntity activityCategoryEntity = SmartBeanUtil.copy(updateForm, ActivityCategoryEntity.class);
        activityCategoryDao.updateById(activityCategoryEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        activityCategoryDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        activityCategoryDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }

    public List<SimpleActivityCategoryVO> getAll() {
        return activityCategoryDao.getAll();
    }
}
