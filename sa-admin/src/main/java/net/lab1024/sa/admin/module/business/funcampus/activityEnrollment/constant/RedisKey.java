package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.constant;

/**
 * author:akkkka114514
 * create at 2025-11-08 14:24
 */
public class RedisKey {
    public static final String SIGN_IN_CODE_PREFIX = "sign_in_code:";

    public static String SIGN_IN_CODE_KEY(Long userId,Long activityId) {
        return SIGN_IN_CODE_PREFIX + userId + ":" + activityId;
    }
}
