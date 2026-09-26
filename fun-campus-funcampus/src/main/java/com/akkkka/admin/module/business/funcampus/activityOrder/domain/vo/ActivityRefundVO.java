package com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 活动报名退款记录 VO（管理端列表）
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 * @Copyright akkkka114514
 */

@Data
public class ActivityRefundVO {

    @Schema(description = "退款单号")
    private String refundNo;

    @Schema(description = "关联订单号")
    private String orderNo;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "活动标题")
    private String activityTitle;

    @Schema(description = "退款用户id")
    private Long userId;

    @Schema(description = "退款用户名")
    private String username;

    @Schema(description = "退款金额（分）")
    private Integer amountFen;

    @Schema(description = "退款原因类型：1-用户申请 2-活动取消 3-报名审核未通过 4-其它")
    private Integer reasonType;

    @Schema(description = "退款原因类型名称")
    private String reasonTypeName;

    @Schema(description = "退款备注")
    private String reason;

    @Schema(description = "退款状态：0-退款中 1-成功 2-失败")
    private Integer status;

    @Schema(description = "退款状态名称")
    private String statusName;

    @Schema(description = "渠道退款单号")
    private String channelRefundNo;

    @Schema(description = "退款完成时间")
    private LocalDateTime refundTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
