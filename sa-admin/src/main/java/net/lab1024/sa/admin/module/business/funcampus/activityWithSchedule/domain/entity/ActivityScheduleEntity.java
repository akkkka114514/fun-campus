package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动时间表 实体类
 *
 * @Author akkkka114514
 * @Date 2025-09-06 15:54:07
 * @Copyright akkkka114514
 */

@Data
@TableName("activity_schedule")
public class ActivityScheduleEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long activityId;

    /**
     * 报名开始时间
     */
    private LocalDateTime enrollStartTime;

    /**
     * 报名结束时间
     */
    private LocalDateTime enrollEndTime;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityStartTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEndTime;

    /**
     * 签到开始时间
     */
    private LocalDateTime signinStartTime;

    /**
     * 签到结束时间
     */
    private LocalDateTime signinEndTime;

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
     * 是否已删除
     */
    private Boolean deletedFlag;

}
