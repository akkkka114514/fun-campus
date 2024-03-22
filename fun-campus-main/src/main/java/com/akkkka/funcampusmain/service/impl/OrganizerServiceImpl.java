package com.akkkka.funcampusmain.service.impl;

import com.akkkka.funcampusmainmodel.entity.College;
import com.akkkka.funcampusmainmodel.entity.Organizer;
import com.akkkka.funcampusmain.mapper.OrganizerMapper;
import com.akkkka.funcampusmainapi.api.ICollegeService;
import com.akkkka.funcampusmainapi.api.IOrganizerService;
import com.akkkka.funcampusutil.util.ExceptionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
/**
 * @author akkkka
 */
@DubboService
@Service
public class OrganizerServiceImpl extends ServiceImpl<OrganizerMapper, Organizer> implements IOrganizerService {
    @Resource
    private ICollegeService collegeService;

    /**
     * 添加organizer活动组织者，organizer必须是
     * 已注册的学校的已注册学院或学校组织
     * */
    @Override
    public void addOrganizer(Organizer organizer) {
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
    public void updateOrganizer(Organizer organizer) {
        Organizer toCheck = this.getById(organizer.getId());

        ExceptionUtil.throwIfNullInDb(toCheck,":organizer");
        ExceptionUtil.throwIfIsDeletedInDb(toCheck.getIsDeleted(),"organizer");

        organizer.setSchoolId(null);
        this.updateById(organizer);
    }

    @Override
    public void deleteOrganizer(Integer id) {
        Organizer organizer = new Organizer();
        organizer.setId(id);
        organizer.setIsDeleted((byte) 1);

        this.updateOrganizer(organizer);
    }


}
