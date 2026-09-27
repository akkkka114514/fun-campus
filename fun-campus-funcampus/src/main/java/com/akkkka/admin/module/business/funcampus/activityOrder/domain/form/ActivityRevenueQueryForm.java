package com.akkkka.admin.module.business.funcampus.activityOrder.domain.form;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 活动收入统计 查询表单（按支付时间范围过滤，管理端）
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */

@Data
public class ActivityRevenueQueryForm {

    @Schema(description = "支付时间起（含）")
    private LocalDateTime beginPayTime;

    @Schema(description = "支付时间止（含）")
    private LocalDateTime endPayTime;
}
