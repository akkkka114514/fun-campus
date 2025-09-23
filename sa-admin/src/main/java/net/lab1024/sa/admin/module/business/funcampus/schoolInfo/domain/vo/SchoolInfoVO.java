package net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 学校信息表 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */

@Data
public class SchoolInfoVO {


    @Schema(description = "学校ID")
    private Long id;

    @Schema(description = "学校名称")
    private String name;

    @Schema(description = "学校编码")
    private String code;

    @Schema(description = "学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)")
    private Integer type;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "官网")
    private String website;

    @Schema(description = "学校简介")
    private String description;

    @Schema(description = "学校logo图片URL")
    private String logoUrl;

    @Schema(description = "状态(0:禁用,1:启用)")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "删除标识(0:未删除,1:已删除)")
    private Integer deletedFlag;

}
