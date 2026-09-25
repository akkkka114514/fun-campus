package com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form;

import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动审核日志 更新表单
 *
 * @Author akkkka114514
 * @Date 2026-01-10 18:34:56
 * @Copyright akkkka114514
 */

@Data
public class ActivityReviewLogUpdateForm{

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

    @Schema(description = "审核人id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "审核人id 不能为空")
    private Long reviewerId;

    @Schema(description = "审核人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "审核人姓名 不能为空")
    private String reviewerName;

    @Schema(description = "审核行为，1-》通过，2-》驳回，3-》建议", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "审核行为，1-》通过，2-》驳回，3-》建议 不能为空")
    private ActivityReviewEvent action;

    @Schema(description = "拒绝理由")
    private String rejectReason;

    @Schema(description = "审阅建议")
    private String checkRemark;


    public static ActivityReviewLogEntity convert(ActivityReviewLogUpdateForm updateForm){
        ActivityReviewLogEntity e = new ActivityReviewLogEntity();
        e.setId(updateForm.getId());
        e.setActivityId(null);
        e.setReviewerId(updateForm.getReviewerId());
        e.setReviewerName(updateForm.getReviewerName());
        e.setReviewStage(null);
        e.setAction(updateForm.getAction());
        e.setRejectReason(updateForm.getRejectReason());
        e.setCheckRemark(updateForm.getCheckRemark());
        e.setCreateTime(null);
        e.setUpdateTime(LocalDateTime.now());
        e.setDeletedFlag(null);

        return e;
    }

}