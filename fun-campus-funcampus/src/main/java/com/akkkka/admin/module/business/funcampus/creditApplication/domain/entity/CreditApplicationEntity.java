package com.akkkka.admin.module.business.funcampus.creditApplication.domain.entity;

import com.akkkka.admin.module.business.funcampus.creditApplication.constant.CreditApplicationStatus;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 学分认定申请 实体类
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
@TableName("credit_application")
public class CreditApplicationEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 学期
     */
    private String semester;

    /**
     * 申请内容
     */
    private String content;

    /**
     * 证明材料图片 fileKey 列表（逗号分隔）
     */
    private String imageList;

    /**
     * 申请人id（portal_user）
     */
    private Long applicantUserId;

    /**
     * 申请人用户名（快照）
     */
    private String applicantUsername;

    /**
     * 申请人学校id（快照）
     */
    private Long applicantSchoolId;

    /**
     * 审核人id（backend_user，can_review=1）
     */
    private Long reviewUserId;

    /**
     * 审核人姓名（快照）
     */
    private String reviewUserName;

    /**
     * 审核人院系/组织id
     */
    private Long reviewOrganizationId;

    /**
     * 审核人院系/组织名称（快照）
     */
    private String reviewOrganizationName;

    /**
     * 审核状态（见 CreditApplicationStatus）
     */
    private CreditApplicationStatus status;

    /**
     * 审核意见/驳回原因
     */
    private String reviewRemark;

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
