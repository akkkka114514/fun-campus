package com.akkkka.admin.module.business.funcampus.creditApplication.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 学分认定申请 VO（门户我的申请 / 管理端审核列表通用）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class CreditApplicationVO {

    @Schema(description = "申请id")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "学期")
    private String semester;

    @Schema(description = "申请内容")
    private String content;

    @Schema(description = "证明材料图片fileKey列表")
    private List<String> imageList;

    @Schema(description = "申请人id")
    private Long applicantUserId;

    @Schema(description = "申请人用户名")
    private String applicantUsername;

    @Schema(description = "审核人id（管理端用户）")
    private Long reviewUserId;

    @Schema(description = "审核人姓名")
    private String reviewUserName;

    @Schema(description = "审核人院系/组织id")
    private Long reviewOrganizationId;

    @Schema(description = "审核人院系/组织名称")
    private String reviewOrganizationName;

    @Schema(description = "审核状态：0-待审核 1-已通过 2-已驳回")
    private Integer status;

    @Schema(description = "审核状态名称")
    private String statusName;

    @Schema(description = "审核意见/驳回原因")
    private String reviewRemark;

    @Schema(description = "审核时间")
    private LocalDateTime reviewTime;

    @Schema(description = "申请时间")
    private LocalDateTime createTime;

}
