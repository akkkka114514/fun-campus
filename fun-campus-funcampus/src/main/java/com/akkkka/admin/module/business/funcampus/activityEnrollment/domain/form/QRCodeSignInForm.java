package com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 扫码签到/签退 请求表单
 *
 * @Author akkkka114514
 * @Date 2026-08-12
 * @Copyright akkkka114514
 */

@Data
public class QRCodeSignInForm {

    @NotNull(message = "活动ID不能为空")
    @Schema(description = "活动ID")
    private Long activityId;

    @NotNull(message = "被签到/签退用户ID不能为空")
    @Schema(description = "被签到/签退的用户ID（从二维码扫描获取）")
    private Long targetUserId;

    @NotNull(message = "二维码Token不能为空")
    @Schema(description = "二维码Token（签到员生成二维码时生成的Token）")
    private String token;
}
