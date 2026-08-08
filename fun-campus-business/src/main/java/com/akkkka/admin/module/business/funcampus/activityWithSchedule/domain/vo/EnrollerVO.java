package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import lombok.Data;

/**
 * author:akkkka114514
 * create at 2026-03-15 12:24
 */
@Data
public class EnrollerVO extends SimplePortalUserVO {
    private boolean signInStatus;
    private boolean activityManagerFlag;
    private boolean signinManagerFlag;
}
