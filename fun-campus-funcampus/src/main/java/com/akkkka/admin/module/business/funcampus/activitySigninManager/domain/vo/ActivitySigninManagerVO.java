package com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.vo;

import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动签到管理员 列表VO
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:10:19
 * @Copyright akkkka114514
 */

@Data
public class ActivitySigninManagerVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "活动主键")
    private Long activityId;

    @Schema(description = "活动签到员主键")
    private PortalUserVO portalUserVO;

    @Schema(description = "是否已删除")
    private Boolean deletedFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    public static ActivitySigninManagerVO convert(ActivitySigninManagerEntity signinManager,PortalUserVO portalUserVO){
        ActivitySigninManagerVO vo = new ActivitySigninManagerVO();
        vo.setId(signinManager.getId());
        vo.setActivityId(signinManager.getActivityId());
        vo.setPortalUserVO(portalUserVO);
        vo.setDeletedFlag(false);
        vo.setCreateTime(signinManager.getCreateTime());
        vo.setUpdateTime(signinManager.getUpdateTime());

        return vo;
    }

}
