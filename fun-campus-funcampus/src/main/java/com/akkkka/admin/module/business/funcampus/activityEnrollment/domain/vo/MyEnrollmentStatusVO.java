package com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 当前用户报名状态 VO（活动详情页使用，免费付费通用）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class MyEnrollmentStatusVO {

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "是否已报名（付费活动支付成功后视为已报名）")
    private Boolean enrolled;

    @Schema(description = "是否已签到（未报名时为 false）")
    private Boolean signInStatus;

    @Schema(description = "是否已签退（未报名时为 false）")
    private Boolean signOutStatus;
}
