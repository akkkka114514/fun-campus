package com.akkkka.funcampusportal.service.impl;

import com.akkkka.funcampusportal.domain.School;
import com.akkkka.funcampusportal.mapper.SchoolMapper;
import com.akkkka.funcampusportal.service.ISchoolService;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements ISchoolService {

    @Override
    public void add(School school) {
        School toCheckSchool=this.getBaseMapper().selectOne(
                new QueryWrapper<School>().eq("school_name",school.getSchoolName()
                )
        );

        ExceptionUtil.throwIfExistsInDb(toCheckSchool);
        school.setIsDeleted((byte)0);
        this.save(school);
    }

    @Override
    public void update(School school) {
        School toCheck = this.getById(school.getId());

        ExceptionUtil.throwIfNullInDb(toCheck,":school");
        ExceptionUtil.throwIfIsDeletedInDb(toCheck.getIsDeleted(),":school");

        this.updateById(school);
    }

    @Override
    public void delete(Integer id) {
        School school = new School();
        school.setId(id);
        school.setIsDeleted((byte) 1);

        this.update(school);
    }
}
