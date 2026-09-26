package com.akkkka.admin.module.business.funcampus.tribe.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部落成员 分页查询表单（门户）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class TribeMemberQueryForm extends PageParam {

    @Schema(description = "部落id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部落id 不能为空")
    private Long tribeId;

    @Schema(description = "用户名关键词（模糊匹配）")
    private String keyword;

}
