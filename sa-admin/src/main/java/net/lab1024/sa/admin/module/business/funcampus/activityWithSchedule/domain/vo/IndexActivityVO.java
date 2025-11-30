package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2025-11-29 16:44
 * 首页活动列表
 */
@Data
public class IndexActivityVO {
    private Page<ActivityWithScheduleVO> mySchool;
    private Page<ActivityWithScheduleVO> global;
}
