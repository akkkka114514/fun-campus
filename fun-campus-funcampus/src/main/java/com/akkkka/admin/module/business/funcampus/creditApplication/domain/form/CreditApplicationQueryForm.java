package com.akkkka.admin.module.business.funcampus.creditApplication.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学分认定申请 分页查询表单（管理端审核列表）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CreditApplicationQueryForm extends PageParam {

    @Schema(description = "审核状态：0-待审核 1-已通过 2-已驳回")
    private Integer status;

    @Schema(description = "关键词（标题/申请人模糊匹配）")
    private String keyword;

}
