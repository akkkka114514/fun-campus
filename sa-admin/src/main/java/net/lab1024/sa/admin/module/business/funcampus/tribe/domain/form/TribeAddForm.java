package net.lab1024.sa.admin.module.business.funcampus.tribe.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 部落 新建表单
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@Data
public class TribeAddForm {

    @Schema(description = "部落id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部落id 不能为空")
    private Long id;

    @Schema(description = "部落名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "部落名 不能为空")
    private String name;

    @Schema(description = "部落类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部落类型 不能为空")
    private Long categoryId;

    @Schema(description = "主席id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主席id 不能为空")
    private Long presidentId;

    @Schema(description = "1-》组织，2-》院系", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "1-》组织，2-》院系 不能为空")
    private Integer belongTo;

    @Schema(description = "是否已删除", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否已删除 不能为空")
    private Boolean deletedFlag;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "创建时间 不能为空")
    private LocalDateTime createTime;

    @Schema(description = "修改时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "修改时间 不能为空")
    private LocalDateTime updateTime;

    @Schema(description = "所属学校id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "所属学校id 不能为空")
    private Long schoolId;
}