package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import com.akkkka.common.domain.IdNameVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * author:akkkka114514
 * create at 2026-05-09 20:47
 */
@Data
public class RejectedActivityDraftVO{
    @Schema(description = "活动基本信息")
    private ActivityVO activityVO;

    @Schema(description = "活动时间表")
    private ActivityScheduleVO scheduleVO;

    @Schema(description = "初审审核人")
    private IdNameVO initialReviewer;

    @Schema(description = "允许报名的学院")
    private List<IdNameVO> canEnrollCollege;

    @Schema(description = "允许报名的部落")
    private List<IdNameVO> canEnrollTribe;

    @Schema(description = "允许报名的年级")
    private List<IdNameVO> canEnrollGrade;

    @Schema(description = "编辑活动草稿的可选项（学院/组织/审核人/分类/年级）")
    private EditActivityDraftSelectionsVO editActivityDraftSelectionsVO;
}
