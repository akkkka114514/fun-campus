package com.akkkka.admin.module.business.funcampus.payment.domain.dto;

import java.time.LocalDateTime;

import com.akkkka.admin.module.business.funcampus.payment.constant.PayChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * 退款回调解析结果
 * <p>
 * 由各渠道 PaymentChannel.parseRefundCallback 验签解析后产出，
 * 统一收敛到 PaymentService.handleRefundNotify 处理
 *
 * author:akkkka114514
 * create at 2026-09-24
 */
@Data
public class RefundNotifyResult {

    /** 渠道 */
    @Schema(description = "渠道")
    private PayChannelEnum channel;

    /** 商户退款单号 */
    @Schema(description = "商户退款单号")
    private String refundNo;

    /** 渠道退款单号 */
    @Schema(description = "渠道退款单号")
    private String channelRefundNo;

    /** 退款是否成功 */
    @Schema(description = "退款是否成功")
    private boolean success;

    /** 退款完成时间（success=true 时有值） */
    @Schema(description = "退款完成时间（success=true 时有值）")
    private LocalDateTime refundTime;

    /** 失败原因（success=false 时有值） */
    @Schema(description = "失败原因（success=false 时有值）")
    private String failReason;
}
