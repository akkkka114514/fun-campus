package com.akkkka.funcampusportal.controller;

import com.akkkka.common.core.enums.ResponseEnum;
import com.akkkka.funcampusportal.domain.User;
import com.akkkka.funcampusportal.service.IUserService;
import com.akkkka.common.core.domain.R;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "user")
public class UserController {
    @Resource
    private IUserService userService;

//    @GetMapping("/registerOrLogin")
//    public R<User> registerOrLogin(@RequestParam String phone,
//                                                @RequestParam String smsCode) {
//        if (!Validator.isMobile(phone)){
//            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
//        }
//        User user = new User();
//        user.setPhone(phone);
//        //return R.ok(smsUserService.registerOrLogin(user,smsCode));
//        return null;
//    }

    @GetMapping("/getByName")
    public R<User> getByName(@RequestParam String username) {
        if (username.isEmpty()){
            return R.fail(ResponseEnum.PARAM_NOT_VALIDATE);
        }
        return R.ok(userService.getOne(
                new QueryWrapper<User>().eq("username",username)
                )
        );
    }
}
