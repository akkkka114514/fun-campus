package net.lab1024.sa.base.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import jakarta.annotation.Resource;
import net.lab1024.sa.base.module.support.cache.CacheService;
import net.lab1024.sa.base.module.support.cache.CaffeineCacheServiceImpl;
import net.lab1024.sa.base.module.support.cache.RedisCacheServiceImpl;
import net.lab1024.sa.base.module.support.redis.CustomRedisCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * 缓存配置
 *
 */
@Configuration
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    private static final String REDIS_CACHE = "redis";
    private static final String CAFFEINE_CACHE = "caffeine";


    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 创建自定义Redis缓存管理器Bean 整合spring-cache
     * Redis连接工厂，用于建立与Redis服务器的连接
     *
     * @return CustomRedisCacheManager Redis缓存管理器实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.cache", name = {"type"}, havingValue = REDIS_CACHE)
    public CustomRedisCacheManager cacheManager() {
        logger.info("Initializing CustomRedisCacheManager");
        // 使用非阻塞模式的缓存写入器，适用于大多数高并发场景
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);

        // 构建默认缓存配置
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                // 禁止缓存 null 值，避免缓存穿透
                .disableCachingNullValues()
                .computePrefixWith(name -> "cache:" + name + ":")
                // 使用 FastJSON 序列化缓存值，支持复杂对象
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericFastJsonRedisSerializer()));

        // 返回自定义缓存管理器，支持 cacheName#ttl 格式与永久缓存（#-1）
        CustomRedisCacheManager cacheManager = new CustomRedisCacheManager(redisCacheWriter, defaultCacheConfig);
        logger.info("CustomRedisCacheManager initialized successfully");
        return cacheManager;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.cache", name = {"type"}, havingValue = REDIS_CACHE)
    public CacheService redisCacheService() {
        logger.info("Initializing RedisCacheService");
        RedisCacheServiceImpl cacheService = new RedisCacheServiceImpl();
        logger.info("RedisCacheService initialized successfully");
        return cacheService;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.cache", name = {"type"}, havingValue = CAFFEINE_CACHE)
    public CacheService caffeineCacheService() {
        logger.info("Initializing CaffeineCacheService");
        CaffeineCacheServiceImpl cacheService = new CaffeineCacheServiceImpl();
        logger.info("CaffeineCacheService initialized successfully");
        return cacheService;
    }

}