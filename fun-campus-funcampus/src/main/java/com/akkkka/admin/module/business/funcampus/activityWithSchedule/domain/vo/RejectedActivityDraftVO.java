package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import com.akkkka.common.domain.IdNameVO;
import lombok.Data;

import java.util.List;

/**
 * author:akkkka114514
 * create at 2026-05-09 20:47
 */
@Data
public class RejectedActivityDraftVO{
    private ActivityVO activityVO;
    private ActivityScheduleVO scheduleVO;
    private IdNameVO initialReviewer;
    private List<IdNameVO> canEnrollCollege;
    private List<IdNameVO> canEnrollTribe;
    private List<IdNameVO> canEnrollGrade;
    private EditActivityDraftSelectionsVO editActivityDraftSelectionsVO;
}
