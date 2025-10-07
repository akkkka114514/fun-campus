package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动报名关系 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-10-02 13:54:42
 * @Copyright akkkka114514
 */

@Data
public class ActivityEnrollmentVO {


    @Schema(description = "活动主键")
    private Long activityId;

    @Schema(description = "用户主键")
    private Long userId;

    @Schema(description = "是否已签到，1-》是，0-》否")
    private Boolean signInStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

}
