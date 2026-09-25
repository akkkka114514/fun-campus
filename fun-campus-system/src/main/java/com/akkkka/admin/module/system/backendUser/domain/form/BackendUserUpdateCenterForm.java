package com.akkkka.admin.module.system.backendUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import com.akkkka.common.enumeration.GenderEnum;
import com.akkkka.common.swagger.SchemaEnum;
import com.akkkka.common.util.SmartVerificationUtil;
import com.akkkka.common.validator.enumeration.CheckEnum;

/**
 * author:akkkka114514
 * create at 2025-09-09 18:46
 */
@Data
public class BackendUserUpdateCenterForm {

    @Schema(hidden = true)
    private Long id;

    @SchemaEnum(GenderEnum.class)
    @CheckEnum(value = GenderEnum.class, message = "性别错误")
    private Integer gender;

    @Schema(description = "邮箱账号")
    @NotNull(message = "邮箱账号不能为空")
    @Pattern(regexp = SmartVerificationUtil.EMAIL, message = "邮箱账号格式不正确")
    private String email;

}
