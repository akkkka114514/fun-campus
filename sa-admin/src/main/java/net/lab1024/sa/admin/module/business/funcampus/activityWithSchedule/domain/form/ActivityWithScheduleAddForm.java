package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
* author:akkkka114514
* create at 2025-09-18 16:33
*/
@Data
public class ActivityWithScheduleAddForm {
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


    @Schema(description = "活动所属学校", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动所属学校 不能为空")
    @Min(value = 0L, message = "活动所属学校 不能小于0")
    private Long activityBelongToSchoolId;

    @Schema(description = "活动所属组织")
    @NotNull(message = "活动所属组织 不能为空")
    @Min(value = 0L, message = "活动所属组织 不能小于0")
    private Long activityBelongToOrganizationId;
    /**
     * 活动所属学院
     */
    @Schema(description = "活动所属学院")
    @Min(value = 0L, message = "活动所属学院 不能小于0")
    private Long activityBelongToCollegeId;

    /**
     * 是否删除
     */
    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

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
    private boolean enrollNeedReview;

    /**
     * 需要签退
     */
    @Schema(description = "需要签退")
    private boolean needSignOut;

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
    private Long categoryId;

    /**
     * 封面图片
     */
    @Schema(description = "封面图片")
    @Size(max = 500, message = "封面图片路径 不能超过500个字符")
    private String coverImg;

    /**
     * 活动管理员和发起者
     */
    @Schema(description = "活动管理员和发起者")
    @Min(value = 0L, message = "活动管理员和发起者 不能小于0")
    private Long activityManagerId;

    @Schema(description = "报名开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "报名开始时间 不能为空")
    @Future
    private LocalDateTime enrollStartTime;

    @Schema(description = "报名结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "报名结束时间 不能为空")
    @Future
    private LocalDateTime enrollEndTime;

    @Schema(description = "活动开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动开始时间 不能为空")
    @Future
    private LocalDateTime activityStartTime;

    @Schema(description = "活动结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动结束时间 不能为空")
    @Future
    private LocalDateTime activityEndTime;

    @Schema(description = "签到开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "签到开始时间 不能为空")
    @Future
    private LocalDateTime signinStartTime;

    @Schema(description = "签到结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "签到结束时间 不能为空")
    @Future
    private LocalDateTime signinEndTime;

    @Schema(description = "初审人")
    @Min(value = 0L, message = "初审人不能为负数")
    private Long initialReviewer;
    /**
     * 能报名的年级的id
     */
    @Schema(description = "能报名的年级的id")
    private List<@Min(value = 0L, message = "能报名的年级的id不能为负数") Long> canEnrollGradeIdList;
    /**
     * 能报名的部落的id
     */
    @Schema(description = "能报名的部落的id")
    private List<@Min(value = 0L, message = "能报名的部落的id不能为负数") Long> canEnrollTribeIdList;

}
