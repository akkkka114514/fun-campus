package com.akkkka.admin.module.business.funcampus.gradeInfo.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.gradeInfo.dao.GradeInfoDao;
import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.entity.GradeInfoEntity;
import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.form.GradeInfoAddForm;
import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.form.GradeInfoQueryForm;
import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.form.GradeInfoUpdateForm;
import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.vo.GradeInfoVO;
import com.akkkka.admin.module.business.funcampus.gradeInfo.domain.vo.SimpleGradeInfoVO;
import com.akkkka.admin.module.business.funcampus.gradeInfo.manager.GradeInfoManager;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import com.akkkka.common.domain.IdNameVO;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 年级信息 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:47:10
 * @Copyright akkkka114514
 */

@Slf4j
@Service
@AllArgsConstructor
public class GradeInfoService {

    private final GradeInfoManager gradeInfoManager;

    /**
     * 获取所有
     */
    public List<IdNameVO> getAll() {
        List<GradeInfoEntity> list = gradeInfoManager.list();
        List<IdNameVO> result = new LinkedList<>();
        for(GradeInfoEntity e:list){
            IdNameVO vo = new IdNameVO();
            vo.setId(e.getId());
            vo.setName(e.getName());
            result.add(vo);
        }
        return result;
    }

    public String getNameById(Long id){
        GradeInfoEntity entity = gradeInfoManager.getById(id);
        AssertUtil.ifTrueThrowParamError(Objects.isNull(entity));
        return entity.getName();
    }
}
