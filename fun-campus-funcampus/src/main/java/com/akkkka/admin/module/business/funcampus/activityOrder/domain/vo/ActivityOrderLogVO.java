package com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 活动订单操作记录 VO（管理端订单详情时间线）
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */

@Data
public class ActivityOrderLogVO {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "操作类型：1-创建订单 2-支付成功 3-支付失败回调 4-支付回调晚于关单 5-关闭订单 6-发起退款申请 7-退款成功 8-退款失败 9-管理端重试退款")
    private Integer action;

    @Schema(description = "操作类型文案")
    private String actionName;

    @Schema(description = "操作人类型：1-门户用户 2-管理端用户 3-系统")
    private Integer operatorType;

    @Schema(description = "操作人文案（如 用户#5 / 管理员#1 / 系统）")
    private String operatorName;

    @Schema(description = "操作详情")
    private String detail;

    @Schema(description = "发生时间")
    private LocalDateTime createTime;
}
