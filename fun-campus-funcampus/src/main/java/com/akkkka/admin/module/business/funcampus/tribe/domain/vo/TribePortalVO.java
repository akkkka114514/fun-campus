package com.akkkka.admin.module.business.funcampus.tribe.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部落 门户列表VO
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class TribePortalVO {

    @Schema(description = "部落id")
    private Long id;

    @Schema(description = "部落名")
    private String name;

    @Schema(description = "部落图标（文件key）")
    private String icon;

    @Schema(description = "部落类型")
    private Long categoryId;

    @Schema(description = "主席id")
    private Long presidentId;

    @Schema(description = "主席用户名")
    private String presidentName;

    @Schema(description = "1-》组织，2-》院系")
    private Integer belongTo;

    @Schema(description = "成员数（热度）")
    private Long memberNum;

    @Schema(description = "当前用户是否已加入")
    private Boolean joinedFlag;

}
