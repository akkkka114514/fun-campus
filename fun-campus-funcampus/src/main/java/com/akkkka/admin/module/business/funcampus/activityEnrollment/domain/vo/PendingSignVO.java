package com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 待签到/待签退活动 VO（门户）：
 * 已报名 + 当前处于签到（签退）时间窗口内，过滤条件分别对齐 signIn/signOut 校验链
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 * @Copyright akkkka114514
 */

@Data
public class PendingSignVO {

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "活动标题")
    private String activityTitle;

    @Schema(description = "活动地点")
    private String position;

    @Schema(description = "窗口开始时间（待签到时为签到开始，待签退时为签退开始）")
    private LocalDateTime windowStartTime;

    @Schema(description = "窗口结束时间（待签到时为签到结束，待签退时为签退结束）")
    private LocalDateTime windowEndTime;

    @Schema(description = "活动开始时间")
    private LocalDateTime activityStartTime;

    @Schema(description = "活动结束时间")
    private LocalDateTime activityEndTime;
}
