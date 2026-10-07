package com.akkkka.module.support.pay.channel;

import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.exception.BusinessException;

/**
 * 渠道熔断快速失败异常
 * <p>
 * 熔断打开（或半开探测限流）期间，出站渠道调用不做实际请求直接抛出，
 * 让调用方立即失败/降级，不再干等渠道超时；继承 BusinessException 以复用全局异常拦截
 *
 * @Author akkkka114514
 * @Date 2026-10-07
 * @Copyright akkkka114514
 */
public class ChannelCircuitOpenException extends BusinessException {

    public ChannelCircuitOpenException(String message) {
        super(SystemErrorCode.SYSTEM_ERROR, message);
    }
}
