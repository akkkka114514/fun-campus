package net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 运营者发布活动的对应关系 实体类
 *
 * @Author akkkka114514
 * @Date 2025-09-19 14:08:56
 * @Copyright akkkka114514
 */

@Data
@TableName("organizer_activity")
public class OrganizerActivityEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 运营者id
     */
    private Long organizerId;

    /**
     * 是否删除
     */
    private Boolean deletedFlag;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
