package com.akkkka.admin.module.business.funcampus.activityOrder.controller;

import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityOrderQueryForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityRefundApplyForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderCreateVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityOrderService;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityRefundService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 活动报名订单 Controller（门户端）
 * <p>
 * 付费报名链路：创建订单（锁座）→ 收银台支付 → 支付回调写报名记录；
 * 取消订单 / 申请退款（退款到账以渠道回调为准）
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动报名订单")
@RequestMapping("portal")
public class ActivityOrderController {

    @Resource
    private ActivityOrderService activityOrderService;

    @Resource
    private ActivityRefundService activityRefundService;

    @Operation(summary = "创建订单（付费活动下单锁座） @author akkkka114514")
    @PostMapping("/activityOrder/create")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<ActivityOrderCreateVO> createOrder(@RequestBody Long activityId) {
        return ResponseDTO.ok(activityOrderService.createOrder(activityId));
    }

    @Operation(summary = "重新获取支付参数（拉起收银台） @author akkkka114514")
    @GetMapping("/activityOrder/prepay")
    public ResponseDTO<ActivityOrderCreateVO> prepay(@RequestParam String orderNo) {
        return ResponseDTO.ok(activityOrderService.prepay(orderNo));
    }

    @Operation(summary = "取消待支付订单 @author akkkka114514")
    @PostMapping("/activityOrder/cancel")
    public ResponseDTO<Void> cancelOrder(@RequestParam String orderNo) {
        activityOrderService.cancelOrder(orderNo);
        return ResponseDTO.ok();
    }

    @Operation(summary = "申请退款 @author akkkka114514")
    @PostMapping("/activityOrder/refundApply")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> refundApply(@RequestBody @Valid ActivityRefundApplyForm form) {
        return ResponseDTO.ok(activityRefundService.refundApply(form));
    }

    @Operation(summary = "订单详情 @author akkkka114514")
    @GetMapping("/activityOrder/detail")
    public ResponseDTO<ActivityOrderVO> detail(@RequestParam String orderNo) {
        return ResponseDTO.ok(activityOrderService.detail(orderNo));
    }

    @Operation(summary = "分页查询我的订单 @author akkkka114514")
    @PostMapping("/activityOrder/query")
    public ResponseDTO<PageResult<ActivityOrderVO>> query(@RequestBody @Valid ActivityOrderQueryForm queryForm) {
        return ResponseDTO.ok(activityOrderService.queryPage(queryForm));
    }
}
