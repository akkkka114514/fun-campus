package net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.dao.ActivitySigninManagerDao;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.ActivitySigninManager.domain.vo.ActivitySigninManagerVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 活动签到管理员 Service
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:10:19
 * @Copyright akkkka114514
 */

@Service
public class ActivitySigninManagerService {

    @Resource
    private ActivitySigninManagerDao activitySigninManagerDao;

    /**
     * 分页查询
     */
    public PageResult<ActivitySigninManagerVO> queryPage(ActivitySigninManagerQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ActivitySigninManagerVO> list = activitySigninManagerDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivitySigninManagerAddForm addForm) {
        ActivitySigninManagerEntity activitySigninManagerEntity = SmartBeanUtil.copy(addForm, ActivitySigninManagerEntity.class);
        activitySigninManagerDao.insert(activitySigninManagerEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(ActivitySigninManagerUpdateForm updateForm) {
        ActivitySigninManagerEntity activitySigninManagerEntity = SmartBeanUtil.copy(updateForm, ActivitySigninManagerEntity.class);
        activitySigninManagerDao.updateById(activitySigninManagerEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        activitySigninManagerDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        activitySigninManagerDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
