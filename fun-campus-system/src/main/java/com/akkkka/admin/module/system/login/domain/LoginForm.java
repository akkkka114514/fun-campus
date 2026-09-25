package com.akkkka.admin.module.system.login.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.akkkka.module.support.captcha.domain.CaptchaForm;
import org.hibernate.validator.constraints.Length;

/**
 * 后台用户登录
 *
 * @Author 1024创新实验室: 开云
 * @Date 2021-12-19 11:49:45
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
public class LoginForm extends CaptchaForm {

    @Schema(description = "登录账号")
    @NotBlank(message = "登录账号不能为空")
    @Length(max = 30, message = "登录账号最多30字符")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Min(value = 1, message = "登录设备类型错误")
    @Max(value = 5, message = "登录设备类型错误")
    private Integer loginDevice;

    @Schema(description = "邮箱验证码")
    private String emailCode;
}
