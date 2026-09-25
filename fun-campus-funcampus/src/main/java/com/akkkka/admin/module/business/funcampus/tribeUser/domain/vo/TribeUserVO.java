package com.akkkka.admin.module.business.funcampus.tribeUser.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 参与部落的用户 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:42:50
 * @Copyright akkkka114514
 */

@Data
public class TribeUserVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "部落id")
    private Long tribeId;

    @Schema(description = "参与部落的前端用户id")
    private Long portalUserId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

}
