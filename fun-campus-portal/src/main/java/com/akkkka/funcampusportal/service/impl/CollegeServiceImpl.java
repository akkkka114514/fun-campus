package com.akkkka.funcampusportal.service.impl;

import com.akkkka.funcampusportal.domain.College;
import com.akkkka.funcampusportal.domain.School;
import com.akkkka.funcampusportal.mapper.CollegeMapper;
import com.akkkka.funcampusportal.service.ICollegeService;
import com.akkkka.funcampusportal.service.ISchoolService;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author akkkka
 * @since 2023-10-03
 */
@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements ICollegeService {

    @Resource
    private ISchoolService schoolService;
    @Override
    public void add(College college) {
        School toCheck = schoolService.getById(college.getSchoolId());

        ExceptionUtil.throwIfNullInDb(toCheck,"schoolId");
        college.setId(null);
        college.setIsDeleted((byte) 0);
        assert this.save(college);
    }


    @Override
    public void update(College college) {
        College toCheck = this.getById(college.getId());

        ExceptionUtil.throwIfNullInDb(toCheck,"college");
        ExceptionUtil.throwIfIsDeletedInDb(toCheck.getIsDeleted(),"college");

        college.setSchoolId(null);
        this.updateById(college);
    }

    @Override
    public void delete(Integer id) {
        College college = new College();
        college.setId(id);
        college.setIsDeleted((byte) 1);

        this.update(college);
    }


}
