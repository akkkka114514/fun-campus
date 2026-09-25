package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import com.akkkka.common.domain.IdNameVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * author:akkkka114514
 * create at 2026-01-16 10:24
 */
@Data
public class EditActivityDraftSelectionsVO {
    List<IdNameVO> selectableCollegeVOList;

    List<IdNameVO> selectableOrganizationVOList;
    //<collegeId,list>
    Map<Long,List<IdNameVO>> selectableCollegeReviewerList;
    //<organizationId,list>
    Map<Long,List<IdNameVO>> selectableOrganizationReviewerList;
    //活动分类
    List<IdNameVO> selectableCategoryVOList;
    //活动年级
    List<IdNameVO> selectableGradeVOList;
}
