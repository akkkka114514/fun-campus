package com.akkkka.funcampusportal.service.impl;

import com.akkkka.common.core.enums.ResponseEnum;
import com.akkkka.common.core.exception.GlobalException;
import com.akkkka.funcampusportal.domain.School;
import com.akkkka.funcampusportal.mapper.SchoolMapper;
import com.akkkka.funcampusportal.service.ISchoolService;
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

        if(toCheckSchool!=null){
            throw new GlobalException(ResponseEnum.EXISTS_IN_DB,"school");
        }
        school.setIsDeleted((byte)0);
        this.save(school);
    }

    @Override
    public void update(School school) {
        School toCheck = this.getById(school.getId());

        if(toCheck==null){
            throw new GlobalException(ResponseEnum.NO_SUCH_RECORD_IN_DB,"school");
        }
        if(toCheck.getIsDeleted()==1){
            throw new GlobalException(ResponseEnum.RECORD_DELETED_LOGICALLY,"school");
        }

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
