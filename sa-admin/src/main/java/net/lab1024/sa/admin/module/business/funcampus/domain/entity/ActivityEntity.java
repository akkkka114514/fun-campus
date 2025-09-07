package net.lab1024.sa.admin.module.business.funcampus.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

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
     * 活动状态，1-》等待报名，2->报名结束，3-》等待签到，4-》活动结束
     */
    private Integer status;

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
    private Long activitySchoolId;

    /**
     * 活动所属组织
     */
    private Long activityOrganizerId;

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

}
