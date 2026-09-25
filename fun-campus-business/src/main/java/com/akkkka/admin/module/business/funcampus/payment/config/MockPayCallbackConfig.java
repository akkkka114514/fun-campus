package com.akkkka.admin.module.business.funcampus.payment.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Mock 支付回调调度配置（仅 pay.channel=mock 时注册）
 * <p>
 * 用于模拟渠道"异步退款通知"的延时投递：调度线程执行回调处理，
 * 不阻塞业务请求线程，也不使用 Thread.sleep
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
@Configuration
@ConditionalOnProperty(name = "pay.channel", havingValue = "mock")
public class MockPayCallbackConfig {

    @Bean(name = "mockPayCallbackScheduler")
    public ThreadPoolTaskScheduler mockPayCallbackScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("mock-pay-callback-");
        scheduler.setDaemon(true);
        scheduler.setWaitForTasksToCompleteOnShutdown(false);
        return scheduler;
    }
}
