package net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学校信息表 更新表单
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */

@Data
public class SchoolInfoUpdateForm {

    @Schema(description = "学校ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "学校ID 不能为空")
    private Long id;

}