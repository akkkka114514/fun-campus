package com.akkkka.admin.module.business.funcampus.activityOrder.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单号/退款单号生成工具
 * <p>
 * 规则：前缀 + yyyyMMddHHmmssSSS + 5位随机数，如 AO2026092415301234500001；
 * 时间戳保证趋势递增，随机位降低同毫秒并发碰撞概率
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */
public class OrderNoUtil {

    /**
     * 订单号前缀
     */
    public static final String ORDER_PREFIX = "AO";

    /**
     * 退款单号前缀
     */
    public static final String REFUND_PREFIX = "AR";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private static final SecureRandom RANDOM = new SecureRandom();

    private OrderNoUtil() {
    }

    public static String generate(String prefix) {
        return prefix + LocalDateTime.now().format(FORMATTER)
                + String.format("%05d", RANDOM.nextInt(100000));
    }
}
