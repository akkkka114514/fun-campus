package com.akkkka.funcampusmain.service.impl;

import com.akkkka.funcampusmainmodel.entity.User;
import com.akkkka.funcampusmain.mapper.UserMapper;
import com.akkkka.funcampusmainapi.api.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @author akkkka
 */
@Service
@DubboService
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User getUserByUsername(String username) {
        return this.getBaseMapper().getUserByUsername(username);
    }
}
