package com.akkkka.module.support.pay.domain.dto;

import java.time.LocalDateTime;

import com.akkkka.module.support.pay.constant.PayChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * 渠道查单结果
 * <p>
 * 超时关单任务在关单前向渠道主动查询订单真实支付结果（真实渠道回调可能丢失）：
 * 渠道已支付 → 走回调同一入账路径补偿；确认未支付 → 才允许关单
 *
 * author:akkkka114514
 * create at 2026-10-02
 */
@Data
public class PayQueryResult {

    /** 渠道 */
    @Schema(description = "渠道")
    private PayChannelEnum channel;

    /** 商户订单号 */
    @Schema(description = "商户订单号")
    private String orderNo;

    /** 渠道侧是否已支付 */
    @Schema(description = "渠道侧是否已支付")
    private boolean paid;

    /** 渠道订单号（paid=true 时有值） */
    @Schema(description = "渠道订单号（paid=true 时有值）")
    private String channelOrderNo;

    /** 渠道侧支付金额（分），用于与订单金额比对 */
    @Schema(description = "渠道侧支付金额（分）")
    private Integer amountFen;

    /** 渠道侧支付时间（paid=true 时有值） */
    @Schema(description = "渠道侧支付时间")
    private LocalDateTime payTime;

    /** 渠道侧状态描述（如：已支付/预支付单未支付/渠道无此单） */
    @Schema(description = "渠道侧状态描述")
    private String statusDesc;
}
