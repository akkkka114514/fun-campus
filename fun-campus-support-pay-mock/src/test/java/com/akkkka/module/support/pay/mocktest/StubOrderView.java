package com.akkkka.module.support.pay.mocktest;

import com.akkkka.module.support.pay.constant.PayChannelEnum;
import com.akkkka.module.support.pay.port.PayOrderView;

import lombok.Data;

/**
 * 测试用订单视图桩（模拟业务实体实现 PayOrderView 后的行为）
 * <p>
 * 只为单元测试提供轻量数据载体：需要哪个字段 set 哪个，
 * 状态语义方法（isWaitPay/isPaid/isClosed/isInRefundFlow）由对应 boolean 标志驱动
 * <p>
 * 本副本服务 pay-mock 模块测试（模块拆分后不依赖核心模块的测试类）；
 * 核心模块同名桩见 fun-campus-support-pay 测试目录
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 */
@Data
public class StubOrderView implements PayOrderView {

    private Long id;

    private String orderNo;

    private Long activityId;

    private Long userId;

    private Integer amountFen;

    private String payeeAccountNo;

    private String payeeName;

    private PayChannelEnum payChannel;

    private String statusText;

    private boolean waitPay;

    private boolean paid;

    private boolean closed;

    private boolean inRefundFlow;
}
