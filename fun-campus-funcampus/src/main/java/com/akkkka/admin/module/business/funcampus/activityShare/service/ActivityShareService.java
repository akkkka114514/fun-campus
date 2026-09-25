package com.akkkka.admin.module.business.funcampus.activityShare.service;

import com.akkkka.admin.module.business.funcampus.activityShare.domain.vo.ActivityShareVO;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.service.ActivityValidator;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 活动分享 Service
 *
 * @Author akkkka114514
 * @Date 2026-08-16
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivityShareService {

    private final ActivityValidator activityValidator;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SHARE_TOKEN_REDIS_KEY_PREFIX = "activity:share:link:";
    private static final long SHARE_TOKEN_EXPIRE_SECONDS = 7 * 24 * 60 * 60;

    @Value("${activity.share.base-url:http://localhost:1024}")
    private String shareBaseUrl;

    /**
     * 生成活动分享链接
     */
    public ActivityShareVO generateShareLink(Long activityId) {
        activityValidator.validateActivityId(activityId);

        String token = UUID.randomUUID().toString().replace("-", "");
        String redisKey = SHARE_TOKEN_REDIS_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(redisKey, activityId, SHARE_TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        String shareUrl = shareBaseUrl + "/activity/share/" + token;

        ActivityShareVO vo = new ActivityShareVO();
        vo.setShareUrl(shareUrl);
        vo.setShareToken(token);
        vo.setExpireSeconds(SHARE_TOKEN_EXPIRE_SECONDS);

        log.info("ActivityShareService.generateShareLink success: activityId={}, token={}", activityId, token);
        return vo;
    }

    /**
     * 根据分享Token解析活动ID
     */
    public Long resolveShareToken(String token) {
        String redisKey = SHARE_TOKEN_REDIS_KEY_PREFIX + token;
        Object activityId = redisTemplate.opsForValue().get(redisKey);
        if (activityId == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "分享链接已过期或无效");
        }
        return ((Number) activityId).longValue();
    }
}
