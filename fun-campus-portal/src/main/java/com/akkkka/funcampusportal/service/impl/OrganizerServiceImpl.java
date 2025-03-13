package com.akkkka.funcampusportal.service.impl;

import com.akkkka.funcampusportal.domain.College;
import com.akkkka.funcampusportal.domain.Organizer;
import com.akkkka.funcampusportal.mapper.OrganizerMapper;
import com.akkkka.funcampusportal.service.ICollegeService;
import com.akkkka.funcampusportal.service.IOrganizerService;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
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
            ExceptionUtil.throwIfNullInDb(college,":college");
            ExceptionUtil.throwIfNotCorrespondToRecordInDb(organizer.getSchoolId(),college.getSchoolId(),":school and college");
        }
        organizer.setId(null);
        organizer.setIsDeleted((byte)0);
        this.save(organizer);
    }

    @Override
    public void update(Organizer organizer) {
        Organizer toCheck = this.getById(organizer.getId());

        ExceptionUtil.throwIfNullInDb(toCheck,":organizer");
        ExceptionUtil.throwIfIsDeletedInDb(toCheck.getIsDeleted(),"organizer");

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
