package com.akkkka.admin.module.business.funcampus.tribe.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.tribe.manager.TribeManager;
import com.akkkka.admin.module.business.funcampus.util.AssertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.business.funcampus.tribe.dao.TribeDao;
import com.akkkka.admin.module.business.funcampus.tribe.domain.entity.TribeEntity;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeAddForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeUpdateForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.SimpleTribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeVO;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akkkka.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 部落 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@Service
@Slf4j
@AllArgsConstructor
public class TribeService {
    private final TribeManager tribeManager;

    public String getNameById(Long id){
        TribeEntity entity = tribeManager.getById(id);
        AssertUtil.ifTrueThrowParamError(Objects.isNull(entity));
        return entity.getName();
    }

}
