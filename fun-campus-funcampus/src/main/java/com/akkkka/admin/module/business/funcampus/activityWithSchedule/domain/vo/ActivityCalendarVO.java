package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动日历 VO（按日期区间查询活动，前端日历页使用）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class ActivityCalendarVO {

    @Schema(description = "活动id")
    private Long id;

    @Schema(description = "活动标题")
    private String title;

    @Schema(description = "活动状态：0-等待报名 1-报名中 2-报名结束 3-进行中 4-已结束")
    private Integer status;

    @Schema(description = "活动地点")
    private String position;

    @Schema(description = "活动封面")
    private String coverImg;

    @Schema(description = "活动所属学校id")
    private Long activityBelongToSchoolId;

    @Schema(description = "活动所属学校名称")
    private String activityBelongToSchoolName;

    @Schema(description = "活动开始时间")
    private LocalDateTime activityStartTime;

    @Schema(description = "活动结束时间")
    private LocalDateTime activityEndTime;

}
