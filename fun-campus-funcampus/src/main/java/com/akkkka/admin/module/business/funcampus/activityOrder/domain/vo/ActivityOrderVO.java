package com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 活动报名订单 VO
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@Data
public class ActivityOrderVO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "活动标题")
    private String activityTitle;

    @Schema(description = "下单用户id")
    private Long userId;

    @Schema(description = "下单用户名（管理端列表展示）")
    private String username;

    @Schema(description = "订单金额（分）")
    private Integer amountFen;

    @Schema(description = "订单状态：0-待支付 1-已支付 2-已关闭 3-退款中 4-已退款 5-退款失败")
    private Integer status;

    @Schema(description = "支付渠道：1-微信 2-支付宝 3-Mock")
    private Integer payChannel;

    @Schema(description = "渠道订单号")
    private String channelOrderNo;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "支付截止时间")
    private LocalDateTime expireTime;

    @Schema(description = "关闭时间")
    private LocalDateTime closeTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
