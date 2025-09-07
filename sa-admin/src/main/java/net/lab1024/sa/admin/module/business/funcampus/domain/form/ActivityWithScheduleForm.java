package net.lab1024.sa.admin.module.business.funcampus.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动和时间表 新建表单
 *
 * @Author akkkka114514
 * @Date 2025-09-07
 * @Copyright akkkka114514
 */

@Data
public class ActivityWithScheduleForm {

    /**
     * 活动信息
     */
    @Schema(description = "活动信息")
    @Valid
    @NotNull(message = "活动信息不能为空")
    private ActivityAddForm activity;

    /**
     * 活动时间表信息
     */
    @Schema(description = "活动时间表信息")
    @Valid
    @NotNull(message = "活动时间表信息不能为空")
    private ActivityScheduleAddForm schedule;

}