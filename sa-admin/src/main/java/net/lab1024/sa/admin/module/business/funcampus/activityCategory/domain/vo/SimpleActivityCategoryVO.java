package net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2026-01-17 14:29
 */
@Data
public class SimpleActivityCategoryVO {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "活动类型名称")
    private String name;
}
