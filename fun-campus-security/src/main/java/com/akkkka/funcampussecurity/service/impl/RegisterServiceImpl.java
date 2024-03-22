package com.akkkka.funcampussecurity.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.akkkka.funcampusmainapi.api.ICollegeService;
import com.akkkka.funcampusmainapi.api.IUserService;
import com.akkkka.funcampusmainmodel.entity.College;
import com.akkkka.funcampusmainmodel.entity.User;
import com.akkkka.funcampussecurityapi.api.IRegisterService;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author akkkka
 * 2023/12/12 19:47 created
 * */
@Service
public class RegisterServiceImpl implements IRegisterService {
    @DubboReference(check=false)
    private IUserService userService;
    @DubboReference(check=false)
    private ICollegeService collegeService;
    @Override
    public void passwordRegister(User user) {
        User toCheck = userService.getOne(
                new QueryWrapper<User>().eq("username", user.getUsername())
        );
        ExceptionUtil.throwIfExistsInDb(toCheck);
        //检查学校学员编号是否对应
        College tocheckCollege = collegeService.getById(user.getCollegeId());
        ExceptionUtil.throwIfNullInDb(tocheckCollege);
        ExceptionUtil.throwIfNotCorrespondToRecordInDb(tocheckCollege.getSchoolId(), user.getSchoolId());
        // 密码加盐加密
        String salt = UUID.randomUUID().toString().replace("-","");
        user.setSalt(salt);
        user.setPassword(DigestUtil.md5Hex(salt+user.getPassword()+salt));

        user.setId(null);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setFunScore(0.0f);
        user.setIsDeleted((byte)0);

        userService.save(user);
    }
}
