package net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.dao.ActivityCanEnrollGradeDao;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.vo.ActivityCanEnrollGradeVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 活动能报名的年级 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:42:17
 * @Copyright akkkka114514
 */

@Service
public class ActivityCanEnrollGradeService {

    @Resource
    private ActivityCanEnrollGradeDao activityCanEnrollGradeDao;

    /**
     * 分页查询
     */
    public PageResult<ActivityCanEnrollGradeVO> queryPage(ActivityCanEnrollGradeQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivityCanEnrollGradeVO> list = activityCanEnrollGradeDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivityCanEnrollGradeAddForm addForm) {
        ActivityCanEnrollGradeEntity activityCanEnrollGradeEntity = SmartBeanUtil.copy(addForm, ActivityCanEnrollGradeEntity.class);
        activityCanEnrollGradeDao.insert(activityCanEnrollGradeEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivityCanEnrollGradeUpdateForm updateForm) {
        ActivityCanEnrollGradeEntity activityCanEnrollGradeEntity = SmartBeanUtil.copy(updateForm, ActivityCanEnrollGradeEntity.class);
        activityCanEnrollGradeDao.updateById(activityCanEnrollGradeEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        activityCanEnrollGradeDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        activityCanEnrollGradeDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
