package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.MyEnrollmentStatusVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderVO;
import com.akkkka.common.domain.IdNameVO;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "活动基本信息")
    private ActivityVO activity;

    /** 活动时间表 */
    @Schema(description = "活动时间表")
    private ActivityScheduleVO schedule;

    /** 报名的用户 */
    @Schema(description = "报名的用户")
    private List<EnrollerVO> enrollUsers;

    /** 报名人数 */
    @Schema(description = "报名人数")
    private Integer enrollNum;

    /** 签到人数 */
    @Schema(description = "签到人数")
    private Long signInNum;

    /** 允许报名的年级 */
    @Schema(description = "允许报名的年级")
    private List<IdNameVO> canEnrollGrade;

    /** 允许报名的学院 */
    @Schema(description = "允许报名的学院")
    private List<IdNameVO> canEnrollCollege;

    /** 允许报名的部落 */
    @Schema(description = "允许报名的部落")
    private List<IdNameVO> canEnrollTribe;

    /** 当前用户在本活动的最新订单（未登录/无订单时为 null，用于付费报名状态展示） */
    @Schema(description = "当前用户在本活动的最新订单，未登录/无订单时为 null")
    private ActivityOrderVO currentUserOrder;

    /** 当前用户在本活动的报名状态（未登录/非门户用户时为 null，免费付费通用） */
    @Schema(description = "当前用户在本活动的报名状态，未登录/非门户用户时为 null")
    private MyEnrollmentStatusVO currentUserEnrollment;

    //todo 评论
}
