package com.akkkka.funcampusportal.mapper;

import com.akkkka.funcampusportal.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
public interface UserMapper extends BaseMapper<User> {
    User getUserByUsername(String username);
}
