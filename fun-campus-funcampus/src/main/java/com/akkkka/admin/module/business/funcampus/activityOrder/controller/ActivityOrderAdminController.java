package com.akkkka.admin.module.business.funcampus.activityOrder.controller;

import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityOrderQueryForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityRefundQueryForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityRefundVO;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityOrderService;
import com.akkkka.admin.module.business.funcampus.activityOrder.service.ActivityRefundService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 活动报名订单 Controller（管理端）
 * <p>
 * 请求路径包含 backend 段：由 AdminInterceptor 解析为管理端登录用户；
 * 订单查询只读，退款重试复用用户申请同一套 CAS 链路（新建退款单，保留原单凭证）
 *
 * @Author akkkka114514
 * @Date 2026-09-26
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动订单（管理端）")
@RequestMapping("backend")
public class ActivityOrderAdminController {

    @Resource
    private ActivityOrderService activityOrderService;

    @Resource
    private ActivityRefundService activityRefundService;

    @Operation(summary = "订单分页查询（管理端全量） @author akkkka114514")
    @PostMapping("/activityOrder/queryPage")
    @SaCheckPermission("activityOrder:query")
    public ResponseDTO<PageResult<ActivityOrderVO>> queryPage(@RequestBody @Valid ActivityOrderQueryForm queryForm) {
        return ResponseDTO.ok(activityOrderService.queryPageForAdmin(queryForm));
    }

    @Operation(summary = "订单详情（管理端） @author akkkka114514")
    @GetMapping("/activityOrder/detail")
    @SaCheckPermission("activityOrder:query")
    public ResponseDTO<ActivityOrderVO> detail(@RequestParam String orderNo) {
        return ResponseDTO.ok(activityOrderService.detailForAdmin(orderNo));
    }

    @Operation(summary = "退款单分页查询（管理端全量） @author akkkka114514")
    @PostMapping("/activityOrder/refundQueryPage")
    @SaCheckPermission("activityRefund:query")
    public ResponseDTO<PageResult<ActivityRefundVO>> refundQueryPage(@RequestBody @Valid ActivityRefundQueryForm queryForm) {
        return ResponseDTO.ok(activityRefundService.queryPageForAdmin(queryForm));
    }

    @Operation(summary = "退款失败重试 @author akkkka114514")
    @PostMapping("/activityOrder/refundRetry")
    @SaCheckPermission("activityRefund:retry")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> refundRetry(@RequestParam String refundNo) {
        return ResponseDTO.ok(activityRefundService.retryRefund(refundNo));
    }
}
