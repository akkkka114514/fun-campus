package net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollTribe.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动能报名的部落 新建表单
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:18:01
 * @Copyright akkkka114514
 */

@Data
public class ActivityCanEnrollTribeAddForm {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "id 不能为空")
    private Long id;

    @Schema(description = "活动id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动id 不能为空")
    private Long activityId;

    @Schema(description = "能报名的部落id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "能报名的部落id 不能为空")
    private Long canEnrollTribe;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "创建时间 不能为空")
    private LocalDateTime createTime;

    @Schema(description = "修改时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "修改时间 不能为空")
    private LocalDateTime updateTime;

    @Schema(description = "是否已删除", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否已删除 不能为空")
    private Boolean deletedFlag;

}