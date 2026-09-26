package com.akkkka.admin.module.business.funcampus.tribe.domain.entity;

import com.akkkka.admin.module.business.funcampus.tribe.constant.TribeApplicationStatus;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 部落加入申请 实体类
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
@TableName("tribe_application")
public class TribeApplicationEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部落id
     */
    private Long tribeId;

    /**
     * 申请的前端用户id
     */
    private Long portalUserId;

    /**
     * 申请人用户名（快照）
     */
    private String username;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 审核状态（见 TribeApplicationStatus）
     */
    private TribeApplicationStatus status;

    /**
     * 审核意见
     */
    private String reviewRemark;

    /**
     * 审核人id（管理端用户）
     */
    private Long reviewUserId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

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

}
