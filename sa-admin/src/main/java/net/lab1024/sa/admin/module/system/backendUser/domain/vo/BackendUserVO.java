package net.lab1024.sa.admin.module.system.backendUser.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.enumeration.GenderEnum;
import net.lab1024.sa.base.common.swagger.SchemaEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * author:akkkka114514
 * create at 2025-09-09 19:07
 */
@Data
public class BackendUserVO {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "登录账号")
    private String username;

    @SchemaEnum(GenderEnum.class)
    private Integer gender;

    @Schema(description = "是否被禁用")
    private Boolean disabledFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "角色列表")
    private List<Long> roleIdList;

    @Schema(description = "角色名称列表")
    private List<String> roleNameList;

    @Schema(description = "邮箱")
    private String email;

}
