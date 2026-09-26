package com.akkkka.admin.module.business.funcampus.portalUser.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 前端用户-实践积分明细 VO
 * <p>
 * 口径：已结束（activity.status=4）且已签到的报名，按活动分值(score_can_get)计分，
 * 派生自报名与活动数据（演示口径，正式版应由结算任务落表）
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 * @Copyright akkkka114514
 */

@Data
public class GradeScoreDetailVO {

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "活动标题")
    private String activityTitle;

    @Schema(description = "获得分值")
    private BigDecimal score;

    @Schema(description = "完成时间（活动结束时间）")
    private LocalDateTime finishTime;
}
