package com.akkkka.funcampussecurity.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.akkkka.funcampusutil.common.CommonResponse;
import com.akkkka.funcampusutil.common.ResponseEnum;
import com.akkkka.funcampusutil.util.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TokenAuthController {
    @RequestMapping("/tokenAuth")
    public CommonResponse<Void> tokenAuth(@RequestParam String token){
        if(token==null){
            return CommonResponse.fail(ResponseEnum.NOT_LOGIN_OR_TOKEN_EXPIRED);
        }
        Long loginId = (Long) StpUtil.getLoginIdByToken(token);
        if(loginId == null){
            return CommonResponse.fail(ResponseEnum.NOT_LOGIN_OR_TOKEN_EXPIRED);
        }
        return CommonResponse.success();
    }

}
