package com.akkkka.admin.module.business.funcampus.activityOrder.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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

    @Schema(description = "下单用户id（管理端筛选）")
    private Long userId;

    @Schema(description = "关键词（订单号/活动标题模糊匹配，管理端筛选）")
    private String keyword;

    @Schema(description = "创建时间起（管理端筛选）")
    private LocalDateTime beginCreateTime;

    @Schema(description = "创建时间止（管理端筛选）")
    private LocalDateTime endCreateTime;
}
