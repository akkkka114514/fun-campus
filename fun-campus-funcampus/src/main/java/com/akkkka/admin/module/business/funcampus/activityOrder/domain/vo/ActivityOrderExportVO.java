package com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 活动报名订单 Excel 导出 VO（管理端）
 * <p>
 * 字段均为已完成格式化的字符串（金额元、状态/渠道文本、时间字符串），
 * 由 Service 组装，导出时无需再挂转换器
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */

@Data
public class ActivityOrderExportVO {

    @Schema(description = "订单号")
    @ExcelProperty(value = "订单号", order = 0)
    private String orderNo;

    @Schema(description = "活动标题")
    @ExcelProperty(value = "活动标题", order = 1)
    private String activityTitle;

    @Schema(description = "下单用户")
    @ExcelProperty(value = "下单用户", order = 2)
    private String username;

    @Schema(description = "订单金额（元）")
    @ExcelProperty(value = "订单金额（元）", order = 3)
    private String amountYuan;

    @Schema(description = "订单状态")
    @ExcelProperty(value = "订单状态", order = 4)
    private String statusName;

    @Schema(description = "支付渠道")
    @ExcelProperty(value = "支付渠道", order = 5)
    private String payChannelName;

    @Schema(description = "渠道订单号")
    @ExcelProperty(value = "渠道订单号", order = 6)
    private String channelOrderNo;

    @Schema(description = "支付时间")
    @ExcelProperty(value = "支付时间", order = 7)
    private String payTime;

    @Schema(description = "支付截止时间")
    @ExcelProperty(value = "支付截止时间", order = 8)
    private String expireTime;

    @Schema(description = "关闭时间")
    @ExcelProperty(value = "关闭时间", order = 9)
    private String closeTime;

    @Schema(description = "创建时间")
    @ExcelProperty(value = "创建时间", order = 10)
    private String createTime;
}
