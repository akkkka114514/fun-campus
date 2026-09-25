package com.akkkka.admin.module.business.funcampus.payment.controller;

import java.math.BigDecimal;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.common.annoation.NoNeedLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟收银台 Controller（仅 pay.channel=mock 时注册，生产环境不存在）
 * <p>
 * 模拟第三方收银台页面：用户点击「支付成功/支付失败」后，
 * 浏览器表单 POST 到统一回调入口 /portal/payment/callback/mock，
 * 完整模拟「渠道异步回调」链路（而非服务端同步返回），
 * 与真实渠道共用同一回调处理逻辑
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "模拟收银台（mock渠道）")
@RequestMapping("portal/payment/mock")
@ConditionalOnProperty(name = "pay.channel", havingValue = "mock")
public class MockCashierController {

    @Resource
    private ActivityOrderManager orderManager;

    @Operation(summary = "模拟收银台页面 @author akkkka114514")
    @GetMapping(value = "/cashier", produces = "text/html;charset=UTF-8")
    @NoNeedLogin
    public String cashier(@RequestParam String orderNo) {
        ActivityOrderEntity order = orderManager.getByOrderNo(orderNo);
        if (order == null) {
            return simplePage("订单不存在", "请确认订单号后重试（订单可能已被删除）");
        }
        if (order.getStatus() != OrderStatus.WAIT_PAY) {
            return simplePage("订单不可支付", "当前订单状态：" + order.getStatus().getLabel() + "，请返回查看订单详情");
        }
        String amount = BigDecimal.valueOf(order.getAmountFen()).movePointLeft(2).toPlainString();
        return cashierPage(order.getOrderNo(), order.getAmountFen(), amount);
    }

    /**
     * 收银台页面：两个按钮分别以 SUCCESS / FAIL 回调统一入口
     */
    private String cashierPage(String orderNo, Integer amountFen, String amount) {
        return "<!DOCTYPE html>"
                + "<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">"
                + "<title>模拟收银台</title><style>"
                + "body{font-family:-apple-system,'Segoe UI',sans-serif;display:flex;justify-content:center;padding-top:60px;background:#f5f6f8;}"
                + ".card{background:#fff;border-radius:12px;padding:32px 48px;box-shadow:0 4px 16px rgba(0,0,0,.08);text-align:center;}"
                + ".amount{font-size:32px;color:#e64545;font-weight:bold;margin:16px 0;}"
                + "button{border:none;border-radius:6px;padding:10px 28px;font-size:16px;cursor:pointer;margin:0 8px;}"
                + ".ok{background:#07c160;color:#fff;}.fail{background:#eee;color:#333;}"
                + ".tip{color:#999;font-size:12px;margin-top:16px;}"
                + "</style></head><body><div class=\"card\">"
                + "<h2>模拟收银台</h2>"
                + "<div>订单号：" + orderNo + "</div>"
                + "<div class=\"amount\">¥" + amount + "</div>"
                + "<form method=\"post\" action=\"/portal/payment/callback/mock\" style=\"display:inline\">"
                + "<input type=\"hidden\" name=\"orderNo\" value=\"" + orderNo + "\">"
                + "<input type=\"hidden\" name=\"amountFen\" value=\"" + amountFen + "\">"
                + "<input type=\"hidden\" name=\"payStatus\" value=\"SUCCESS\">"
                + "<button class=\"ok\" type=\"submit\">支付成功</button></form>"
                + "<form method=\"post\" action=\"/portal/payment/callback/mock\" style=\"display:inline\">"
                + "<input type=\"hidden\" name=\"orderNo\" value=\"" + orderNo + "\">"
                + "<input type=\"hidden\" name=\"amountFen\" value=\"" + amountFen + "\">"
                + "<input type=\"hidden\" name=\"payStatus\" value=\"FAIL\">"
                + "<button class=\"fail\" type=\"submit\">支付失败</button></form>"
                + "<div class=\"tip\">仅开发/测试环境可用（pay.channel=mock）</div>"
                + "</div></body></html>";
    }

    private String simplePage(String title, String message) {
        return "<!DOCTYPE html>"
                + "<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>" + title + "</title></head>"
                + "<body style=\"font-family:sans-serif;text-align:center;padding-top:80px;background:#f5f6f8;\">"
                + "<h2>" + title + "</h2><p>" + message + "</p>"
                + "</body></html>";
    }
}
