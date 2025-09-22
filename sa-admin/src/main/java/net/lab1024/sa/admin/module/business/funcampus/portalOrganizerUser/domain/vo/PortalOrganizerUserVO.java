package net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import net.lab1024.sa.admin.module.system.backendUser.domain.vo.BackendUserVO;

/**
 * 组织账号运营者 列表VO
 *
 * @Author akkkka114514
 * @Date 2025-09-19 10:12:39
 * @Copyright akkkka114514
 */

@Data
public class PortalOrganizerUserVO extends BackendUserVO {

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "学校id")
    private String schoolId;

}
