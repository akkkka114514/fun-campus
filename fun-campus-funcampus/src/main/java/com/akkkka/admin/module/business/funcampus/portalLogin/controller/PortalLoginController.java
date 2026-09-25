package com.akkkka.admin.module.business.funcampus.portalLogin.controller;

import cn.hutool.extra.servlet.JakartaServletUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.PortalLoginForm;
import com.akkkka.admin.module.business.funcampus.portalLogin.domain.PortalLoginResultVO;
import com.akkkka.admin.module.business.funcampus.portalLogin.service.PortalLoginService;
import com.akkkka.common.annoation.NoNeedLogin;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.util.SmartRequestUtil;
import com.akkkka.module.support.captcha.domain.CaptchaVO;
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
        return portalUserLoginService.getCaptcha();
    }
}
