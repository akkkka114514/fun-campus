package net.lab1024.sa.admin.module.system.backendUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * author:akkkka114514
 * create at 2025-09-09 18:52
 */
@Data
public class BackendUserUpdatePasswordForm {
    @Schema(hidden = true)
    private Long id;

    @Schema(description = "原密码")
    @NotBlank(message = "原密码不能为空")
    @Length(min = 6, max = 30, message = "原密码长度应为6-30个字符")
    private String oldPassword;

    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    @Length(min = 6, max = 30, message = "新密码长度应为6-30个字符")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+$",
            message = "新密码必须包含大小写字母和数字，可包含特殊字符")
    private String newPassword;

}