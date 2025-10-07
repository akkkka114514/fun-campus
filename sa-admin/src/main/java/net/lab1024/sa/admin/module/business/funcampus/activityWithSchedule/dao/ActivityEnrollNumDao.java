package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEnrollNum;
import org.apache.ibatis.annotations.Mapper;

/**
 * author:akkkka114514
 * create at 2025-09-29 09:37
 */
@Mapper
public interface ActivityEnrollNumDao extends BaseMapper<ActivityEnrollNum> {
    boolean increaseEnrollNum(Long activityId);

    boolean decreaseEnrollNum(Long activityId);
}
