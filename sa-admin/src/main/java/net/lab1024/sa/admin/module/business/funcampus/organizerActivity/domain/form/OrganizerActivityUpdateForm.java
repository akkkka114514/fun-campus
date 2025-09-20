package net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 运营者发布活动的对应关系 更新表单
 *
 * @Author akkkka114514
 * @Date 2025-09-19 14:08:56
 * @Copyright akkkka114514
 */

@Data
public class OrganizerActivityUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

}