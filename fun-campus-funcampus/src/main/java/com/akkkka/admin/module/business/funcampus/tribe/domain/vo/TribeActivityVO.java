package com.akkkka.admin.module.business.funcampus.tribe.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 部落发起的活动 列表VO（门户）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class TribeActivityVO {

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "活动标题")
    private String title;

    @Schema(description = "封面图片（文件key）")
    private String coverImg;

    @Schema(description = "活动地点")
    private String position;

    @Schema(description = "活动状态：0-待报名 1-报名中 2-报名已结束 3-进行中 4-已结束 9-待报名审核")
    private Integer activityStatus;

    @Schema(description = "是否付费活动")
    private Boolean paidFlag;

    @Schema(description = "报名费（分）")
    private Integer priceFen;

    @Schema(description = "可获学分")
    private BigDecimal scoreCanGet;

    @Schema(description = "已报名人数")
    private Integer enrollNum;

    @Schema(description = "活动开始时间")
    private LocalDateTime activityStartTime;

    @Schema(description = "活动结束时间")
    private LocalDateTime activityEndTime;

}
