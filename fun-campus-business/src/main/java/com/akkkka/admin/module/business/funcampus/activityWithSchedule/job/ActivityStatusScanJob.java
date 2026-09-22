package com.akkkka.admin.module.business.funcampus.activityWithSchedule.job;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.manager.ActivityStatusCacheManager;
import com.akkkka.module.support.job.core.SmartJob;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动状态扫描任务（大任务）
 * 低频执行：扫描数据库中当天有关键时间点（报名开始/结束、活动开始/结束）的活动，
 * 全量重建 Redis 名单，供 ActivityStatusUpdateJob（小任务）高频消费。
 * 缓存只是加速提示：更新任务在名单缺失或过期时会自行回源重建，本任务失败不会导致状态停更。
 *
 * @Author your-name
 * @Date 2025-09-07
 * @Copyright your-copyright
 */
@Service
public class ActivityStatusScanJob implements SmartJob {

    @Resource
    private ActivityStatusCacheManager activityStatusCacheManager;

    /**
     * 扫描任务：重建当天关键活动名单
     *
     * @param param 可选参数
     * @return 执行结果描述
     */
    @Override
    public String run(String param) {
        List<Long> activityIds = activityStatusCacheManager.rebuild();
        return String.format("扫描完成：当天有关键时间点的活动共%d个，名单已重建", activityIds.size());
    }
}
