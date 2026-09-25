package com.akkkka.admin.module.business.funcampus.portalUser.domain.vo;

import com.akkkka.common.domain.IdNameVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 简要门户用户 VO（含头像）
 *
 * @Author akkkka114514
 * @Date 2025-10-02
 * @Copyright akkkka114514
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SimplePortalUserVO extends IdNameVO {

    /** 头像Key */
    private String avatarKey;
}
