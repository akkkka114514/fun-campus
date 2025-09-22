package net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import net.lab1024.sa.base.common.domain.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * 组织账号运营者 分页查询表单
 *
 * @Author akkkka114514
 * @Date 2025-09-19 10:12:39
 * @Copyright akkkka114514
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class PortalOrganizerUserQueryForm extends PageParam {

    @Schema(description = "搜索词")
    @Length(max = 20, message = "搜索词最多20字符")
    private String keyword;

    @Schema(description = "删除标识", hidden = true)
    private boolean deletedFlag;

}
