package com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.form;

import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewEvent;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.constant.ActivityReviewStage;
import com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity.ActivityReviewLogEntity;
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

    @Schema(description = "活动id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动id 不能为空")
    private Long activityId;

    @Schema(description = "审核人id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "审核人id 不能为空")
    private Long reviewerId;

    @Schema(description = "审核人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "审核人姓名 不能为空")
    private String reviewerName;

    @Schema(description = "审核阶段", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "审核阶段不能为空")
    private ActivityReviewStage reviewStage;

    public static ActivityReviewLogEntity convert(ActivityReviewLogAddForm addForm){
        ActivityReviewLogEntity e = new ActivityReviewLogEntity();
        e.setId(null);
        e.setActivityId(addForm.getActivityId());
        e.setReviewerId(addForm.getReviewerId());
        e.setReviewerName(addForm.getReviewerName());
        e.setReviewStage(addForm.getReviewStage());
        e.setAction(null);
        e.setRejectReason(null);
        e.setCheckRemark(null);
        e.setCreateTime(LocalDateTime.now());
        e.setUpdateTime(LocalDateTime.now());
        e.setDeletedFlag(false);

        return e;
    }

}