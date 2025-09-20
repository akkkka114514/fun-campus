package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动管理 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-09-04 13:41:42
 * @Copyright akkkka114514
 */

@Data
public class ActivityVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "活动标题")
    private String title;

    @Schema(description = "活动状态")
    private Integer status;

    @Schema(description = "活动地点")
    private String position;

    @Schema(description = "能得到的学分")
    private BigDecimal scoreCanGet;

    @Schema(description = "报名人数限制")
    private Integer enrollNumLimit;

    @Schema(description = "活动所属学校")
    private Long activitySchoolId;

    @Schema(description = "活动所属组织")
    private Long activityOrganizerId;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

}
