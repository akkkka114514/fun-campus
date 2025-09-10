package net.lab1024.sa.admin.module.system.backendUser.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * author:akkkka114514
 * create at 2025-09-09 18:45
 */
@Data
public class BackendUserQueryForm extends PageParam {

    @Schema(description = "搜索词")
    @Length(max = 20, message = "搜索词最多20字符")
    private String keyword;

    @Schema(description = "是否禁用")
    private Boolean disabledFlag;

    @Schema(description = "员工id集合")
    @Size(max = 99, message = "最多查询99个员工")
    private List<Long> backendUserIdList;

    @Schema(description = "删除标识", hidden = true)
    private Boolean deletedFlag;
    
    /**
     * 重写父类分页参数验证
     */
    @Schema(description = "分页-当前页码")
    @NotNull(message = "当前页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    @Override
    public Long getPageNum() {
        return super.getPageNum();
    }

    /**
     * 重写父类分页参数验证
     */
    @Schema(description = "分页-每页条数")
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数至少为1")
    @Max(value = 500, message = "每页条数不能超过500")
    @Override
    public Long getPageSize() {
        return super.getPageSize();
    }
}