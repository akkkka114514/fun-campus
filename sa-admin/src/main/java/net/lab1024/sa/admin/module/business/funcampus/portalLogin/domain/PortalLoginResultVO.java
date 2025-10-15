package net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import net.lab1024.sa.admin.module.system.menu.domain.vo.MenuVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * author:akkkka114514
 * create at 2025-10-10 10:12
 */
public class PortalLoginResultVO {
    @Schema(description = "token")
    private String token;

    @Schema(description = "上次登录ip")
    private String lastLoginIp;

    @Schema(description = "上次登录ip地区")
    private String lastLoginIpRegion;

    @Schema(description = "上次登录user-agent")
    private String lastLoginUserAgent;

    @Schema(description = "上次登录时间")
    private LocalDateTime lastLoginTime;
}
