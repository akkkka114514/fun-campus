package net.lab1024.sa.admin.module.business.funcampus.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.business.funcampus.dao.ActivityDao;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 活动管理  Manager
 *
 * @Author akkkka114514
 * @Date 2025-09-04 13:41:42
 * @Copyright akkkka114514
 */
@Service
public class ActivityManager extends ServiceImpl<ActivityDao, ActivityEntity> {

    @Resource
    private ActivityDao activityDao;

    /**
     * 更新活动状态
     *
     * @param activityId 活动ID
     * @param status 新状态
     * @return 是否更新成功
     */
    public boolean updateStatus(Long activityId, Integer status) {
        if (activityId == null || status == null) {
            return false;
        }
        
        UpdateWrapper<ActivityEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", activityId);
        updateWrapper.set("status", status);
        return this.update(updateWrapper);
    }

    /**
     * 批量更新活动状态
     *
     * @param statusMap 活动ID和状态的映射
     * @return 更新成功的记录数
     */
    public int updateStatusBatch(Map<Long, Integer> statusMap) {
        if (statusMap == null || statusMap.isEmpty()) {
            return 0;
        }

        activityDao.updateStatusBatch(statusMap);
        return statusMap.size(); // 返回更新的记录数
    }
}