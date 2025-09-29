package net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 组织账号运营者 实体类
 *
 * @Author akkkka114514
 * @Date 2025-09-19 10:12:39
 * @Copyright akkkka114514
 */

@Data
@TableName("portal_organizer_user")
public class PortalOrganizerUserEntity extends BackendUserEntity {
    /**
     * 头像
     */
    private Long avatarFileId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 学校id
     */
    private Long schoolId;

}
