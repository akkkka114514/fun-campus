package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form;

import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity.ActivityEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * author:akkkka114514
 * create at 2026-04-18 14:14
 */
@Data
public class ActivityAddForm {
    @Schema(description = "活动标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "活动标题 不能为空")
    @NotNull(message = "活动标题 不能为空")
    private String title;

    @Schema(description = "活动地点", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "活动地点 不能为空")
    @NotNull(message = "活动地点 不能为空")
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


    @Schema(description = "活动所属学校", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 0L, message = "活动所属学校 不能小于0")
    private Long activityBelongToSchoolId;

    @Schema(description = "活动所属组织")
    @Min(value = 0L, message = "活动所属组织 不能小于0")
    private Long activityBelongToOrganizationId;
    /**
     * 活动所属学院
     */
    @Schema(description = "活动所属学院")
    @Min(value = 0L, message = "活动所属学院 不能小于0")
    private Long activityBelongToCollegeId;

    /**
     * 描述
     */
    @Schema(description = "描述")
    @Size(max = 2000, message = "描述 不能超过2000个字符")
    private String description;

    /**
     * 报名需审核
     */
    @Schema(description = "报名需审核")
    private Boolean enrollNeedReview;

    /**
     * 需要签退
     */
    @Schema(description = "需要签退")
    private Boolean needSignOut;

    /**
     * 附件
     */
    @Schema(description = "附件")
    @Size(max = 500, message = "附件路径 不能超过500个字符")
    private String attachment;

    /**
     * 分类
     */
    @Schema(description = "分类")
    @Min(value = 0L, message = "分类 不能小于0")
    @NotNull(message = "活动分类 不能为空")
    private Long categoryId;

    /**
     * 封面图片
     */
    @Schema(description = "封面图片")
    @Size(max = 500, message = "封面图片路径 不能超过500个字符")
    @NotNull(message = "活动封面 不能为空")
    private String coverImg;

    /**
     * 活动管理员和发起者
     */
    @Schema(description = "活动管理员和发起者")
    @Min(value = 0L, message = "活动管理员和发起者 不能小于0")
    private Long activityManagerId;


}
