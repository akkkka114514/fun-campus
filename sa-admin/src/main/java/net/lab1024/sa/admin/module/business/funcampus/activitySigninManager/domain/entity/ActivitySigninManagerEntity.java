package net.lab1024.sa.admin.module.business.funcampus.activitySigninManager.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动签到管理员 实体类
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:10:19
 * @Copyright akkkka114514
 */

@Data
@TableName("activity_signin_manager")
public class ActivitySigninManagerEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动主键
     */
    private Long activityId;

    /**
     * 活动签到员主键
     */
    private Long portalUserId;

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
     * 签到员用户名
     */
    private String username;

}
