package com.akkkka.admin.module.business.funcampus.tribe.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 部落加入申请 列表VO（门户我的申请 / 管理端审核列表通用）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class TribeApplicationVO {

    @Schema(description = "申请id")
    private Long id;

    @Schema(description = "部落id")
    private Long tribeId;

    @Schema(description = "部落名")
    private String tribeName;

    @Schema(description = "申请用户id")
    private Long portalUserId;

    @Schema(description = "申请用户名")
    private String username;

    @Schema(description = "申请理由")
    private String reason;

    @Schema(description = "审核状态：0-待审核 1-已通过 2-已驳回")
    private Integer status;

    @Schema(description = "审核意见")
    private String reviewRemark;

    @Schema(description = "审核人id（管理端用户）")
    private Long reviewUserId;

    @Schema(description = "审核时间")
    private LocalDateTime reviewTime;

    @Schema(description = "申请时间")
    private LocalDateTime createTime;

}
