package com.akkkka.funcampusmainapi.api;

import com.akkkka.funcampusmainmodel.entity.College;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
public interface ICollegeService extends IService<College> {

    void addCollege(College college);

    void updateCollege(College college);

    void deleteCollege(Integer id);

}
