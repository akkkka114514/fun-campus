package com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动审核日志 实体类
 *
 * @Author akkkka114514
 * @Date 2026-01-10 18:34:56
 * @Copyright akkkka114514
 */

@Data
@TableName("activity_review_log")
public class ActivityReviewLogEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 审核人id
     */
    private Long reviewerId;

    /**
     * 审核人姓名
     */
    private String reviewerName;

    private Long nextReviewerId;

    private String nextReviewerName;

    /**
     * 审核阶段，1-》初审，2-》审阅，3-》终审，4-》完结审核
     */
    private Byte reviewStage;

    /**
     * 审核行为，1-》通过，2-》驳回，3-》建议
     */
    private Byte action;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 审阅修改建议，仅适用于审阅，不适用于审核
     */
    private String checkRemark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //是否已删除
    private Boolean deletedFlag;

}
