package com.akkkka.funcampussecurity.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.DigestUtil;

import com.akkkka.funcampusmainapi.api.IUserService;
import com.akkkka.funcampusmainmodel.entity.User;
import com.akkkka.funcampussecurityapi.api.ILoginService;
import com.akkkka.funcampusutil.common.ResponseEnum;
import com.akkkka.funcampusutil.util.CustomException;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * @author akkkka
 * 2023/12/10 18:46 created
 * */
@Service
public class LoginServiceImpl implements ILoginService {

    @DubboReference(check=false)
    private IUserService userService;

    @Resource
    private PermissionServiceImpl permissionService;

    /**
     * 如果用户名和密码正确，返回token value，用于saToken 登录
     * 否则抛出异常
     * */
    @Override
    public Map<String, String> passwordLogin(String username, String password) {
        User user = this.checkPassword(username, password);

        StpUtil.login(user.getId());
        // 设置token过期时间为一天
        StpUtil.getTokenInfo().setTokenTimeout(60 * 60 * 24);
        Map<String,String> map = new HashMap<>();
        map.put("satoken",StpUtil.getTokenValue());
        map.put("userId",String.valueOf(user.getId()));
        map.put("username",user.getUsername());
        map.put("schoolId",String.valueOf(user.getSchoolId()));
        map.put("collegeId",String.valueOf(user.getCollegeId()));
        map.put("funScore",String.valueOf(user.getFunScore()));
        map.put("phone",user.getPhone());
        return map;
    }

    @Override
    public User checkPassword(String username, String password) {
        User user = userService.getUserByUsername(username);
        ExceptionUtil.throwIfNullInDb(user);
        ExceptionUtil.throwIfIsDeletedInDb(user.getIsDeleted());

        String salt = user.getSalt();
        // 加密密码
        String encodedToCheck = DigestUtil.md5Hex(salt + password + salt);

        if(!encodedToCheck.equals(user.getPassword())){
            throw new CustomException(ResponseEnum.PASSWORD_WRONG);
        }
        return user;
    }

    @Override
    public void managerLoginVerify(String token, String passwordBase64) {
        Integer id = (Integer) StpUtil.getLoginIdByToken(token);
        User user = userService.getById(id);
        String password = Base64.decodeStr(passwordBase64);

        String encodedToCheck = DigestUtil.md5Hex(user.getSalt() + password + user.getSalt());
        if(!encodedToCheck.equals(user.getPassword())){
            throw new CustomException(ResponseEnum.PASSWORD_WRONG);
        }
        if(permissionService.getPermissionList(id, null).isEmpty()){
            throw new CustomException(ResponseEnum.PERMISSION_DENIED);
        }
    }


}
