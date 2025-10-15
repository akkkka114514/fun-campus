package net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.swagger.SchemaEnum;

import java.io.Serializable;

/**
 * author:akkkka114514
 * create at 2025-10-15 10:24
 */
@Data
public class RequestPortalUser implements RequestUser, Serializable {

    @Schema(description = "用户id")
    private Long id;

    @SchemaEnum(UserTypeEnum.class)
    private UserTypeEnum userType;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "是否禁用")
    private Boolean deletedFlag;

    @Schema(description = "请求ip")
    private String ip;

    @Schema(description = "请求user-agent")
    private String userAgent;

    @Override
    public Long getUserId() {
        return id;
    }

    @Override
    public String getUserName() {
        return username;
    }
}
