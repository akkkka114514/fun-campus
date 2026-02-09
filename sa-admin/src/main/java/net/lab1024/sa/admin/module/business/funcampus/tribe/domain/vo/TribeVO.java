package net.lab1024.sa.admin.module.business.funcampus.tribe.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 部落 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@Data
public class TribeVO {


    @Schema(description = "部落id")
    private Long id;

    @Schema(description = "部落名")
    private String name;

    @Schema(description = "部落类型")
    private Long categoryId;

    @Schema(description = "主席id")
    private Long presidentId;

    @Schema(description = "1-》组织，2-》院系")
    private Integer belongTo;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "所属学校id")
    private Long schoolId;

}
