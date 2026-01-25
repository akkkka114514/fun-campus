package net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollGrade.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动能报名的年级 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:42:17
 * @Copyright akkkka114514
 */

@Data
public class ActivityCanEnrollGradeVO {


    @Schema(description = "id")
    private Long id;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "能报名的年级")
    private Integer canEnrollGrade;

    @Schema(description = "是否删除")
    private Boolean deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

}
