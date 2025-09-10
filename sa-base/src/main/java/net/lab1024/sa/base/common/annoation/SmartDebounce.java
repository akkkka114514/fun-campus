package net.lab1024.sa.base.common.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 接口防抖注解
 * 用于防止重复提交或频繁触发相同操作
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2025-09-07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartDebounce {

    /**
     * 防抖key的表达式，支持SpEL表达式
     * 默认使用方法名作为key
     */
    String key() default "";

    /**
     * 防抖时间
     */
    long timeout() default 1;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 是否基于用户ID防抖
     * 如果为true，则会自动拼接当前用户ID到key中
     */
    boolean userSpecific() default true;

    /**
     * 是否基于请求参数防抖
     * 如果为true，则会自动拼接请求参数的hash值到key中
     */
    boolean paramSpecific() default false;
}