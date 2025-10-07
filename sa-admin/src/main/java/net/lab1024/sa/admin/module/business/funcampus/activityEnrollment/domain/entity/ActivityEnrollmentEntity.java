package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动报名关系 实体类
 *
 * @Author akkkka114514
 * @Date 2025-10-02 13:54:42
 * @Copyright akkkka114514
 */

@Data
@TableName("activity_enrollment")
public class ActivityEnrollmentEntity {

    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long activityId;

    /**
     * 
     */
    @TableId
    private Long userId;

    /**
     * 是否已签到，1-》是，0-》否
     */
    private Boolean signInStatus;

    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 
     */
    private Boolean deletedFlag;

}
