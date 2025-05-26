package com.akkkka.funcampusportal.service.impl;

import com.akkkka.common.core.enums.ResponseEnum;
import com.akkkka.common.core.exception.GlobalException;
import com.akkkka.funcampusportal.domain.College;
import com.akkkka.funcampusportal.domain.Organizer;
import com.akkkka.funcampusportal.mapper.OrganizerMapper;
import com.akkkka.funcampusportal.service.ICollegeService;
import com.akkkka.funcampusportal.service.IOrganizerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author akkkka
 */
@Service
public class OrganizerServiceImpl extends ServiceImpl<OrganizerMapper, Organizer> implements IOrganizerService {
    @Resource
    private ICollegeService collegeService;

    /**
     * 添加organizer活动组织者，organizer必须是
     * 已注册的学校的已注册学院或学校组织
     * */
    @Override
    public void add(Organizer organizer) {
        College college=null;
        if(organizer.getCollegeId()!=null) {
            college = collegeService.getById(organizer.getCollegeId());
            if(college==null){
                throw new GlobalException(ResponseEnum.NO_SUCH_RECORD_IN_DB,"College");
            }
            if(!Objects.equals(organizer.getSchoolId(), college.getSchoolId())){
                throw new GlobalException(ResponseEnum.NOT_CORRESPOND_TO_RECORD_IN_DB);
            }
        }
        organizer.setId(null);
        organizer.setIsDeleted((byte)0);
        this.save(organizer);
    }

    @Override
    public void update(Organizer organizer) {
        Organizer toCheck = this.getById(organizer.getId());

        if(toCheck==null){
            throw new GlobalException(ResponseEnum.NO_SUCH_RECORD_IN_DB,"organizer");
        }
        if(toCheck.getIsDeleted()==1){
            throw new GlobalException(ResponseEnum.RECORD_DELETED_LOGICALLY,"organizer");
        }

        organizer.setSchoolId(null);
        this.updateById(organizer);
    }

    @Override
    public void delete(Integer id) {
        Organizer organizer = new Organizer();
        organizer.setId(id);
        organizer.setIsDeleted((byte) 1);

        this.update(organizer);
    }


}
