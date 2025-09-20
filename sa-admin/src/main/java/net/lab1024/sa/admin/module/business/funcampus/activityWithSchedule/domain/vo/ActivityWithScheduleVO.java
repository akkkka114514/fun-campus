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
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "活动标题")
    private String title;

    @Schema(description = "活动状态")
    private Integer status;

    @Schema(description = "活动地点")
    private String position;

    @Schema(description = "能得到的学分")
    private BigDecimal scoreCanGet;

    @Schema(description = "报名人数限制")
    private Integer enrollNumLimit;

    @Schema(description = "活动所属学校")
    private Long activitySchoolId;

    @Schema(description = "活动所属组织")
    private Long activityOrganizerId;

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;
}
