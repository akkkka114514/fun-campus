package com.akkkka.admin.module.business.funcampus.payment.domain.dto;

import com.akkkka.admin.module.business.funcampus.payment.constant.PayChannelEnum;

import lombok.Data;

/**
 * 支付回调解析结果
 * <p>
 * 由各渠道 PaymentChannel.parsePayCallback 验签解析后产出，
 * 统一收敛到 PaymentService.handlePayNotify 处理（mock 与真实渠道同一入口）
 *
 * author:akkkka114514
 * create at 2026-09-24
 */
@Data
public class PayNotifyResult {

    /** 渠道 */
    private PayChannelEnum channel;

    /** 商户订单号 */
    private String orderNo;

    /** 渠道订单号 */
    private String channelOrderNo;

    /** 支付金额（分），用于与订单金额比对，防止金额篡改 */
    private Integer amountFen;

    /** 支付是否成功 */
    private boolean success;

    /** 失败原因（success=false 时有值） */
    private String failReason;
}
