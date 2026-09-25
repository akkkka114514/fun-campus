package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2025-11-29 16:44
 * 首页活动列表
 */
@Data
public class IndexActivityVO {
    private Page<ActivityWithScheduleVO> mySchoolActivities;
    private Page<ActivityWithScheduleVO> globalActivities;
}
