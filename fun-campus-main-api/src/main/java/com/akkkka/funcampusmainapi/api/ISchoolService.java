package com.akkkka.funcampusmainapi.api;

import com.akkkka.funcampusmainmodel.entity.School;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
public interface ISchoolService extends IService<School> {

    void addSchool(School school);

    void updateSchool(School school);

    void deleteSchool(Integer id);

}
