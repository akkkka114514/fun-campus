package com.akkkka.funcampusmain.service.impl;

import com.akkkka.funcampusmainmodel.entity.College;
import com.akkkka.funcampusmainmodel.entity.School;
import com.akkkka.funcampusmain.mapper.CollegeMapper;
import com.akkkka.funcampusmainapi.api.ICollegeService;
import com.akkkka.funcampusmainapi.api.ISchoolService;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
@DubboService
@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements ICollegeService {

    @Resource
    private ISchoolService schoolService;
    @Override
    public void addCollege(College college) {
        School toCheck = schoolService.getById(college.getSchoolId());

        ExceptionUtil.throwIfNullInDb(toCheck,"schoolId");
        college.setId(null);
        college.setIsDeleted((byte) 0);
        assert this.save(college);
    }


    @Override
    public void updateCollege(College college) {
        College toCheck = this.getById(college.getId());

        ExceptionUtil.throwIfNullInDb(toCheck,"college");
        ExceptionUtil.throwIfIsDeletedInDb(toCheck.getIsDeleted(),"college");

        college.setSchoolId(null);
        this.updateById(college);
    }

    @Override
    public void deleteCollege(Integer id) {
        College college = new College();
        college.setId(id);
        college.setIsDeleted((byte) 1);

        this.updateCollege(college);
    }


}
