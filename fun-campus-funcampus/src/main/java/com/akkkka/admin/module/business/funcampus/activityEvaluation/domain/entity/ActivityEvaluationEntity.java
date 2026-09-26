package com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动评价 实体类
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
@TableName("activity_evaluation")
public class ActivityEvaluationEntity {

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
     * 评价人id（portal_user）
     */
    private Long userId;

    /**
     * 评价人姓名（快照）
     */
    private String username;

    /**
     * 评分：1-5
     */
    private Integer score;

    /**
     * 评价内容
     */
    private String content;

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
