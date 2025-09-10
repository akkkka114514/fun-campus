package net.lab1024.sa.admin.module.system.backendUser.domain.form;

/**
 * author:akkkka114514
 * create at 2025-09-09 18:53
 */

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BackendUserUpdateRoleForm {

    @Schema(description = "后台用户id")
    @NotNull(message = "后台用户id不能为空")
    private Long id;

    @Schema(description = "角色ids")
    @NotNull(message = "角色列表不能为空")
    @Size(min = 1, max = 99, message = "角色数量应为1-99个")
    private List<Long> roleIdList;

}