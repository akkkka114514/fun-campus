package com.akkkka.admin.module.business.funcampus.organizationInfo.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 各学校组织信息 实体类
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:24:05
 * @Copyright akkkka114514
 */

@Data
@TableName("organization_info")
public class OrganizationInfoEntity {

    /**
     * 组织id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 属于学校的id
     */
    private Long schoolId;

    /**
     * 组织名称
     */
    private String name;

    /**
     * 是否已删除
     */
    private Boolean deletedFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
