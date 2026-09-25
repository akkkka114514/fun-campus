package com.akkkka.admin.module.business.funcampus.payment.domain.dto;

import lombok.Data;

/**
 * 渠道退款受理结果
 * <p>
 * 调用渠道 refund 接口的同步返回：受理成功仅代表渠道已接受退款请求，
 * 最终到账结果以渠道异步退款回调（handleRefundNotify）为准
 *
 * author:akkkka114514
 * create at 2026-09-24
 */
@Data
public class RefundChannelResult {

    /** 渠道是否受理成功 */
    private boolean accepted;

    /** 渠道退款单号（受理成功时有值） */
    private String channelRefundNo;

    /** 受理失败原因（accepted=false 时有值） */
    private String failReason;
}
