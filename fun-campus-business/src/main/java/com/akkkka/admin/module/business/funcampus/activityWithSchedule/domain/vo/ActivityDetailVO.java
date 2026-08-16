package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import java.util.List;

import com.akkkka.common.domain.IdNameVO;
import lombok.Data;

/**
 * 活动详情VO
 *
 * @Author akkkka114514
 * @Date 2025-12-07 16:01
 * @Copyright akkkka114514
 */
@Data
public class ActivityDetailVO {

    /** 活动基本信息 */
    private ActivityVO activity;

    /** 活动时间表 */
    private ActivityScheduleVO schedule;

    /** 报名的用户 */
    private List<EnrollerVO> enrollUsers;

    /** 报名人数 */
    private Integer enrollNum;

    /** 签到人数 */
    private Long signInNum;

    /** 允许报名的年级 */
    private List<IdNameVO> canEnrollGrade;

    /** 允许报名的学院 */
    private List<IdNameVO> canEnrollCollege;

    /** 允许报名的部落 */
    private List<IdNameVO> canEnrollTribe;

    //todo 评论
}
