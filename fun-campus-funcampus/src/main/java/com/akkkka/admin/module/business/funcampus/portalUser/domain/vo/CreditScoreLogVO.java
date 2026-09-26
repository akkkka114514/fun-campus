package com.akkkka.admin.module.business.funcampus.portalUser.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 前端用户-信誉分变动记录 VO
 * <p>
 * 口径：已结束（activity.status=4）且未签到的报名视为爽约，扣除固定分值，
 * changeScore/reason 由服务层填充（演示口径，派生自报名数据）
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 * @Copyright akkkka114514
 */

@Data
public class CreditScoreLogVO {

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "活动标题")
    private String activityTitle;

    @Schema(description = "变动分值（负数表示扣分）")
    private Integer changeScore;

    @Schema(description = "变动原因")
    private String reason;

    @Schema(description = "变动时间（活动结束时间）")
    private LocalDateTime changeTime;
}
