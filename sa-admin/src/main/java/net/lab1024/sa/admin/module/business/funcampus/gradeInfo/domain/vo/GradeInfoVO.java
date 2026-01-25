package net.lab1024.sa.admin.module.business.funcampus.gradeInfo.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 年级信息 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:47:10
 * @Copyright akkkka114514
 */

@Data
public class GradeInfoVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "年级")
    private String name;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime udpateTime;

}
