package net.lab1024.sa.admin.module.business.funcampus.portalLogin.controller;

import cn.hutool.extra.servlet.JakartaServletUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain.PortalLoginForm;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain.PortalLoginResultVO;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.service.PortalLoginService;
import net.lab1024.sa.admin.module.system.login.service.LoginService;
import net.lab1024.sa.base.common.annoation.NoNeedLogin;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.captcha.domain.CaptchaVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * author:akkkka114514
 * create at 2025-10-10 10:14
 */
@RestController
@Tag(name = "Portal用户登录")
public class PortalLoginController {

    @Resource
    private PortalLoginService portalUserLoginService;
    @Resource
    private LoginService loginService;

    @Operation(summary = "Portal用户登录")
    @PostMapping("/portal/login")
    @NoNeedLogin
    public ResponseDTO<PortalLoginResultVO> login(@RequestBody @Valid PortalLoginForm loginForm, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String ip = JakartaServletUtil.getClientIP(request);
        return portalUserLoginService.login(loginForm, ip, userAgent);
    }

    @Operation(summary = "Portal用户退出登录")
    @PostMapping("/portal/logout")
    public ResponseDTO<String> logout() {
        return portalUserLoginService.logout(SmartRequestUtil.getRequestUser());
    }

    @Operation(summary = "获取验证码")
    @GetMapping("/portal/login/getCaptcha")
    @NoNeedLogin
    public ResponseDTO<CaptchaVO> getCaptcha() {
        return loginService.getCaptcha();
    }
}
