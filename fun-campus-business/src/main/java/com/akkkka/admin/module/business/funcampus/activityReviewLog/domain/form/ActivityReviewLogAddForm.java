package com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动审核日志 新建表单
 *
 * @Author akkkka114514
 * @Date 2026-01-10 18:34:56
 * @Copyright akkkka114514
 */

@Data
public class ActivityReviewLogAddForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "活动id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动id 不能为空")
    private Long activityId;

    @Schema(description = "审核人id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "审核人id 不能为空")
    private Long reviewerId;

    /*
    指定下一阶段审核人
    */
    @Schema(description = "指定下一个审核人id")
    private Long nextReviewerId;

    @Schema(description = "下一个审核人姓名")
    private String nextReviewerName;

    @Schema(description = "审核人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "审核人姓名 不能为空")
    private String reviewerName;

    @Schema(description = "审核阶段", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "审核阶段不能为空")
    private Integer reviewStage;

    @Schema(description = "审核行为，1-》通过，2-》驳回，3-》建议", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "审核行为，1-》通过，2-》驳回，3-》建议 不能为空")
    private Integer action;

    @Schema(description = "拒绝理由")
    private String rejectReason;

    @Schema(description = "审阅建议")
    private String checkRemark;

}