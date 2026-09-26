package com.akkkka.admin.module.business.funcampus.portalUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 前端用户-个人资料更新表单（仅允许修改昵称/头像/手机号/性别）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class PortalUserProfileUpdateForm {

    @Schema(description = "昵称")
    @Size(max = 50, message = "昵称最多50字符")
    private String nickname;

    @Schema(description = "头像（文件key）")
    @Size(max = 255, message = "头像文件key最多255字符")
    private String avatar;

    @Schema(description = "手机号")
    @Size(max = 13, message = "手机号最多13字符")
    private String phone;

    @Schema(description = "性别：true-男，false-女")
    private Boolean gender;
}
