package com.akkkka.admin.module.business.funcampus.creditApplication.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学分认定 审核人候选 VO（门户表单：审核人院系/组织 + 审核人 两级选择）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class CreditReviewerVO {

    @Schema(description = "审核人id（backend_user）")
    private Long id;

    @Schema(description = "审核人用户名")
    private String username;

    @Schema(description = "审核人院系/组织id")
    private Long organizationId;

    @Schema(description = "审核人院系/组织名称")
    private String organizationName;

}
