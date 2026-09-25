package com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建订单结果 VO
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@Data
public class ActivityOrderCreateVO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单金额（分）")
    private Integer amountFen;

    @Schema(description = "支付截止时间")
    private LocalDateTime expireTime;

    @Schema(description = "收银台/支付跳转链接（mock 渠道为模拟收银台页面）")
    private String cashierUrl;
}
