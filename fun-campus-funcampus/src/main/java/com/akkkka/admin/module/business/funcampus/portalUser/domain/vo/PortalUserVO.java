package com.akkkka.admin.module.business.funcampus.portalUser.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 前端用户 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */

@Data
public class PortalUserVO {

    private Long id;

    private String username;

    private Boolean gender;

    private Long schoolId;

    private String schoolName;

    private Long collegeId;

    private String collegeName;

    private Boolean disableFlag;

    private String phone;

    private String avatar;

    private boolean canPublishActivity;

    private Long gradeId;

    private String gradeName;

}
