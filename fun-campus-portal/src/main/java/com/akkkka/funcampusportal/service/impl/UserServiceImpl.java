package com.akkkka.funcampusportal.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.akkkka.funcampusportal.domain.College;
import com.akkkka.funcampusportal.domain.User;
import com.akkkka.funcampusportal.mapper.UserMapper;
import com.akkkka.funcampusportal.service.ICollegeService;
import com.akkkka.funcampusportal.service.IUserService;
import com.akkkka.funcampusutil.constant.ResponseEnum;
import com.akkkka.funcampusutil.util.CustomException;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.amdelamar.jhash.Hash;
import com.amdelamar.jhash.algorithms.Type;
import com.amdelamar.jhash.exception.InvalidHashException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author akkkka
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private ICollegeService collegeService;

    @Override
    public SaTokenInfo userPasswordLogin(String username, String password) throws InvalidHashException {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        User user = this.getOne(lambdaQueryWrapper.eq(User::getUsername,username));
        ExceptionUtil.throwIfNullInDb(user);
        ExceptionUtil.throwIfIsDeletedInDb(user.getIsDeleted());

        if(!Hash.password(password.toCharArray()).algorithm(Type.SCRYPT).verify(user.getPassword())){
            throw new CustomException(ResponseEnum.PASSWORD_WRONG);
        }

        StpUtil.login(user.getId());
        // 设置token过期时间为一天
        StpUtil.getTokenInfo().setTokenTimeout(60 * 60 * 24);
        return StpUtil.getTokenInfo();
    }

    @Override
    public void userPasswordRegister(User user) {
        User toCheck = this.getOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername())
        );
        ExceptionUtil.throwIfExistsInDb(toCheck);
        //检查学校学员编号是否对应
        College toCheckCollege = collegeService.getById(user.getCollegeId());
        ExceptionUtil.throwIfNullInDb(toCheckCollege);
        ExceptionUtil.throwIfNotCorrespondToRecordInDb(toCheckCollege.getSchoolId(), user.getSchoolId());
        // 密码加盐加密
        user.setPassword(Hash.password(user.getPassword()
                .toCharArray())
                .algorithm(Type.SCRYPT)
                .create());

        user.setId(null);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setFunScore(0.0f);
        user.setIsDeleted((byte)0);

        this.save(user);
    }
}
