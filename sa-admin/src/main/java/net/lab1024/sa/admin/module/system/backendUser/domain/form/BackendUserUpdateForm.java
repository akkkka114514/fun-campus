package net.lab1024.sa.admin.module.system.backendUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2025-09-09 18:47
 */

@Data
public class BackendUserUpdateForm extends BackendUserAddForm{
    @Schema(description = "后台用户id")
    @NotNull(message = "后台用户id不能为空")
    private Long id;
}
