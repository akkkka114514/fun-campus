package com.akkkka.admin.module.system.backendUser.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
* author:akkkka114514
* create at 2026-01-16 18:49
*/
@Data
public class SimpleBackendUserVO {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "用户名")
    private String username;

}
