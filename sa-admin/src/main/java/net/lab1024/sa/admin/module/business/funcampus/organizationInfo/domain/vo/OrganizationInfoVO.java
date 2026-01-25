package net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 各学校组织信息 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:24:05
 * @Copyright akkkka114514
 */

@Data
public class OrganizationInfoVO {


    @Schema(description = "组织id")
    private Long id;

    @Schema(description = "属于学校的id")
    private Long schoolId;

    @Schema(description = "组织名称")
    private String name;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

}
