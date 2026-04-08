package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * author:akkkka114514
 * create at 2025-12-07 16:01
 */
@Data
public class ActivityDetailVO {

    /**
     * 活动主键
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

    private String description;
    /**
    * 报名需审核
    */
    private boolean enrollNeedReview;
    /**
    * 需要签退
    */
    private boolean needSignOut;

    private String attachment;

    private String category;

    private String coverImg;
    /**
    * 报名的用户
    */
    private List<EnrollerVO> enrollUsers;

    /**
    * 报名人数
    */
    private Integer enrollNum;
    /**
    * 签到人数
    */
    private Long signInNum;

    /**
    * 允许报名的年级
    */
    private List<String> canEnrollGrade;

    /**
    * 允许报名的学院
    */
    private List<String> canEnrollCollege;

    private List<String> canEnrollTribe;

    private LocalDateTime enrollStartTime;

    private LocalDateTime enrollEndTime;

    private LocalDateTime activityStartTime;

    private LocalDateTime activityEndTime;

    //todo 评论
}
