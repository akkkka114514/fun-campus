package net.lab1024.sa.admin.module.business.funcampus.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.lab1024.sa.admin.module.business.funcampus.dao.ActivityDao;
import net.lab1024.sa.admin.module.business.funcampus.domain.entity.ActivityEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动管理  Manager
 *
 * @Author akkkka114514
 * @Date 2025-09-04 13:41:42
 * @Copyright akkkka114514
 */
@Service
public class ActivityManager extends ServiceImpl<ActivityDao, ActivityEntity> {

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
     * 获取需要检查状态的活动列表（未删除且未结束的活动）
     *
     * @param limit 限制数量
     * @return 活动列表
     */
    public List<ActivityEntity> getActivitiesForStatusCheck(int limit) {
        QueryWrapper<ActivityEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted_flag", false);
        queryWrapper.ne("status", 4); // 排除已结束的活动
        queryWrapper.last("LIMIT " + limit);
        return this.list(queryWrapper);
    }
}