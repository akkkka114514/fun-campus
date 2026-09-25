package com.akkkka.admin.module.business.funcampus.collegeInfo.domain.vo;

import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.vo.SchoolInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 学院信息 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:45:43
 * @Copyright akkkka114514
 */

@Data
public class CollegeInfoVO {


    @Schema(description = "id")
    private Long id;

    @Schema(description = "学院名称")
    private String name;

    @Schema(description = "所属学校id")
    private SchoolInfoVO school;

    @Schema(description = "是否删除")
    private Boolean deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

}
