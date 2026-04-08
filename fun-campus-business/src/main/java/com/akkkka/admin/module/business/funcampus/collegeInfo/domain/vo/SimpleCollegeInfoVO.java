package com.akkkka.admin.module.business.funcampus.collegeInfo.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2026-01-15 20:12
 */
@Data
public class SimpleCollegeInfoVO {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "学院名称")
    private String name;
}
