package net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织干事用户 分页查询表单
 *
 * @Author akkkka114514
 * @Date 2025-11-08 14:09:30
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class OrganizerCadreQueryForm extends PageParam {

    @Schema(description = "与portalUser共享id")
    private Long id;

    @Schema(description = "所属organizer")
    private Long organizerId;

    @Schema(description = "删除flag")
    private Boolean deletedFlag;

}
