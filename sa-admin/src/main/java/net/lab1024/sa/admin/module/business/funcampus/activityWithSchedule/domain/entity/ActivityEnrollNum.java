package net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * author:akkkka114514
 * create at 2025-09-29 09:33
 */
@TableName("activity_enroll_num")
@Data
public class ActivityEnrollNum {
    @TableId(type = IdType.AUTO)
    private Long activityId;
    private Integer enrollNum;
}
