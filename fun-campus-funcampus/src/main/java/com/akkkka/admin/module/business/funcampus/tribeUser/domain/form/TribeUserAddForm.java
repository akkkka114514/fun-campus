package com.akkkka.admin.module.business.funcampus.tribeUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 参与部落的用户 新建表单
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:42:50
 * @Copyright akkkka114514
 */

@Data
public class TribeUserAddForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "部落id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部落id 不能为空")
    private Long tribeId;

    @Schema(description = "参与部落的前端用户id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "参与部落的前端用户id 不能为空")
    private Long portalUserId;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名 不能为空")
    private String username;

    @Schema(description = "是否已删除", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean deletedFlag;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "修改时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

}