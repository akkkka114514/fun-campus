package com.akkkka.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2026-05-09 20:58
 */
@Data
public class IdNameVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "名称")
    private String name;
}
