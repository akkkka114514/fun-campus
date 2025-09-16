package net.lab1024.sa.admin.module.business.funcampus.service;

import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityEntity;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityScheduleEntity;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityAddForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityScheduleAddForm;
import net.lab1024.sa.admin.module.business.funcampus.domain.form.ActivityWithScheduleForm;
import net.lab1024.sa.admin.module.business.funcampus.job.ActivityStatusUpdateJob;
import net.lab1024.sa.admin.module.business.funcampus.manager.ActivityManager;
import net.lab1024.sa.admin.module.business.funcampus.manager.ActivityScheduleManager;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.module.support.job.api.SmartJobService;
import net.lab1024.sa.base.module.support.job.api.domain.SmartJobAddForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动和时间表 组合服务
 *
 * @Author akkkka114514
 * @Date 2025-09-07
 * @Copyright akkkka114514
 */

@Service
public class ActivityWithScheduleService {

    @Resource
    private ActivityManager activityManager;

    @Resource
    private ActivityScheduleManager activityScheduleManager;
    
    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 同时添加活动和活动时间表
     * 使用编程式事务，根据save方法的返回值决定是否回滚
     *
     * @param addForm 活动和时间表信息
     * @return ResponseDTO
     */
    public ResponseDTO<String> addActivityWithSchedule(ActivityWithScheduleForm addForm) {
        // 使用编程式事务，根据save方法的返回值决定是否回滚
        return transactionTemplate.execute(status -> {
            try {
                // 添加活动
                ActivityAddForm activityForm = addForm.getActivity();
                ActivityEntity activityEntity = SmartBeanUtil.copy(activityForm, ActivityEntity.class);
                boolean activitySaved = activityManager.save(activityEntity);
                
                if (!activitySaved) {
                    // 如果保存失败，手动回滚事务
                    status.setRollbackOnly();
                    return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动信息保存失败");
                }
                
                // 获取活动ID，用于关联时间表
                Long activityId = activityEntity.getId();
                
                // 添加活动时间表
                ActivityScheduleAddForm scheduleForm = addForm.getSchedule();
                ActivityScheduleEntity scheduleEntity = SmartBeanUtil.copy(scheduleForm, ActivityScheduleEntity.class);
                // 关联活动ID
                scheduleEntity.setActivityId(activityId);
                boolean scheduleSaved = activityScheduleManager.save(scheduleEntity);
                
                if (!scheduleSaved) {
                    // 如果保存失败，手动回滚事务
                    status.setRollbackOnly();
                    return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "活动时间表保存失败");
                }
                return ResponseDTO.ok("活动和时间表创建成功");
            } catch (Exception e) {
                // 发生异常时回滚事务
                status.setRollbackOnly();
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "保存过程中发生异常：" + e.getMessage());
            }
        });
    }

    public ResponseDTO<String> deleteActivityWithSchedule(Long activityId) {
        ActivityEntity deletedActivity = new ActivityEntity();
        deletedActivity.setDeletedFlag(true);
        ActivityScheduleEntity deletedSchedule = new ActivityScheduleEntity();
        deletedSchedule.setDeletedFlag(true);
        transactionTemplate.execute(status -> {
            if(!activityManager.updateById(deletedActivity)){
                status.setRollbackOnly();
            }
            if(!activityScheduleManager.updateById(deletedSchedule)){
                status.setRollbackOnly();
            }
            return ResponseDTO.ok("删除成功");
        });
        return null;
    }

}