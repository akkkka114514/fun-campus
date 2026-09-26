package com.akkkka.admin.module.business.funcampus.tribe.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部落加入申请 分页查询表单（管理端）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class TribeApplicationQueryForm extends PageParam {

    @Schema(description = "部落id")
    private Long tribeId;

    @Schema(description = "审核状态：0-待审核 1-已通过 2-已驳回（不传查全部）")
    private Integer status;

}
