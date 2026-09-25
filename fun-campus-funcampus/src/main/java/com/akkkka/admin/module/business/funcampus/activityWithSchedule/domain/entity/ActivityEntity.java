package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.akkkka.admin.module.business.funcampus.activityOrder.constant.RefundPolicy;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.constant.ActivityStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;

/**
 * 活动管理 实体类
 *
 * @Author akkkka114514
 * @Date 2025-09-04 13:41:42
 * @Copyright akkkka114514
 */

@Data
@TableName("activity")
public class ActivityEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动状态（枚举化，与字典 ACTIVITY_STATUS 对应，见 ActivityStatus）
     */
    private ActivityStatus status;

    /**
     * 活动地点
     */
    private String position;

    /**
     * 能得到的学分
     */
    private BigDecimal scoreCanGet;

    /**
     * 报名人数限制
     */
    private Integer enrollNumLimit;

    /**
     * 活动所属学校
     */
    private Long activityBelongToSchoolId;

    /**
     * 活动所属组织
     */
    private Long activityBelongToOrganizationId;

    /**
     * 活动所属学院
     */
    private Long activityBelongToCollegeId;

    /**
     * 是否删除
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
     * 描述
     */
    private String description;

    /**
     * 报名需审核
     */
    private Boolean enrollNeedReview;

    /**
     * 需要签退
     */
    private Boolean needSignOut;

    /**
     * 附件
     */
    private String attachment;

    /**
     * 分类
     */
    private Long categoryId;

    /**
     * 封面图片
     */
    private String coverImg;

    /**
     * 活动管理员和发起者
     */
    private Long activityManagerId;

    /**
     * 是否付费活动（true 时报名需走「下单-支付」链路，支付成功后自动写入报名记录）
     */
    private Boolean paidFlag;

    /**
     * 报名费（单位：分，paid_flag=true 时有效）
     */
    private Integer priceFen;

    /**
     * 退款政策（见 RefundPolicy，仅付费活动有效）
     */
    private RefundPolicy refundPolicy;

    public void validateStatus(ActivityStatus status){
        if(this.getStatus() != status){
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "活动未开始报名或报名已结束");
        }
    }
}
