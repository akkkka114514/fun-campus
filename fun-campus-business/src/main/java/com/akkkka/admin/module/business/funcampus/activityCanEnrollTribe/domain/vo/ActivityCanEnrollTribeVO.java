package com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动能报名的部落 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:18:01
 * @Copyright akkkka114514
 */

@Data
public class ActivityCanEnrollTribeVO {


    @Schema(description = "id")
    private Long id;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "能报名的部落id")
    private Long canEnrollTribe;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

}
