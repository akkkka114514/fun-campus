package com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import com.akkkka.module.support.redis.RedisService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

/**
 * 首页活动列表 缓存管理器
 * 语义约定：缓存只做加速，数据库才是事实。
 * - Cache-Aside：读未命中回源查库并回填；写路径（内容更改/状态流转/删除）整体失效；
 * - 版本号失效：缓存 key 携带版本号，失效只需更换版本号，旧代缓存不再被读取，随 TTL 自然回收，
 *   避免 SCAN 批量删 key 的复杂度与开销；
 * - 防击穿：未命中时用 SETNX 锁保证单请求回源重建（double-check），抢不到锁短暂等待后重读；
 * - 防穿透：空结果短 TTL 缓存，无匹配数据时同样受保护；
 * - Redis 故障全降级：读、写、加锁任意异常均直接回源查库，业务不因缓存故障受损；
 * - 失效失败仅记录日志，由 TTL 兜底（与项目既有“写入端无条件尽力”策略一致）
 *
 * @Author akkkka114514
 * @Date 2025-10-12
 * @Copyright akkkka114514
 */
@Slf4j
@Service
public class IndexActivityCacheManager {

    /**
     * 缓存 key 前缀
     */
    private static final String CACHE_PREFIX = "funcampus:home_index:";

    /**
     * 版本号 key 后缀：更换版本号即完成整体失效
     */
    private static final String VERSION_KEY_SUFFIX = "version";

    /**
     * 版本号有效期（秒）：防止版本 key 被回收后无人初始化；轮换由每次失效刷新
     */
    private static final long VERSION_KEY_EXPIRE_SECONDS = 30 * 24 * 3600L;

    /**
     * 重建锁 key 后缀
     */
    private static final String LOCK_KEY_SUFFIX = ":rebuild_lock";

    /**
     * 重建锁过期时间（毫秒）：锁持有者回源耗时上限，超时后其他请求可接管重建
     */
    private static final long LOCK_EXPIRE_MILLIS = 3000;

    /**
     * 抢不到锁时的等待重读次数与间隔（毫秒）
     */
    private static final int LOCK_WAIT_TIMES = 3;
    private static final long LOCK_WAIT_INTERVAL_MILLIS = 80;

    /**
     * 缓存有效期（秒）：正常结果 120s；空结果 30s 防穿透
     */
    private static final long CACHE_EXPIRE_SECONDS = 120;
    private static final long EMPTY_CACHE_EXPIRE_SECONDS = 30;

    @Resource
    private RedisService redisService;

    /**
     * 读缓存，未命中回源查库并回填
     *
     * @param scopeKey   数据范围标识（本校 school:{schoolId} / 全局 global）
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @param categoryId 分类筛选（null 表示不限）
     * @param status     状态筛选（null 表示默认过滤）
     * @param timeRange  活动开始时间范围筛选（null 表示不限）
     * @param loader     回源查询
     * @return 分页结果
     */
    public Page<ActivityWithScheduleVO> getOrLoad(String scopeKey, long pageNum, long pageSize,
                                                  Long categoryId, Integer status, Integer timeRange,
                                                  Supplier<Page<ActivityWithScheduleVO>> loader) {
        // 版本号读取失败说明 Redis 不可用：整体降级查库
        Long version = currentVersionQuietly();
        if (version == null) {
            return loader.get();
        }
        String key = buildKey(version, scopeKey, pageNum, pageSize, categoryId, status, timeRange);
        CachedIndexPage cached = readQuietly(key);
        if (cached != null) {
            return toPage(cached, pageNum, pageSize);
        }
        // 未命中：SETNX 抢锁单飞重建，防击穿；加锁异常同样降级查库
        String lockKey = key + LOCK_KEY_SUFFIX;
        Boolean locked = lockQuietly(lockKey);
        if (locked == null) {
            return loader.get();
        }
        if (locked) {
            try {
                // double-check：等待锁期间可能已由其他请求完成重建
                cached = readQuietly(key);
                if (cached != null) {
                    return toPage(cached, pageNum, pageSize);
                }
                Page<ActivityWithScheduleVO> page = loader.get();
                writeQuietly(key, page);
                return page;
            } finally {
                unLockQuietly(lockKey);
            }
        }
        // 未抢到锁：短暂等待重建完成后重读
        for (int i = 0; i < LOCK_WAIT_TIMES; i++) {
            sleepQuietly();
            cached = readQuietly(key);
            if (cached != null) {
                return toPage(cached, pageNum, pageSize);
            }
        }
        // 等待仍未命中：降级查库（不写缓存，避免绕过单飞重建）
        return loader.get();
    }

    /**
     * 整体失效：更换版本号，旧代缓存不再被读取，随 TTL 自然过期回收
     * 失败仅记录日志，由 TTL 兜底
     */
    public void evictAll() {
        try {
            bumpVersion();
        } catch (Exception e) {
            log.warn("首页活动缓存整体失效失败，将由 TTL 兜底", e);
        }
    }

    /**
     * 更换版本号：新版本号取当前毫秒时间戳并保证严格递增
     * （系统时钟精度内同毫秒连续失效时，旧值 +1 兜底，避免后一次失效丢失）
     */
    private void bumpVersion() {
        long version = System.currentTimeMillis();
        String current = redisService.get(versionKey());
        if (current != null) {
            try {
                long oldVersion = Long.parseLong(current);
                if (version <= oldVersion) {
                    version = oldVersion + 1;
                }
            } catch (NumberFormatException ignore) {
                // 版本号损坏：直接用新时间戳覆盖
            }
        }
        String versionText = String.valueOf(version);
        redisService.set(versionKey(), versionText, VERSION_KEY_EXPIRE_SECONDS);
    }

    /**
     * 读取当前版本号；缺失时初始化写入；Redis 异常返回 null 表示整体降级
     */
    private Long currentVersionQuietly() {
        try {
            String current = redisService.get(versionKey());
            if (current != null) {
                try {
                    return Long.parseLong(current);
                } catch (NumberFormatException ignore) {
                    // 版本号损坏：重新初始化
                }
            }
            long version = System.currentTimeMillis();
            String versionText = String.valueOf(version);
            redisService.set(versionKey(), versionText, VERSION_KEY_EXPIRE_SECONDS);
            return version;
        } catch (Exception e) {
            log.warn("读取首页活动缓存版本号失败，降级直接查库", e);
            return null;
        }
    }

    private String versionKey() {
        return redisService.generateRedisKey(CACHE_PREFIX, VERSION_KEY_SUFFIX);
    }

    /**
     * 构造缓存 key：版本号 + 范围 + 全部筛选参数 + 分页参数，参数值为 null 时记 all
     */
    private String buildKey(long version, String scopeKey, long pageNum, long pageSize,
                            Long categoryId, Integer status, Integer timeRange) {
        String rawKey = String.format("v%d:%s:c%s:s%s:t%s:p%d:n%d",
                version, scopeKey, display(categoryId), display(status), display(timeRange), pageNum, pageSize);
        return redisService.generateRedisKey(CACHE_PREFIX, rawKey);
    }

    private String display(Object value) {
        return value == null ? "all" : String.valueOf(value);
    }

    private CachedIndexPage readQuietly(String key) {
        try {
            return redisService.getObject(key, CachedIndexPage.class);
        } catch (Exception e) {
            log.warn("读取首页活动缓存失败，降级直接查库，key={}", key, e);
            return null;
        }
    }

    private void writeQuietly(String key, Page<ActivityWithScheduleVO> page) {
        try {
            CachedIndexPage cached = CachedIndexPage.of(page);
            long expire = cached.getRecords().isEmpty() ? EMPTY_CACHE_EXPIRE_SECONDS : CACHE_EXPIRE_SECONDS;
            redisService.set(key, cached, expire);
        } catch (Exception e) {
            log.warn("写入首页活动缓存失败，不影响业务，key={}", key, e);
        }
    }

    /**
     * 抢锁；返回 null 表示 Redis 不可用（调用方降级查库）
     */
    private Boolean lockQuietly(String lockKey) {
        try {
            return redisService.getLock(lockKey, LOCK_EXPIRE_MILLIS);
        } catch (Exception e) {
            log.warn("首页活动缓存加锁失败，降级直接查库，key={}", lockKey, e);
            return null;
        }
    }

    private void unLockQuietly(String lockKey) {
        try {
            redisService.unLock(lockKey);
        } catch (Exception e) {
            log.warn("释放首页活动缓存重建锁失败，等待自动过期，key={}", lockKey, e);
        }
    }

    private void sleepQuietly() {
        try {
            Thread.sleep(LOCK_WAIT_INTERVAL_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 重建分页对象：只回填 total 与 records（缓存命中路径与回源路径返回结构一致）
     */
    private Page<ActivityWithScheduleVO> toPage(CachedIndexPage cached, long pageNum, long pageSize) {
        Page<ActivityWithScheduleVO> page = new Page<>(pageNum, pageSize, cached.getTotal());
        page.setRecords(cached.getRecords());
        return page;
    }

    /**
     * 缓存的首页分页数据
     * 只保留渲染必需字段，避免序列化整个 Page 对象（count 优化参数等无关字段不落地）
     */
    @Data
    public static class CachedIndexPage {

        private long total;

        private List<ActivityWithScheduleVO> records;

        public static CachedIndexPage of(Page<ActivityWithScheduleVO> page) {
            CachedIndexPage cached = new CachedIndexPage();
            cached.setTotal(page.getTotal());
            cached.setRecords(page.getRecords() == null ? List.of() : page.getRecords());
            return cached;
        }
    }
}
