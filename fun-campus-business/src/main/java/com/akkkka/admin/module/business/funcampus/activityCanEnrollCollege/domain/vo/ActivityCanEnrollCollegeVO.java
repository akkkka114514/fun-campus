package com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.vo;

import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.vo.CollegeInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动能报名的学院 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-01-01 20:24:18
 * @Copyright akkkka114514
 */

@Data
public class ActivityCanEnrollCollegeVO {


    @Schema(description = "id")
    private Long id;

    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "能报名的学院id")
    private Long canEnrollCollegeId;

    @Schema(description = "学院名")
    private String collegeName;

    @Schema(description = "是否删除")
    private Boolean deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

}
