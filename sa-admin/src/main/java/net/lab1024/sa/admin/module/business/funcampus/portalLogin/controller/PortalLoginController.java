package net.lab1024.sa.admin.module.business.funcampus.portalLogin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain.PortalLoginForm;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.domain.PortalLoginResultVO;
import net.lab1024.sa.admin.module.business.funcampus.portalLogin.service.PortalLoginService;
import net.lab1024.sa.base.common.annoation.NoNeedLogin;
import net.lab1024.sa.base.common.domain.ResponseDTO;
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
        return portalUserLoginService.login(loginForm, request);
    }

    @Operation(summary = "Portal用户退出登录")
    @PostMapping("/portal/logout")
    public ResponseDTO<String> logout() {
        return portalUserLoginService.logout();
    }
}
