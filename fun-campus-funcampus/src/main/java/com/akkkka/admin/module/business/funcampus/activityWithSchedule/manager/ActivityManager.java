package com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao.ActivityDao;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
    public boolean updateStatus(Long activityId, ActivityStatus status) {
        if (activityId == null || status == null) {
            return false;
        }
        
        UpdateWrapper<ActivityEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", activityId);
        updateWrapper.set("status", status.getCode());
        return this.update(updateWrapper);
    }

    /**
     * 条件更新活动状态：仅当数据库中的当前状态与预期一致时才更新
     * 供定时任务使用，防止与业务并发改状态时相互覆盖；返回 false 表示状态已被并发修改
     *
     * @param activityId 活动ID
     * @param newStatus 新状态
     * @param oldStatus 预期的当前状态
     * @return 是否更新成功
     */
    public boolean updateStatusIfMatch(Long activityId, ActivityStatus newStatus, ActivityStatus oldStatus) {
        if (activityId == null || newStatus == null || oldStatus == null) {
            return false;
        }
        UpdateWrapper<ActivityEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", activityId);
        updateWrapper.eq("status", oldStatus.getCode());
        updateWrapper.set("status", newStatus.getCode());
        return this.update(updateWrapper);
    }

    /**
     * 分页查询等待状态推进的活动
     * 只查询已发布且未结束的活动（0等待报名~3进行中），用于定时任务按时间表推进状态；
     * 待审核(9)等活动不由定时任务驱动，故不在候选范围内
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果（仅含 id、status 字段）
     */
    public IPage<ActivityEntity> pagePendingStatusActivities(long pageNum, long pageSize) {
        Page<ActivityEntity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ActivityEntity> queryWrapper = new LambdaQueryWrapper<>();
        // 状态推进只需 id 与当前状态
        queryWrapper.select(ActivityEntity::getId, ActivityEntity::getStatus);
        queryWrapper.eq(ActivityEntity::getDeletedFlag, false);
        queryWrapper.in(ActivityEntity::getStatus, ActivityStatus.pendingTimeLineCodes());
        return this.page(page, queryWrapper);
    }
}