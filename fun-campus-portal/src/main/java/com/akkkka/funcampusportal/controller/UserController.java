package com.akkkka.funcampusportal.controller;

import cn.hutool.core.lang.Validator;
import com.akkkka.funcampusportal.domain.User;
import com.akkkka.funcampusportal.service.IUserService;
import com.akkkka.funcampusutil.util.CommonResponse;
import com.akkkka.funcampusutil.constant.ResponseEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "user")
public class UserController {
    @Resource
    private IUserService userService;

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
