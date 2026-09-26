package com.akkkka.admin.module.business.funcampus.tribe.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 部落成员 列表VO（门户）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Data
public class TribeMemberVO {

    @Schema(description = "门户用户id")
    private Long portalUserId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像（文件key）")
    private String avatar;

    @Schema(description = "加入时间")
    private LocalDateTime createTime;

}
