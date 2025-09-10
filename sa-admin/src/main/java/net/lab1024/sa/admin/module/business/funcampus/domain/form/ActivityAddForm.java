package net.lab1024.sa.admin.module.business.funcampus.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

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
    @DecimalMin(value = "0", message = "能得到的学分 不能小于0")
    @DecimalMax(value = "100", message = "能得到的学分 不能大于100")
    private BigDecimal scoreCanGet;

    @Schema(description = "报名人数限制", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "报名人数限制 不能为空")
    @Min(value = 0L, message = "报名人数限制 不能小于0")
    @Max(value = 1000L, message = "报名人数限制 不能大于100000")
    private Integer enrollNumLimit;

    @Schema(description = "活动所属组织", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动所属组织 不能为空")
    @Min(value = 0L, message = "活动所属组织 不能小于0")
    private Long activityOrganizerId;

}