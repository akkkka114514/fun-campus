package com.akkkka.admin.module.business.funcampus.schoolInfo.service;

import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.schoolInfo.dao.SchoolInfoDao;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.entity.SchoolInfoEntity;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoAddForm;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoQueryForm;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoUpdateForm;
import com.akkkka.admin.module.business.funcampus.schoolInfo.domain.vo.SchoolInfoVO;
import com.akkkka.admin.module.business.funcampus.schoolInfo.manager.SchoolInfoManager;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import com.akkkka.common.exception.BusinessException;
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
 * 学校信息表 Service
 *
 * @Author akkkka114514
 * @Date 2025-09-23 08:34:37
 * @Copyright akkkka114514
 */
@Slf4j
@Service
@AllArgsConstructor
public class SchoolInfoService {
    private final SchoolInfoManager schoolInfoManager;

    public String getNameById(Long id){
        SchoolInfoEntity entity = schoolInfoManager.getById(id);

        AssertUtil.ifTrueThrowParamError(Objects.isNull(entity));

        return entity.getName();
    }


}