package net.lab1024.sa.admin.module.business.funcampus.tribe.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 部落 实体类
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@Data
@TableName("tribe")
public class TribeEntity {

    /**
     * 部落id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部落名
     */
    private String name;

    /**
     * 部落类型
     */
    private Long categoryId;

    /**
     * 主席id
     */
    private Long presidentId;

    /**
     * 1-》组织，2-》院系
     */
    private Integer belongTo;

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
    /**
     * 所属学校id
     */
    private Long schoolId;
}
