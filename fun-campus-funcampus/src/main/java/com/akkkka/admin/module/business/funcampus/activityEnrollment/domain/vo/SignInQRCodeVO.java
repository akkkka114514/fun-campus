package com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 签到/签退二维码 VO
 *
 * @Author akkkka114514
 * @Date 2026-08-12
 * @Copyright akkkka114514
 */

@Data
public class SignInQRCodeVO {

    @Schema(description = "二维码Base64图片")
    private String qrCodeImage;

    @Schema(description = "二维码Token")
    private String token;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "过期时间(秒)")
    private Integer expireSeconds;
}
