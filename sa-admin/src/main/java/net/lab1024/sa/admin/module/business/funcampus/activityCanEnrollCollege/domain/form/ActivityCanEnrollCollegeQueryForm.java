package net.lab1024.sa.admin.module.business.funcampus.activityCanEnrollCollege.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动能报名的学院 分页查询表单
 *
 * @Author akkkka114514
 * @Date 2026-01-01 20:24:18
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ActivityCanEnrollCollegeQueryForm extends PageParam {

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "能报名的学院id")
    private Long canEnrollCollege;

    @Schema(description = "是否删除")
    private Boolean deletedFlag;

}
