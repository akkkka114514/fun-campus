package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.vo.ActivityReviewLogVO;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import com.akkkka.common.domain.IdNameVO;

import lombok.Data;

@Data
public class ActivityReviewProposalVO {
    private ActivityVO activityVO;
    private ActivityScheduleVO scheduleVO;
    private ActivityReviewLogVO reviewLogVO;
    private List<IdNameVO> canEnrollCollege;
    private List<IdNameVO> canEnrollTribe;
    private List<IdNameVO> canEnrollGrade;
    private List<PortalUserVO> activityManagers;
    private EditActivityDraftSelectionsVO editActivityDraftSelectionsVO;
}
