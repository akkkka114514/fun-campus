package net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 各学校组织信息 新建表单
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:24:05
 * @Copyright akkkka114514
 */

@Data
public class OrganizationInfoAddForm {

    @Schema(description = "组织id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "组织id 不能为空")
    private Long id;

    @Schema(description = "属于学校的id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "属于学校的id 不能为空")
    private Long schoolId;

    @Schema(description = "组织名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "组织名称 不能为空")
    private String name;

    @Schema(description = "是否已删除", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否已删除 不能为空")
    private Boolean deletedFlag;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "创建时间 不能为空")
    private LocalDateTime createTime;

    @Schema(description = "修改时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "修改时间 不能为空")
    private LocalDateTime updateTime;

}