package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.vo.ActivityReviewLogVO;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import com.akkkka.common.domain.IdNameVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ActivityReviewProposalVO {
    @Schema(description = "活动基本信息")
    private ActivityVO activityVO;

    @Schema(description = "活动时间表")
    private ActivityScheduleVO scheduleVO;

    @Schema(description = "审核日志")
    private ActivityReviewLogVO reviewLogVO;

    @Schema(description = "允许报名的学院")
    private List<IdNameVO> canEnrollCollege;

    @Schema(description = "允许报名的部落")
    private List<IdNameVO> canEnrollTribe;

    @Schema(description = "允许报名的年级")
    private List<IdNameVO> canEnrollGrade;

    @Schema(description = "活动管理员列表")
    private List<PortalUserVO> activityManagers;

    @Schema(description = "编辑活动草稿的可选项（学院/组织/审核人/分类/年级）")
    private EditActivityDraftSelectionsVO editActivityDraftSelectionsVO;
}
