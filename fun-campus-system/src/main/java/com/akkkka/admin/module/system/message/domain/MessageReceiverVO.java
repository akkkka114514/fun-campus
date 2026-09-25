package com.akkkka.admin.module.system.message.domain;

import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 消息接收人VO
 *
 * @Author akkkka114514
 * @Date 2026-09-22
 * @Copyright akkkka114514
 */
@Data
public class MessageReceiverVO {

    @Schema(description = "用户id")
    private Long id;

    @SchemaEnum(value = UserTypeEnum.class, desc = "用户类型")
    private Integer userType;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号（后台用户无手机号）")
    private String phone;
}
