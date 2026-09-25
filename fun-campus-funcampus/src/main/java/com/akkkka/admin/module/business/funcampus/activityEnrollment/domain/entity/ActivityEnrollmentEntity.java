package com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import lombok.Data;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;

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
    @TableField(value = "activity_id")
    private Long activityId;

    /**
     * 用户id
     */
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

    private Boolean signOutStatus;

    //检查是否已经签到过该活动
    public void validateSignInStatus(){
        if(Boolean.TRUE.equals(signInStatus)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"请勿重复签到");
        }
    }

    //检查是否已经签退过该活动
    public void validateSignOutStatus(){
        if(Boolean.TRUE.equals(signOutStatus)){
            throw new BusinessException(UserErrorCode.PARAM_ERROR,"请勿重复签退");
        }
    }

}
