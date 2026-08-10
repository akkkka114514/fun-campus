package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动与时间表 综合列表VO
 *
 * @Author akkkka114514
 * @Date 2025-09-07
 * @Copyright akkkka114514
 */
@Data
public class ActivityWithScheduleVO {

    /** 主键 */
    private Long id;

    /** 活动标题 */
    private String title;

    /** 活动状态 */
    private Integer status;

    /** 活动地点 */
    private String position;

    /** 能得到的学分 */
    private BigDecimal scoreCanGet;

    /** 报名人数限制 */
    private Integer enrollNumLimit;

    /** 活动所属学校ID */
    private Long activityBelongToSchoolId;

    /** 活动所属学校名称 */
    private String activityBelongToSchoolName;

    /** 活动所属组织ID */
    private Long activityBelongToOrganizationId;

    /** 活动所属组织名称 */
    private String activityBelongToOrganizationName;

    /** 活动所属学院ID */
    private Long activityBelongToCollegeId;

    /** 活动所属学院名称 */
    private String activityBelongToCollegeName;

    /** 描述 */
    private String description;

    /** 报名需审核 */
    private Boolean enrollNeedReview;

    /** 需要签退 */
    private Boolean needSignOut;

    /** 附件 */
    private String attachment;

    /** 分类ID */
    private Long categoryId;

    /** 分类名称 */
    private String categoryName;

    /** 封面图片 */
    private String coverImg;

    /** 活动管理员ID */
    private Long activityManagerId;

    /** 是否删除 */
    private Boolean deletedFlag;

    /** 报名开始时间 */
    private LocalDateTime enrollStartTime;

    /** 报名结束时间 */
    private LocalDateTime enrollEndTime;

    /** 活动开始时间 */
    private LocalDateTime activityStartTime;

    /** 活动结束时间 */
    private LocalDateTime activityEndTime;

    /** 签到开始时间 */
    private LocalDateTime signinStartTime;

    /** 签到结束时间 */
    private LocalDateTime signinEndTime;

    /** 签退开始时间 */
    private LocalDateTime signoutStartTime;

    /** 签退结束时间 */
    private LocalDateTime signoutEndTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 修改时间 */
    private LocalDateTime updateTime;
}
