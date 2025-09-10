package net.lab1024.sa.admin.module.system.role.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色 后台用户关系
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2022-03-07 18:54:42
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@TableName("t_role_user")
public class RoleBackendUserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private Long userId;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

    public RoleBackendUserEntity() {
    }

    public RoleBackendUserEntity(Long roleId, Long userId) {
        this.roleId = roleId;
        this.userId = userId;
    }
}
