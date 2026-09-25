package com.akkkka.admin.module.business.funcampus.activityWithSchedule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEnrollNum;
import org.apache.ibatis.annotations.Mapper;

/**
 * author:akkkka114514
 * create at 2025-09-29 09:37
 */
@Mapper
public interface ActivityEnrollNumDao extends BaseMapper<ActivityEnrollNum> {
    boolean increaseEnrollNum(Long activityId);

    boolean decreaseEnrollNum(Long activityId);

    /**
     * 报名计数清零（用于状态倒退清空报名数据）
     *
     * @param activityId 活动ID
     * @return 是否更新成功
     */
    boolean resetEnrollNum(Long activityId);
}
