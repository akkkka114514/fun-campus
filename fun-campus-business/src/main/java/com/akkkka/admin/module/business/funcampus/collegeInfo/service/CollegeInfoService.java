package com.akkkka.admin.module.business.funcampus.collegeInfo.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.vo.CollegeInfoVO;
import com.akkkka.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import com.akkkka.common.domain.IdNameVO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.akkkka.admin.module.business.funcampus.collegeInfo.dao.CollegeInfoDao;
import com.akkkka.admin.module.business.funcampus.collegeInfo.domain.entity.CollegeInfoEntity;
import com.akkkka.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * 学院信息 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:45:43
 * @Copyright akkkka114514
 */

@Service
@Slf4j
@AllArgsConstructor
public class CollegeInfoService {
    private final CollegeInfoManager collegeInfoManager;


    public String getNameById(Long id){
        CollegeInfoEntity collegeInfo = collegeInfoManager.getById(id);

        AssertUtil.ifTrueThrowParamError(Objects.isNull(collegeInfo));

        return collegeInfo.getName();
    }

    public List<IdNameVO> getIdNameBySchoolId(Long schoolId){
        List<CollegeInfoEntity> list = collegeInfoManager.list(
                Wrappers.lambdaQuery(CollegeInfoEntity.class)
                        .eq(CollegeInfoEntity::getSchoolId,schoolId)
                        .eq(CollegeInfoEntity::getDeletedFlag,false)
                        .select(CollegeInfoEntity::getId)
                        .select(CollegeInfoEntity::getName)
        );
        List<IdNameVO> result = new LinkedList<>();
        if(list.isEmpty()){
            return result;
        }
        for(CollegeInfoEntity e:list){
            IdNameVO vo = new IdNameVO();
            vo.setId(e.getId());
            vo.setName(e.getName());
            result.add(vo);
        }
        return result;
    }

    public List<Long> getIdsBySchoolId(Long schoolId){
        return collegeInfoManager.list(
                Wrappers.lambdaQuery(CollegeInfoEntity.class)
                        .eq(CollegeInfoEntity::getSchoolId,schoolId)
                        .eq(CollegeInfoEntity::getDeletedFlag,false)
                        .select(CollegeInfoEntity::getId)
        ).stream().map(CollegeInfoEntity::getId).toList();
    }
}
