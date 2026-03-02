package net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import net.lab1024.sa.base.common.domain.RequestUser;

/**
 * 前端用户 实体类
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */

@Data
@TableName("portal_user")
public class PortalUserEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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
    * 是否禁用
    */
    private Boolean disableFlag;

    /**
     * 性别
     */
    private Boolean gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 学校id
     */
    private Long schoolId;

    /**
     * 学院id
     */
    private Long collegeId;

    //能否发布活动
    private boolean canPublishActivity;

    private Long gradeId;

}