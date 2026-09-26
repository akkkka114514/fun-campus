package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.SimplePortalUserVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2026-03-15 12:24
 */
@Data
public class EnrollerVO extends SimplePortalUserVO {
    @Schema(description = "是否已签到")
    private boolean signInStatus;

    @Schema(description = "是否为活动管理员")
    private boolean activityManagerFlag;

    @Schema(description = "是否为签到管理员")
    private boolean signinManagerFlag;
}
