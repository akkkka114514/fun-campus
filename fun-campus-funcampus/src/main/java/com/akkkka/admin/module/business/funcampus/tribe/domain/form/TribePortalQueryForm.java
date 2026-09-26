package com.akkkka.admin.module.business.funcampus.tribe.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部落 门户分页查询表单
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class TribePortalQueryForm extends PageParam {

    @Schema(description = "部落名关键词（模糊匹配）")
    private String keyword;

    @Schema(description = "排序：1-默认，2-热度（成员数）从高到低")
    private Integer sortType;

}
