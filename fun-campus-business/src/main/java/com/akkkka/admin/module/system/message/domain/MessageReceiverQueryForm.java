package com.akkkka.admin.module.system.message.domain;

import com.akkkka.common.domain.PageParam;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.swagger.SchemaEnum;
import com.akkkka.common.validator.enumeration.CheckEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 消息接收人查询form（管理端发送消息时选择接收人）
 *
 * @Author akkkka114514
 * @Date 2026-09-22
 * @Copyright akkkka114514
 */
@Data
public class MessageReceiverQueryForm extends PageParam {

    @SchemaEnum(value = UserTypeEnum.class, desc = "接收人类型")
    @CheckEnum(value = UserTypeEnum.class, message = "接收人类型")
    @NotNull(message = "接收人类型不能为空")
    private Integer receiverUserType;

    @Schema(description = "关键词（用户名）")
    @Length(max = 50, message = "关键词最多50字符")
    private String keyword;
}
