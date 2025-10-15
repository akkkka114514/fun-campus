package net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.lab1024.sa.base.common.swagger.SchemaEnum;
import net.lab1024.sa.base.common.validator.enumeration.CheckEnum;
import net.lab1024.sa.base.constant.LoginDeviceEnum;
import org.hibernate.validator.constraints.Length;

/**
 * author:akkkka114514
 * create at 2025-10-10 10:12
 */
@Data
public class PortalLoginForm {

    @Schema(description = "登录账号")
    @NotBlank(message = "登录账号不能为空")
    @Length(max = 30, message = "登录账号最多30字符")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @SchemaEnum(desc = "登录终端", value = LoginDeviceEnum.class)
    @CheckEnum(value = LoginDeviceEnum.class, required = true, message = "此终端不允许登录")
    private Integer loginDevice;
}
