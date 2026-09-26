package com.akkkka.admin.module.business.funcampus.payment.domain.dto;

import com.akkkka.admin.module.business.funcampus.payment.constant.PayChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * 渠道预下单结果
 * <p>
 * prepay 后返回给前端的支付参数：mock 渠道为模拟收银台链接，
 * 真实渠道（微信/支付宝）为拉起支付所需的参数（后续扩展）
 *
 * author:akkkka114514
 * create at 2026-09-24
 */
@Data
public class PayPrepayResult {

    /** 渠道 */
    @Schema(description = "渠道")
    private PayChannelEnum channel;

    /** 收银台/支付跳转链接（mock 为模拟收银台页面地址） */
    @Schema(description = "收银台/支付跳转链接（mock 为模拟收银台页面地址）")
    private String cashierUrl;
}
