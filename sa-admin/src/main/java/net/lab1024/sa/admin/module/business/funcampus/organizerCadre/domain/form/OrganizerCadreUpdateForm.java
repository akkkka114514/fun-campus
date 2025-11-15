package net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 组织干事用户 更新表单
 *
 * @Author akkkka114514
 * @Date 2025-11-08 14:09:30
 * @Copyright akkkka114514
 */

@Data
public class OrganizerCadreUpdateForm {

    @Schema(description = "与portalUser共享id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "与portalUser共享id 不能为空")
    private Long id;

    @Schema(description = "所属organizer", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "所属organizer 不能为空")
    private Long organizerId;

}