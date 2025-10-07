package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动报名关系 分页查询表单
 *
 * @Author akkkka114514
 * @Date 2025-10-02 13:54:42
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ActivityEnrollmentQueryForm extends PageParam {

    @Schema(description = "根据活动id查询")
    private Long activityId;

    @Schema(description = "根据用户id查询")
    private Long userId;

    @Schema(description = "根据是否已删除查询")
    private Boolean deletedFlag;

    @Schema(description = "是否已签到，1-》是，0-》否")
    private Boolean signInStatus;

}
