package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import lombok.Data;
import net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.vo.SimpleActivityCategoryVO;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.vo.SimpleCollegeInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.vo.SimpleGradeInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.vo.SimpleOrganizationInfoVO;
import net.lab1024.sa.admin.module.system.backendUser.domain.vo.SimpleBackendUserVO;

import java.util.List;
import java.util.Map;

/**
 * author:akkkka114514
 * create at 2026-01-16 10:24
 */
@Data
public class InitPublishActivityPageVO {
    List<SimpleCollegeInfoVO> collegeInfoVOList;

    List<SimpleOrganizationInfoVO> organizationInfoVOList;
    //<collegeId,list>
    Map<Long,List<SimpleBackendUserVO>> collegeReviewerList;
    //<organizationId,list>
    Map<Long,List<SimpleBackendUserVO>> organizationReviewerList;
    //活动分类
    List<SimpleActivityCategoryVO> activityCategoryVOList;
    //活动年级
    List<SimpleGradeInfoVO> gradeInfoVOList;
}
