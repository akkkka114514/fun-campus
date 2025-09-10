package net.lab1024.sa.admin.module.system.login.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.swagger.SchemaEnum;

import java.io.Serializable;

/**
 * 请求后台用户登录信息
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2021/8/4 21:15
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
public class RequestBackendUser implements RequestUser, Serializable {

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