package net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.lab1024.sa.admin.module.system.backendUser.domain.form.BackendUserUpdateForm;

/**
 * 组织账号运营者 更新表单
 *
 * @Author akkkka114514
 * @Date 2025-09-19 10:12:39
 * @Copyright akkkka114514
 */

@Data
public class PortalOrganizerUserUpdateForm extends BackendUserUpdateForm {

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "学校id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学校id 不能为空")
    private String schoolId;

}