package com.akkkka.admin.module.system.role.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色的后台用户
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2022-04-08 21:53:04
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
public class RoleBackendUserVO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "后台用户ID")
    private Long backendUserId;

    @Schema(description = "角色名称")
    private String roleName;
}
