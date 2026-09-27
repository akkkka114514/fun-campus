package com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 活动收入统计 VO（按活动汇总报名费，管理端）
 * <p>
 * 口径：报名费按「曾支付成功」订单（已支付/退款中/已退款/退款失败）汇总；
 * 退款仅统计这些订单中成功退款的退款单；净收入 = 报名费总额 - 退款总额（报表内部自洽）
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */

@Data
public class ActivityRevenueStatisticsVO {

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "活动标题")
    private String activityTitle;

    @Schema(description = "付费订单数（曾支付成功）")
    private Integer paidOrderCount;

    @Schema(description = "报名费总额（分，曾支付成功订单金额合计）")
    private Integer paidAmountFen;

    @Schema(description = "退款单数（成功退款）")
    private Integer refundOrderCount;

    @Schema(description = "退款总额（分，成功退款金额合计）")
    private Integer refundAmountFen;

    @Schema(description = "净收入（分）= 报名费总额 - 退款总额")
    private Integer netAmountFen;
}
