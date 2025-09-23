package net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 学校信息表 实体类
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */

@Data
@TableName("school_info")
public class SchoolInfoEntity {

    /**
     * 学校ID
     */
    @TableId
    private Long id;

    /**
     * 学校名称
     */
    private String name;

    /**
     * 学校编码
     */
    private String code;

    /**
     * 学校类型(1:小学,2:初中,3:高中,4:中职,5:高职,6:本科)
     */
    private Integer type;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 官网
     */
    private String website;

    /**
     * 学校简介
     */
    private String description;

    /**
     * 学校logo图片URL
     */
    private String logoUrl;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标识(0:未删除,1:已删除)
     */
    private Integer deletedFlag;

}
