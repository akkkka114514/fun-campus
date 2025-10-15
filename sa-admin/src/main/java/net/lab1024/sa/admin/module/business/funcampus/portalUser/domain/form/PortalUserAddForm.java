package net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 前端用户 新建表单
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */

@Data
public class PortalUserAddForm {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名 不能为空")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码 不能为空")
    private String password;

    @Schema(description = "性别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "性别 不能为空")
    private Boolean gender;

    @Schema(description = "学校名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学校名 不能为空")
    private String schoolName;

    @Schema(description = "学院名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学院名 不能为空")
    private String collegeName;

}