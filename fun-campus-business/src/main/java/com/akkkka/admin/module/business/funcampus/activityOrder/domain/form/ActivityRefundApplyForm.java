package com.akkkka.admin.module.business.funcampus.activityOrder.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 活动报名 退款申请表单
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@Data
public class ActivityRefundApplyForm {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号")
    private String orderNo;

    @Size(max = 200, message = "退款原因不能超过200个字符")
    @Schema(description = "退款原因（可选）")
    private String reason;
}
