package net.lab1024.sa.admin.module.system.backendUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.lab1024.sa.base.common.enumeration.GenderEnum;
import net.lab1024.sa.base.common.swagger.SchemaEnum;
import net.lab1024.sa.base.common.util.SmartVerificationUtil;
import net.lab1024.sa.base.common.validator.enumeration.CheckEnum;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * author:akkkka114514
 * create at 2025-09-09 18:44
 */
@Data
public class BackendUserAddForm {

    @Schema(description = "登录账号")
    @NotBlank(message = "登录账号不能为空")
    @Length(min = 4, max = 30, message = "登录账号长度应为4-30个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "登录账号只能包含字母、数字和下划线")
    private String username;

    @SchemaEnum(GenderEnum.class)
    @CheckEnum(value = GenderEnum.class, message = "性别错误")
    private Integer gender;

    @Schema(description = "是否启用")
    @NotNull(message = "是否被禁用不能为空")
    private Boolean disabledFlag;

    @Schema(description = "邮箱账号")
    @NotBlank(message = "邮箱账号不能为空")
    @Length(max = 100, message = "邮箱长度不能超过100个字符")
    @Pattern(regexp = SmartVerificationUtil.EMAIL, message = "邮箱账号格式不正确")
    private String email;

    @Schema(description = "角色列表")
    @NotNull(message = "角色列表不能为空")
    @Size(min = 1, message = "至少需要选择一个角色")
    private List<Long> roleIdList;

}