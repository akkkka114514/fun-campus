package com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 我的报名 分页查询表单
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class MyEnrollmentQueryForm extends PageParam {

    @Schema(description = "是否已签到：true-已签到，false-未签到（不传查全部）")
    private Boolean signInStatus;

    @Schema(description = "活动状态筛选（不传查全部）：0-待报名 1-报名中 2-报名已结束 3-进行中 4-已结束 9-待报名审核")
    private Integer activityStatus;

}
