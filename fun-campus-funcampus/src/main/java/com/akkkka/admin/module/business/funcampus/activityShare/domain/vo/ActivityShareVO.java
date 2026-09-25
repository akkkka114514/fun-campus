package com.akkkka.admin.module.business.funcampus.activityShare.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 活动分享链接 VO
 *
 * @Author akkkka114514
 * @Date 2026-08-16
 * @Copyright akkkka114514
 */

@Data
public class ActivityShareVO {

    @Schema(description = "分享链接")
    private String shareUrl;

    @Schema(description = "分享Token")
    private String shareToken;

    @Schema(description = "过期时间(秒)")
    private Long expireSeconds;
}
