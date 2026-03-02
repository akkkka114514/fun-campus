package net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动签到管理员 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:10:19
 * @Copyright akkkka114514
 */

@Data
public class ActivitySigninManagerVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "活动主键")
    private Long activityId;

    @Schema(description = "活动签到员主键")
    private Long portalUserId;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "签到员用户名")
    private String username;

}
