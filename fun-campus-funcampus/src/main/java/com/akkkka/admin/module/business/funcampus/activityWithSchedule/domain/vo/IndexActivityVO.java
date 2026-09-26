package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2025-11-29 16:44
 * 首页活动列表
 */
@Data
public class IndexActivityVO {
    @Schema(description = "本校活动列表")
    private Page<ActivityWithScheduleVO> mySchoolActivities;

    @Schema(description = "全局活动列表")
    private Page<ActivityWithScheduleVO> globalActivities;
}
