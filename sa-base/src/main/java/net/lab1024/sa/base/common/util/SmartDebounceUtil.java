package net.lab1024.sa.base.common.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 接口防抖工具类
 * 基于Redis实现分布式环境下的接口防抖
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2025-09-07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Component
public class SmartDebounceUtil {

    /**
     * 防抖前缀
     */
    private static final String DEBOUNCE_PREFIX = "debounce:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 执行防抖操作 - 默认1秒内不能重复执行
     *
     * @param key 防抖标识key
     * @return true: 可以执行, false: 被防抖拦截
     */
    public boolean tryAcquire(String key) {
        return tryAcquire(key, 1, TimeUnit.SECONDS);
    }

    /**
     * 执行防抖操作 - 自定义防抖时间
     *
     * @param key 防抖标识key
     * @param timeout 防抖时间
     * @param unit 时间单位
     * @return true: 可以执行, false: 被防抖拦截
     */
    public boolean tryAcquire(String key, long timeout, TimeUnit unit) {
        String redisKey = DEBOUNCE_PREFIX + key;
        Boolean result = redisTemplate.opsForValue().setIfAbsent(redisKey, System.currentTimeMillis(), timeout, unit);
        return result != null && result;
    }

    /**
     * 执行防抖操作 - 基于用户ID的防抖
     *
     * @param userId 用户ID
     * @param operation 操作标识
     * @return true: 可以执行, false: 被防抖拦截
     */
    public boolean tryAcquire(Long userId, String operation) {
        return tryAcquire(userId + ":" + operation, 1, TimeUnit.SECONDS);
    }

    /**
     * 执行防抖操作 - 基于用户ID和业务ID的防抖
     *
     * @param userId 用户ID
     * @param operation 操作标识
     * @param bizId 业务ID
     * @return true: 可以执行, false: 被防抖拦截
     */
    public boolean tryAcquire(Long userId, String operation, Long bizId) {
        return tryAcquire(userId + ":" + operation + ":" + bizId, 1, TimeUnit.SECONDS);
    }

    /**
     * 执行防抖操作 - 基于用户ID和业务ID的防抖（自定义时间）
     *
     * @param userId 用户ID
     * @param operation 操作标识
     * @param bizId 业务ID
     * @param timeout 防抖时间
     * @param unit 时间单位
     * @return true: 可以执行, false: 被防抖拦截
     */
    public boolean tryAcquire(Long userId, String operation, Long bizId, long timeout, TimeUnit unit) {
        return tryAcquire(userId + ":" + operation + ":" + bizId, timeout, unit);
    }

    /**
     * 清除防抖记录
     *
     * @param key 防抖标识key
     */
    public void clear(String key) {
        String redisKey = DEBOUNCE_PREFIX + key;
        redisTemplate.delete(redisKey);
    }

    /**
     * 清除基于用户ID的防抖记录
     *
     * @param userId 用户ID
     * @param operation 操作标识
     */
    public void clear(Long userId, String operation) {
        clear(userId + ":" + operation);
    }

    /**
     * 清除基于用户ID和业务ID的防抖记录
     *
     * @param userId 用户ID
     * @param operation 操作标识
     * @param bizId 业务ID
     */
    public void clear(Long userId, String operation, Long bizId) {
        clear(userId + ":" + operation + ":" + bizId);
    }
}