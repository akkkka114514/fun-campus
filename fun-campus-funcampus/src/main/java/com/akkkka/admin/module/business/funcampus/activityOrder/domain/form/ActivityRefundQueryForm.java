package com.akkkka.admin.module.business.funcampus.activityOrder.domain.form;

import java.time.LocalDateTime;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动报名退款记录 分页查询表单（管理端）
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ActivityRefundQueryForm extends PageParam {

    @Schema(description = "退款状态：0-退款中 1-成功 2-失败")
    private Integer status;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "退款用户id")
    private Long userId;

    @Schema(description = "关键词（退款单号/订单号模糊匹配）")
    private String keyword;

    @Schema(description = "创建时间起")
    private LocalDateTime beginCreateTime;

    @Schema(description = "创建时间止")
    private LocalDateTime endCreateTime;
}
