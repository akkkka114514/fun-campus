package com.akkkka.admin.module.business.funcampus.portalUser.service;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
import com.akkkka.admin.module.business.funcampus.gradeInfo.service.GradeInfoService;
import com.akkkka.admin.module.business.funcampus.portalUser.dao.PortalUserDao;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.form.PortalUserAddForm;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.form.PortalUserQueryForm;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.form.PortalUserUpdateForm;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.schoolInfo.service.SchoolInfoService;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;

/**
 * 前端用户 Service
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class PortalUserService {
    private final PortalUserManager portalUserManager;
    private final SchoolInfoService schoolInfoService;
    private final CollegeInfoService collegeInfoService;
    private final GradeInfoService gradeInfoService;

    public PortalUserVO getById(Long id){
        PortalUserEntity entity = portalUserManager.getById(id);
        PortalUserVO vo = new PortalUserVO();
        vo.setId(id);
        vo.setUsername(entity.getUsername());
        vo.setGender(entity.getGender());
        vo.setSchoolId(entity.getSchoolId());
        vo.setSchoolName(schoolInfoService.getNameById(entity.getSchoolId()));
        vo.setCollegeId(entity.getCollegeId());
        vo.setCollegeName(collegeInfoService.getNameById(entity.getCollegeId()));
        vo.setDisableFlag(entity.getDisableFlag());
        vo.setPhone(entity.getPhone());
        vo.setAvatar(entity.getAvatar());
        vo.setCanPublishActivity(entity.getCanPublishActivity());
        vo.setGradeId(entity.getGradeId());
        vo.setGradeName(gradeInfoService.getNameById(entity.getGradeId()));
        return vo;
    }

}