package com.akkkka.admin.module.business.funcampus.portalUser.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 前端用户 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */

@Data
public class PortalUserVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "是否禁用")
    private Boolean disabledFlag;

    @Schema(description = "性别")
    private Boolean gender;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "学校id")
    private Long schoolId;

    @Schema(description = "学院id")
    private Long collegeId;

    @Schema(description = "年级id")
    private Long gradeId;

}
