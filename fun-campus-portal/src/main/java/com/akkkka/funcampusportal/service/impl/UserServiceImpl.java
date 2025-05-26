package com.akkkka.funcampusportal.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.akkkka.common.core.enums.ResponseEnum;
import com.akkkka.common.core.exception.GlobalException;
import com.akkkka.funcampusportal.domain.College;
import com.akkkka.funcampusportal.domain.User;
import com.akkkka.funcampusportal.mapper.UserMapper;
import com.akkkka.funcampusportal.service.ICollegeService;
import com.akkkka.funcampusportal.service.IUserService;
import com.amdelamar.jhash.Hash;
import com.amdelamar.jhash.algorithms.Type;
import com.amdelamar.jhash.exception.InvalidHashException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

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
        if(user==null){
            throw new GlobalException(ResponseEnum.NO_SUCH_RECORD_IN_DB,"user");
        }
        if(user.getIsDeleted()==1){
            throw new GlobalException(ResponseEnum.RECORD_DELETED_LOGICALLY,"user");
        }

        if(!Hash.password(password.toCharArray()).algorithm(Type.SCRYPT).verify(user.getPassword())){
            throw new GlobalException(ResponseEnum.PASSWORD_WRONG);
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
        if(toCheck!=null){
            throw new GlobalException(ResponseEnum.EXISTS_IN_DB,"user");
        }
        //检查学校学员编号是否对应
        College toCheckCollege = collegeService.getById(user.getCollegeId());
        if(toCheckCollege==null){
            throw new GlobalException(ResponseEnum.NO_SUCH_RECORD_IN_DB,"college");
        }
        if(!Objects.equals(toCheckCollege.getSchoolId(), user.getSchoolId())){
            throw new GlobalException(ResponseEnum.NOT_CORRESPOND_TO_RECORD_IN_DB);
        }
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
