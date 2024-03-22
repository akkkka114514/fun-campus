package com.akkkka.funcampussecurity.controller;


import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Validator;
import com.akkkka.funcampusmainmodel.entity.User;
import com.akkkka.funcampussecurityapi.api.ILoginService;
import com.akkkka.funcampusutil.common.CommonResponse;
import com.akkkka.funcampusutil.common.ResponseEnum;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.akkkka.funcampusutil.util.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author akkkka
 * created at 2024/1/5
 * */
@RestController
@RequestMapping("/sso")
@RefreshScope
@Slf4j
public class LoginController {
    @Resource
    private ILoginService loginService;

    /*
     * SSO-Server端：处理所有SSO相关请求 (下面的章节我们会详细列出开放的接口)
     */
    @RequestMapping("/*")
    public Object ssoRequest() {
        return SaSsoProcessor.instance.serverDister();
    }
    @PostMapping("/passwd-login")
    public CommonResponse<Map<String,String>> login(@RequestParam String username, @RequestParam String password) {
        if(Validator.isEmpty(username) || Validator.isEmpty(password)){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        log.info("receive request with username:{} and password:{}",username,password);
        Map<String,String> map= loginService.passwordLogin(username, password);
        return CommonResponse.success(map);
    }

    @RequestMapping("/check-login")
    public CommonResponse<Void> checkLogin(@RequestParam String token){
        if(Validator.isEmpty(token)){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        boolean isLogin = StpUtil.isLogin();
        if (isLogin){
            return CommonResponse.success();
        }
        return CommonResponse.fail(ResponseEnum.NOT_LOGIN_OR_TOKEN_EXPIRED);
    }

    //二级认证校验
//    @RequestMapping("/open-safe")
//    public CommonResponse<Void> openSafe(@RequestParam String username, @RequestParam String password){
//        if(StpUtil.isSafe()){
//            return CommonResponse.fail(ResponseEnum.OPERATION_DUPLICATED);
//        }
//        if(Validator.isEmpty(password) || Validator.isEmpty(username)){
//            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
//        }
//        loginService.checkPassword(username , password);
//        StpUtil.openSafe(180);
//        return CommonResponse.success();
//    }

    @RequestMapping("/manager-login")
    public CommonResponse<Void> ManagerLogin(ServerHttpRequest request, @RequestParam String password){
        if(Validator.isEmpty(password)){
            throw new CustomException(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        loginService.managerLoginVerify(getTokenFromRequest(request), password);
        return CommonResponse.success();
    }
    public String getTokenFromRequest(ServerHttpRequest request){
        List<String> cookies = request.getHeaders().get("Cookie");
        if(Objects.isNull(cookies)){
            return null;
        }
        for (String cookie : cookies) {
            if (cookie.contains("satoken=")) {
                return cookie.split("=")[1];
            }
        }
        return null;
    }
}
