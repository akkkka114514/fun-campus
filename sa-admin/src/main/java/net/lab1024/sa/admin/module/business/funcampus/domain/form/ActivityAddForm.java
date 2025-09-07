package net.lab1024.sa.admin.module.business.funcampus.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 活动管理 新建表单
 *
 * @Author akkkka114514
 * @Date 2025-09-04 13:41:42
 * @Copyright akkkka114514
 */

@Data
public class ActivityAddForm {

    @Schema(description = "活动标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "活动标题 不能为空")
    private String title;

    @Schema(description = "活动地点", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "活动地点 不能为空")
    private String position;

    @Schema(description = "能得到的学分", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "能得到的学分 不能为空")
    private BigDecimal scoreCanGet;

    @Schema(description = "报名人数限制", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "报名人数限制 不能为空")
    private Integer enrollNumLimit;

    @Schema(description = "活动所属组织", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动所属组织 不能为空")
    private Long activityOrganizerId;

}