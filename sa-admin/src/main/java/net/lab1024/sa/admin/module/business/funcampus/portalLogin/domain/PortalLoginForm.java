package net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.lab1024.sa.admin.module.system.login.domain.LoginForm;
import net.lab1024.sa.base.common.swagger.SchemaEnum;
import net.lab1024.sa.base.common.validator.enumeration.CheckEnum;
import net.lab1024.sa.base.constant.LoginDeviceEnum;
import org.hibernate.validator.constraints.Length;

/**
 * author:akkkka114514
 * create at 2025-10-10 10:12
 */
@Data
public class PortalLoginForm extends LoginForm {
}
