package net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2026-01-17 14:39
 */
@Data
public class SimpleGradeInfoVO {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "年级")
    private String name;
}
