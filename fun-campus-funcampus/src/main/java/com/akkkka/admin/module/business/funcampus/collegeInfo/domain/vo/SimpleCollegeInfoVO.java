package com.akkkka.admin.module.business.funcampus.collegeInfo.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 简要学院信息 VO
 *
 * @Author akkkka114514
 * @Date 2025-10-02
 * @Copyright akkkka114514
 */
@Data
public class SimpleCollegeInfoVO {

    /** 学院ID */
    @Schema(description = "学院ID")
    private Long id;

    /** 学院名称 */
    @Schema(description = "学院名称")
    private String name;
}
