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
    @Schema(description = "主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动标题
     */
    @Schema(description = "活动标题")
    private String title;

    /**
     * 活动状态
     */
    @Schema(description = "活动状态")
    private Integer status;

    /**
     * 活动地点
     */
    @Schema(description = "活动地点")
    private String position;

    /**
     * 能得到的学分
     */
    @Schema(description = "能得到的学分")
    private BigDecimal scoreCanGet;

    /**
     * 报名人数限制
     */
    @Schema(description = "报名人数限制")
    private Integer enrollNumLimit;

    /**
     * 活动所属学校
     */
    @Schema(description = "活动所属学校ID")
    private Long activityBelongToSchoolId;

    @Schema(description = "活动所属学校名称")
    private String activityBelongToSchoolName;

    /**
     * 活动所属组织
     */
    @Schema(description = "活动所属组织ID")
    private Long activityBelongToOrganizationId;

    @Schema(description = "活动所属组织名称")
    private String activityBelongToOrganizationName;

    /**
     * 活动所属学院
     */
    @Schema(description = "活动所属学院ID")
    private Long activityBelongToCollegeId;

    @Schema(description = "活动所属学院名称")
    private String activityBelongToCollegeName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 报名需审核
     */
    @Schema(description = "报名需审核")
    private Boolean enrollNeedReview;

    /**
     * 需要签退
     */
    @Schema(description = "需要签退")
    private Boolean needSignOut;

    /**
     * 附件
     */
    @Schema(description = "附件")
    private String attachment;

    /**
     * 分类
     */
    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 封面图片
     */
    @Schema(description = "封面图片")
    private String coverImg;

    /**
     * 是否付费活动
     */
    @Schema(description = "是否付费活动")
    private Boolean paidFlag;

    /**
     * 报名费（单位：分）
     */
    @Schema(description = "报名费（单位：分）")
    private Integer priceFen;

    /**
     * 退款政策：1-报名截止前可退 2-活动开始前可退 3-不可退款
     */
    @Schema(description = "退款政策：1-报名截止前可退 2-活动开始前可退 3-不可退款")
    private Integer refundPolicy;

    @Schema(description = "活动管理员和发起者ID")
    private Long activityManagerId;

    @Schema(description = "活动管理员")
    private PortalUserVO activityManager;

}
