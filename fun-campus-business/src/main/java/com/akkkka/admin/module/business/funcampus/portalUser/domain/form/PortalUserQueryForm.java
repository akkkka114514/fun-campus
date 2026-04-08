package com.akkkka.admin.module.business.funcampus.portalUser.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 前端用户 分页查询表单
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class PortalUserQueryForm extends PageParam {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

    @Schema(description = "是否禁用")
    private Boolean disableFlag;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "学校id")
    private String schoolId;

    @Schema(description = "学院id")
    private String collegeId;

}
