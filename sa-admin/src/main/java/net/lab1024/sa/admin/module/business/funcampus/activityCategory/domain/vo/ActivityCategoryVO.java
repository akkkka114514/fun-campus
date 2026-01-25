package net.lab1024.sa.admin.module.business.funcampus.activityCategory.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动分类 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-01-10 19:46:44
 * @Copyright akkkka114514
 */

@Data
public class ActivityCategoryVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "活动类型名称")
    private String name;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

}
