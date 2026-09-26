package com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动能报名的学院 新建表单
 *
 * @Author akkkka114514
 * @Date 2026-01-01 20:24:18
 * @Copyright akkkka114514
 */

@Data
public class ActivityCanEnrollCollegeAddForm {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "活动id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动id 不能为空")
    private Long activityId;

    @Schema(description = "能报名的学院id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "能报名的学院id 不能为空")
    private Long canEnrollCollege;

    @Schema(description = "是否删除", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean deletedFlag;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "修改时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

}