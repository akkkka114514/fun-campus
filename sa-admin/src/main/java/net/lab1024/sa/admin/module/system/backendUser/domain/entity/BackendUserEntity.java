package net.lab1024.sa.admin.module.system.backendUser.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台用户 实体表
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2025-09-08 20:47:06
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@TableName("backend_user")
public class BackendUserEntity {

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 是否已删除
     */
    @TableField("deleted_flag")
    private Boolean deletedFlag;

    /**
     * 是否禁用
     */
    @TableField("disabled_flag")
    private Boolean disabledFlag;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;
    /**
     * 角色id
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

}