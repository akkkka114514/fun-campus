package com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动评价 VO（按活动列表 / 我的评价列表通用）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class ActivityEvaluationVO {

    @Schema(description = "评价id")
    private Long id;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "活动标题（我的评价列表带出）")
    private String activityTitle;

    @Schema(description = "活动封面（我的评价列表带出）")
    private String coverImg;

    @Schema(description = "评价人姓名")
    private String username;

    @Schema(description = "评分：1-5")
    private Integer score;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "评价时间")
    private LocalDateTime createTime;

}
