package net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 运营者发布活动的对应关系 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-09-19 14:08:56
 * @Copyright akkkka114514
 */

@Data
public class OrganizerActivityVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "运营者id")
    private Long organizerId;

    @Schema(description = "是否删除")
    private Boolean deletedFlag;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
