package net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 组织干事用户 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-11-08 14:09:30
 * @Copyright akkkka114514
 */

@Data
public class OrganizerCadreVO {


    @Schema(description = "与portalUser共享id")
    private Long id;

    @Schema(description = "所属organizer")
    private Long organizerId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "删除flag")
    private Boolean deletedFlag;

}
