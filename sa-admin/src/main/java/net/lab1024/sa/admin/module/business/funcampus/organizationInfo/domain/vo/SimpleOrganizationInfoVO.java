package net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2026-01-16 14:25
 */
@Data
public class SimpleOrganizationInfoVO {

    @Schema(description = "组织id")
    private Long id;

    @Schema(description = "组织名称")
    private String name;

}
