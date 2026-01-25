package net.lab1024.sa.admin.module.business.funcampus.activityReviewLog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动审核日志 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-01-10 18:34:56
 * @Copyright akkkka114514
 */

@Data
public class ActivityReviewLogVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "审核人id")
    private Long reviewerId;

    @Schema(description = "审核人姓名")
    private String reviewerName;

    @Schema(description = "审核阶段，1-》初审，2-》审阅，3-》终审，4-》完结审核")
    private Integer reviewStage;

    @Schema(description = "审核行为，1-》通过，2-》驳回，3-》建议")
    private Integer action;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "审阅修改建议，仅适用于审阅，不适用于审核")
    private String checkRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
