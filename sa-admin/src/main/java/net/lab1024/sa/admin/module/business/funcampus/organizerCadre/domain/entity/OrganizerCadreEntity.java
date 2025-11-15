package net.lab1024.sa.admin.module.business.funcampus.organizerCadre.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 组织干事用户 实体类
 *
 * @Author akkkka114514
 * @Date 2025-11-08 14:09:30
 * @Copyright akkkka114514
 */

@Data
@TableName("organizer_cadre")
public class OrganizerCadreEntity {

    /**
     * 与portalUser共享id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属organizer
     */
    private Long organizerId;

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
     * 删除flag
     */
    private Boolean deletedFlag;

}
