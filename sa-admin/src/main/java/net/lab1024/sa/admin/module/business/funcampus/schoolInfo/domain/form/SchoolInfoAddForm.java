package net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 学校信息表 新建表单
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */

@Data
public class SchoolInfoAddForm {

    @Schema(description = "学校名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学校名称 不能为空")
    private String name;

    @Schema(description = "学校编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学校编码 不能为空")
    private String code;

    @Schema(description = "学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科) 不能为空")
    private Integer type;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "详细地址 不能为空")
    private String address;

    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "联系人 不能为空")
    private String contactPerson;

    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "联系电话 不能为空")
    private String contactPhone;

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱 不能为空")
    private String email;

    @Schema(description = "官网", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "官网 不能为空")
    private String website;

    @Schema(description = "学校简介", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学校简介 不能为空")
    private String description;

    @Schema(description = "学校logo图片URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学校logo图片URL 不能为空")
    private String logoUrl;

    @Schema(description = "状态(0:禁用,1:启用)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态(0:禁用,1:启用) 不能为空")
    private Integer status;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序 不能为空")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "创建时间 不能为空")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "更新时间 不能为空")
    private LocalDateTime updateTime;

    @Schema(description = "删除标识(0:未删除,1:已删除)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "删除标识(0:未删除,1:已删除) 不能为空")
    private Integer deletedFlag;

}