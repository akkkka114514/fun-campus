package com.akkkka.funcampusmainapi.api;


import com.akkkka.funcampusmainmodel.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
public interface IUserService extends IService<User> {
    User getUserByUsername(String username);
}
