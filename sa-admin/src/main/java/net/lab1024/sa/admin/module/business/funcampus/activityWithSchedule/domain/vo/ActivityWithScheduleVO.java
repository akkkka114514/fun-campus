package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * author:akkkka114514
 * create at 2025-09-17 14:58
 */

@Data
public class ActivityWithScheduleVO {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;

    /**
     * 活动标题
     */
    @Schema(description = "活动标题")
    private String title;

    /**
     * 活动状态
     */
    @Schema(description = "活动状态")
    private Integer status;

    /**
     * 活动地点
     */
    @Schema(description = "活动地点")
    private String position;

    /**
     * 能得到的学分
     */
    @Schema(description = "能得到的学分")
    private BigDecimal scoreCanGet;

    /**
     * 报名人数限制
     */
    @Schema(description = "报名人数限制")
    private Integer enrollNumLimit;

    /**
     * 活动所属学校
     */
    @Schema(description = "所属学校id")
    private Long activityBelongToSchoolId;

    /**
     * 活动所属组织
     */
    @Schema(description = "所属组织id")
    private Long activityBelongToOrganizationId;

    /**
     * 活动所属学院
     */
    @Schema(description = "所属学院id")
    private Long activityBelongToCollegeId;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 报名需审核
     */
    @Schema(description = "报名需审核")
    private boolean enrollNeedReview;

    /**
     * 需要签退
     */
    @Schema(description = "需要签退")
    private boolean needSignOut;

    /**
     * 附件
     */
    @Schema(description = "附件")
    private String attachment;

    /**
     * 分类
     */
    @Schema(description = "分类id")
    private Long categoryId;

    /**
     * 封面图片
     */
    @Schema(description = "封面图片")
    private String coverImg;

    /**
     * 活动管理员和发起者
     */
    @Schema(description = "活动管理员和发起者id")
    private Long activityManagerId;

    @Schema(description = "报名开始时间")
    private LocalDateTime enrollStartTime;

    @Schema(description = "报名结束时间")
    private LocalDateTime enrollEndTime;

    @Schema(description = "活动开始时间")
    private LocalDateTime activityStartTime;

    @Schema(description = "活动结束时间")
    private LocalDateTime activityEndTime;

    @Schema(description = "签到开始时间")
    private LocalDateTime signinStartTime;

    @Schema(description = "签到结束时间")
    private LocalDateTime signinEndTime;

    //签退开始时间
    @Schema(description = "签退开始时间")
    private LocalDateTime signoutStartTime;

    //签退结束时间
    @Schema(description = "签退结束时间")
    private LocalDateTime signoutEndTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;
}
