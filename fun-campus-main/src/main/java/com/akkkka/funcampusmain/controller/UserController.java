package com.akkkka.funcampusmain.controller;

import cn.hutool.core.lang.Validator;
import com.akkkka.funcampusmainmodel.entity.User;
import com.akkkka.funcampusmainapi.api.IUserService;
import com.akkkka.funcampusutil.common.CommonResponse;
import com.akkkka.funcampusutil.common.ResponseEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;

    @GetMapping("/sendCode")
    public CommonResponse<Void> sendSmsLoginCode(@RequestParam String phone){
        if (!Validator.isMobile(phone)){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        //smsUserService.sendSmsLoginCode(phone);
        return CommonResponse.success();
    }

    @GetMapping("/registerOrLogin")
    public CommonResponse<User> registerOrLogin(@RequestParam String phone,
                                                @RequestParam String smsCode) {
        if (!Validator.isMobile(phone)){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        User user = new User();
        user.setPhone(phone);
        //return CommonResponse.success(smsUserService.registerOrLogin(user,smsCode));
        return null;
    }

    @GetMapping("/getByName")
    public CommonResponse<User> getByName(@RequestParam String username) {
        if (username == null || username.isEmpty()){
            return CommonResponse.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        return CommonResponse.success(userService.getOne(
                new QueryWrapper<User>().eq("username",username)
                )
        );
    }
}
