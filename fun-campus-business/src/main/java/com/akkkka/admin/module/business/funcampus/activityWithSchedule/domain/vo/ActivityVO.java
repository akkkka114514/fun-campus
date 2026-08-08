package com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.vo;

import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动管理 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-09-04 13:41:42
 * @Copyright akkkka114514
 */

@Data
public class ActivityVO {

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
     * 活动状态
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
    private Long activityBelongToSchoolId;

    private String activityBelongToSchoolName;

    /**
     * 活动所属组织
     */
    private Long activityBelongToOrganizationId;

    private String activityBelongToOrganizationName;

    /**
     * 活动所属学院
     */
    private Long activityBelongToCollegeId;

    private String activityBelongToCollegeName;

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

    private String categoryName;

    /**
     * 封面图片
     */
    private String coverImg;

    private PortalUserVO activityManager;

}
