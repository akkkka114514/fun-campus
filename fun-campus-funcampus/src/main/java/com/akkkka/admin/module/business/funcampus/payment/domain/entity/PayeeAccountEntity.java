package com.akkkka.admin.module.business.funcampus.payment.domain.entity;

import java.time.LocalDateTime;

import com.akkkka.admin.module.business.funcampus.payment.constant.PayeeAccountStatus;
import com.akkkka.admin.module.business.funcampus.payment.constant.PayeeOwnerType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 收款账户 实体类（模拟渠道侧商户号：报名费收给谁）
 * <p>
 * 与真实渠道的 mch_id 对齐：下单时按活动归属解析出收款商户号，
 * 回调验签通过后校验商户号一致（防串户），退款按原收款商户原路退回
 *
 * @Author akkkka114514
 * @Date 2026-10-02
 * @Copyright akkkka114514
 */
@Data
@TableName("payee_account")
public class PayeeAccountEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收款主体类型：1-学校 2-学院 3-组织
     */
    private PayeeOwnerType ownerType;

    /**
     * 主体id（school_info/college_info/organization_info）
     */
    private Long ownerId;

    /**
     * 主体名称（开户时快照，展示用）
     */
    private String ownerName;

    /**
     * 收款商户号（模拟渠道商户号，真实渠道对应 mch_id）
     */
    private String payeeNo;

    /**
     * 状态：0-停用 1-启用
     */
    private PayeeAccountStatus status;

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
