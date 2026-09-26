package com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 待评价活动 VO（门户）：
 * 已报名 + 已签到 + 活动已结束 + 尚未评价
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class PendingEvaluationVO {

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "活动标题")
    private String title;

    @Schema(description = "活动封面")
    private String coverImg;

    @Schema(description = "活动结束时间")
    private LocalDateTime activityEndTime;

}
