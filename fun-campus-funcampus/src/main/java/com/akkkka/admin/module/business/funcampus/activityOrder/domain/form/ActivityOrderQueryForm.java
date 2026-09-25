package com.akkkka.admin.module.business.funcampus.activityOrder.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动报名订单 分页查询表单（我的订单）
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ActivityOrderQueryForm extends PageParam {

    @Schema(description = "订单状态：0-待支付 1-已支付 2-已关闭 3-退款中 4-已退款 5-退款失败")
    private Integer status;

    @Schema(description = "活动id")
    private Long activityId;
}
