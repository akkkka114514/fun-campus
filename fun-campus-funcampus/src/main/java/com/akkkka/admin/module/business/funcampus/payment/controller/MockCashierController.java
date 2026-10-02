package com.akkkka.admin.module.business.funcampus.payment.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akkkka.admin.module.business.funcampus.activityOrder.constant.OrderStatus;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.manager.ActivityOrderManager;
import com.akkkka.admin.module.business.funcampus.payment.channel.MockChannelCallbackClient;
import com.akkkka.admin.module.business.funcampus.payment.channel.MockChannelLedger;
import com.akkkka.common.annoation.NoNeedLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

/**
 * 模拟收银台 Controller（仅 pay.channel=mock 时注册，生产环境不存在）
 * <p>
 * 角色划分与真实渠道一致：
 * - 收银台页面（/cashier）：用户通过 prepay 返回的链接打开，凭渠道预支付单 prepayId 展示；
 * - 渠道受理（/accept）：用户点击支付结果后先由"渠道"受理（标记渠道侧支付状态），
 *   再由渠道调度线程异步投递回调到统一入口 /portal/payment/callback/mock（带签名、支持重投），
 *   浏览器不再直接提交回调，与真实"渠道服务器发通知"的链路对齐；
 * - 支持模拟「支付成功但回调丢失」：只标记渠道侧已支付、不投递通知，用于演示超时查单补偿
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

    @Resource
    private MockChannelLedger channelLedger;

    @Resource
    private MockChannelCallbackClient callbackClient;

    /**
     * 模拟渠道回调延时（秒）
     */
    @Value("${pay.mock.callback-delay-seconds:0}")
    private long callbackDelaySeconds;

    @Operation(summary = "模拟收银台页面 @author akkkka114514")
    @GetMapping(value = "/cashier", produces = "text/html;charset=UTF-8")
    @NoNeedLogin
    public String cashier(@RequestParam(required = false) String prepayId) {
        if (prepayId == null || prepayId.isBlank()) {
            return simplePage("链接无效", "请返回订单详情页重新发起支付");
        }
        MockChannelLedger.MockPrepayOrder prepay = channelLedger.getByPrepayId(prepayId);
        if (prepay == null) {
            return simplePage("预支付单不存在", "链接已失效（模拟渠道数据在服务重启后清空），请返回订单详情页重新发起支付");
        }
        ActivityOrderEntity order = orderManager.getByOrderNo(prepay.getOrderNo());
        if (order == null) {
            return simplePage("订单不存在", "请确认订单号后重试（订单可能已被删除）");
        }
        if (prepay.getPayTime() != null) {
            return simplePage("该笔支付已完成", "渠道侧已完成收款，请返回订单详情页查看结果");
        }
        if (order.getStatus() != OrderStatus.WAIT_PAY) {
            return simplePage("订单不可支付", "当前订单状态：" + order.getStatus().getLabel() + "，请返回订单详情页");
        }
        return cashierPage(prepay.getPrepayId(), order.getOrderNo(), order.getAmountFen());
    }

    @Operation(summary = "模拟渠道受理支付结果 @author akkkka114514")
    @PostMapping(value = "/accept", produces = "text/html;charset=UTF-8")
    @NoNeedLogin
    public String accept(@RequestParam String prepayId, @RequestParam String payResult) {
        MockChannelLedger.MockPrepayOrder prepay = channelLedger.getByPrepayId(prepayId);
        if (prepay == null) {
            return simplePage("受理失败", "预支付单不存在或已失效，请返回订单详情页重新发起支付");
        }
        ActivityOrderEntity order = orderManager.getByOrderNo(prepay.getOrderNo());
        if (order == null) {
            return simplePage("受理失败", "订单不存在");
        }
        return switch (payResult) {
            case "SUCCESS" -> acceptPaySuccess(prepay, order);
            case "FAIL" -> acceptPayFail(prepay, order);
            case "LOST" -> acceptPayLost(prepay, order);
            default -> simplePage("受理失败", "未知的支付结果：" + payResult);
        };
    }

    /**
     * 受理支付成功：渠道簿标记已支付 → 异步投递回调（带签名、失败重投）
     */
    private String acceptPaySuccess(MockChannelLedger.MockPrepayOrder prepay, ActivityOrderEntity order) {
        if (!channelLedger.markPaid(prepay.getPrepayId())) {
            return simplePage("渠道已受理", "该预支付单已完成支付，请勿重复操作，可返回订单详情页查看结果");
        }
        callbackClient.deliverPayNotify(order.getOrderNo(), order.getAmountFen(), true,
                prepay.getChannelOrderNo(), callbackDelaySeconds);
        return simplePage("支付成功（模拟）",
                "模拟渠道已受理支付成功<br>"
                        + "异步通知将在 " + callbackDelaySeconds + " 秒后投递给平台（未获成功应答会自动重投）<br>"
                        + "请返回订单详情页查看最终结果");
    }

    /**
     * 受理支付失败：投递失败通知（平台记录留痕、订单状态不变）
     */
    private String acceptPayFail(MockChannelLedger.MockPrepayOrder prepay, ActivityOrderEntity order) {
        callbackClient.deliverPayNotify(order.getOrderNo(), order.getAmountFen(), false,
                null, callbackDelaySeconds);
        return simplePage("支付失败（模拟）",
                "模拟渠道已通知平台支付失败<br>订单保持待支付，您可以在订单超时前重新支付");
    }

    /**
     * 模拟「支付成功但回调丢失」：只标记渠道侧已支付、不投递任何通知，
     * 订单将保持待支付，等待超时查单补偿任务发现并入账
     */
    private String acceptPayLost(MockChannelLedger.MockPrepayOrder prepay, ActivityOrderEntity order) {
        if (!channelLedger.markPaid(prepay.getPrepayId())) {
            return simplePage("渠道已受理", "该预支付单已完成支付，请勿重复操作");
        }
        return simplePage("已模拟回调丢失",
                "渠道侧已收款成功，但本次不会投递任何异步通知（模拟渠道回调丢失）<br>"
                        + "订单将保持待支付，在订单超时后由查单补偿任务完成入账");
    }

    /**
     * 收银台页面：三个按钮分别模拟「支付成功 / 支付失败 / 支付成功但回调丢失」
     */
    private String cashierPage(String prepayId, String orderNo, Integer amountFen) {
        String amount = BigDecimal.valueOf(amountFen).movePointLeft(2).toPlainString();
        return "<!DOCTYPE html>"
                + "<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">"
                + "<title>模拟收银台</title><style>"
                + "body{font-family:-apple-system,'Segoe UI',sans-serif;display:flex;justify-content:center;padding-top:60px;background:#f5f6f8;}"
                + ".card{background:#fff;border-radius:12px;padding:32px 48px;box-shadow:0 4px 16px rgba(0,0,0,.08);text-align:center;}"
                + ".amount{font-size:32px;color:#e64545;font-weight:bold;margin:16px 0;}"
                + "button{border:none;border-radius:6px;padding:10px 28px;font-size:16px;cursor:pointer;margin:0 8px;}"
                + ".ok{background:#07c160;color:#fff;}.fail{background:#eee;color:#333;}"
                + ".lost{background:#fff3e0;color:#b26a00;font-size:13px;padding:8px 18px;}"
                + ".tip{color:#999;font-size:12px;margin-top:16px;}"
                + ".lost-tip{color:#b26a00;font-size:12px;margin-top:10px;}"
                + "</style></head><body><div class=\"card\">"
                + "<h2>模拟收银台</h2>"
                + "<div>订单号：" + orderNo + "</div>"
                + "<div class=\"amount\">¥" + amount + "</div>"
                + "<form method=\"post\" action=\"/portal/payment/mock/accept\" style=\"display:inline\">"
                + hiddenInputs(prepayId, "SUCCESS")
                + "<button class=\"ok\" type=\"submit\">支付成功</button></form>"
                + "<form method=\"post\" action=\"/portal/payment/mock/accept\" style=\"display:inline\">"
                + hiddenInputs(prepayId, "FAIL")
                + "<button class=\"fail\" type=\"submit\">支付失败</button></form>"
                + "<div class=\"lost-tip\">— 以下为测试场景 —</div>"
                + "<form method=\"post\" action=\"/portal/payment/mock/accept\" style=\"display:inline\">"
                + hiddenInputs(prepayId, "LOST")
                + "<button class=\"lost\" type=\"submit\">支付成功但回调丢失（模拟）</button></form>"
                + "<div class=\"tip\">仅开发/测试环境可用（pay.channel=mock）：点击后由渠道受理并异步投递回调</div>"
                + "</div></body></html>";
    }

    private String hiddenInputs(String prepayId, String payResult) {
        return "<input type=\"hidden\" name=\"prepayId\" value=\"" + prepayId + "\">"
                + "<input type=\"hidden\" name=\"payResult\" value=\"" + payResult + "\">";
    }

    private String simplePage(String title, String message) {
        return "<!DOCTYPE html>"
                + "<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>" + title + "</title></head>"
                + "<body style=\"font-family:sans-serif;text-align:center;padding-top:80px;background:#f5f6f8;\">"
                + "<h2>" + title + "</h2><p>" + message + "</p>"
                + "</body></html>";
    }
}
