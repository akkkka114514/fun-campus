package net.lab1024.sa.admin.module.business.funcampus.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动时间表 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-09-06 15:54:07
 * @Copyright akkkka114514
 */

@Data
public class ActivityScheduleVO {


    @Schema(description = "主键")
    private Long activityId;

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
