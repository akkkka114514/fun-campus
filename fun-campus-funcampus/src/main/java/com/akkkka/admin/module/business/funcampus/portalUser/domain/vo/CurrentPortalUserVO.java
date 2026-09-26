package com.akkkka.admin.module.business.funcampus.portalUser.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 前端用户-当前登录用户信息 VO
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class CurrentPortalUserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别：true-男，false-女")
    private Boolean gender;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "学院ID")
    private Long collegeId;

    @Schema(description = "学院名称")
    private String collegeName;

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "年级名称")
    private String gradeName;

    @Schema(description = "组织ID")
    private Long organizationId;

    @Schema(description = "学分（实践分）")
    private BigDecimal gradeScore;

    @Schema(description = "信誉分")
    private Integer creditScore;

    @Schema(description = "能否发布活动")
    private Boolean canPublishActivity;
}
