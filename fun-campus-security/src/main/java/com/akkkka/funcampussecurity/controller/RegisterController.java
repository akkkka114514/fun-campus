package com.akkkka.funcampussecurity.controller;

import cn.hutool.core.lang.Validator;

import com.akkkka.funcampusmainmodel.entity.User;
import com.akkkka.funcampussecurityapi.api.IRegisterService;
import com.akkkka.funcampusutil.common.CommonResponse;
import com.akkkka.funcampusutil.common.ResponseEnum;
import javax.annotation.Resource;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author akkkka
 * 2023/12/12 19:51 createds
 * */
@RestController
@RefreshScope
public class RegisterController {
    @Resource
    private IRegisterService registerService;
    @RequestMapping("/sso/passwd-register")
    public CommonResponse<Void> passwordRegister(@RequestBody User user){
        if(!Validator.isMobile(user.getPhone())){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        registerService.passwordRegister(user);
        return CommonResponse.success();
    }
}
