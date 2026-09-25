package com.akkkka.admin.module.business.funcampus.tribe.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2026-01-25 13:03
 */
@Data
public class SimpleTribeVO {
    @Schema(description = "部落id")
    private Long id;
    @Schema(description = "部落名")
    private String name;
}
