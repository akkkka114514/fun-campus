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
import com.akkkka.common.domain.ValidateList;
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

    public PageResult<PortalUserVO> queryPage(PortalUserQueryForm queryForm) {
        Page<PortalUserVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<PortalUserVO> list = portalUserManager.getBaseMapper().queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    public ResponseDTO<String> add(PortalUserAddForm addForm) {
        PortalUserEntity entity = SmartBeanUtil.copy(addForm, PortalUserEntity.class);
        entity.setDeletedFlag(false);
        portalUserManager.save(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> update(PortalUserUpdateForm updateForm) {
        PortalUserEntity entity = SmartBeanUtil.copy(updateForm, PortalUserEntity.class);
        portalUserManager.updateById(entity);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> batchDelete(ValidateList<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        portalUserManager.getBaseMapper().batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> delete(Long id) {
        if (id == null) {
            return ResponseDTO.ok();
        }
        portalUserManager.getBaseMapper().updateDeleted(id, true);
        return ResponseDTO.ok();
    }

}