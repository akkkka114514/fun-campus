package com.akkkka.funcampusportal.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.akkkka.funcampusportal.domain.User;
import com.amdelamar.jhash.exception.InvalidHashException;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: Zoe Wu
 * @create: 2025-03-07 21:53
 * @Description:
 */
public interface IUserService extends IService<User> {

    SaTokenInfo userPasswordLogin(String username, String password) throws InvalidHashException;

    void userPasswordRegister(User user);
}
