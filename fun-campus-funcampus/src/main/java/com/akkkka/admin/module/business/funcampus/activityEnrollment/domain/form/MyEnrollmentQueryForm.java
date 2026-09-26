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

}
