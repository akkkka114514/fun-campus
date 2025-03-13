package com.akkkka.funcampusportal.service;

import com.akkkka.funcampusportal.domain.School;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: Zoe Wu
 * @create: 2025-03-07 21:53
 * @Description:
 */
public interface ISchoolService extends IService<School> {
    void add(School school);

    void update(School school);

    void delete(Integer id);
}
