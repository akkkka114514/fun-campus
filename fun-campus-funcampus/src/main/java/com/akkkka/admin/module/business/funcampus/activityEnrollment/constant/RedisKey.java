package com.akkkka.admin.module.business.funcampus.activityEnrollment.constant;

/**
 * 活动报名模块 Redis Key 常量
 * <p>
 * 签到/签退二维码一人一码、不绑定活动，Token 按用户维度缓存，30 秒过期
 *
 * @author akkkka114514
 * @Date 2025-11-08 14:24
 */
public class RedisKey {

    /**
     * 签到/签退二维码 Token 缓存前缀
     */
    public static final String QR_CODE_TOKEN_PREFIX = "sign_in:qr_code_token:";

    /**
     * 用户签到/签退二维码 Token 的 Redis Key
     */
    public static String qrCodeTokenKey(Long userId) {
        return QR_CODE_TOKEN_PREFIX + userId;
    }
}
