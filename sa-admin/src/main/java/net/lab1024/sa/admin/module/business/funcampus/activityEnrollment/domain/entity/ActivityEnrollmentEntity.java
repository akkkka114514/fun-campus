package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
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
     * 活动id
     */
    @MppMultiId
    @TableField(value = "activity_id")
    private Long activityId;

    /**
     * 用户id
     */
    @MppMultiId
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 是否已签到，1-》是，0-》否
     */
    private Boolean signInStatus;

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

    /**
    *  审核状态：0-》待审核，1-》审核通过，2-》审核未通过
    */
    private byte signinReviewStatus;


    private byte enrollReviewStatus;

}
