package com.akkkka.funcampusportal.service;

import com.akkkka.funcampusportal.domain.College;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: Zoe Wu
 * @create: 2025-03-07 21:52
 * @Description:
 */
public interface ICollegeService extends IService<College> {

    void add(College college);

    void update(College college);

    void delete(Integer id);
}
