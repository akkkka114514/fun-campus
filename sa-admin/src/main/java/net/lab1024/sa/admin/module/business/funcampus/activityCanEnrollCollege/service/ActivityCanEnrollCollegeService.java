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
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 活动能报名的学院 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-01 20:24:18
 * @Copyright akkkka114514
 */

@Service
public class ActivityCanEnrollCollegeService {

    @Resource
    private ActivityCanEnrollCollegeDao activityCanEnrollCollegeDao;

    /**
     * 分页查询
     */
    public PageResult<ActivityCanEnrollCollegeVO> queryPage(ActivityCanEnrollCollegeQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityCanEnrollCollegeVO> list = activityCanEnrollCollegeDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityCanEnrollCollegeAddForm addForm) {
        ActivityCanEnrollCollegeEntity activityCanEnrollCollegeEntity = SmartBeanUtil.copy(addForm, ActivityCanEnrollCollegeEntity.class);
        activityCanEnrollCollegeDao.insert(activityCanEnrollCollegeEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityCanEnrollCollegeUpdateForm updateForm) {
        ActivityCanEnrollCollegeEntity activityCanEnrollCollegeEntity = SmartBeanUtil.copy(updateForm, ActivityCanEnrollCollegeEntity.class);
        activityCanEnrollCollegeDao.updateById(activityCanEnrollCollegeEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        activityCanEnrollCollegeDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        activityCanEnrollCollegeDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
