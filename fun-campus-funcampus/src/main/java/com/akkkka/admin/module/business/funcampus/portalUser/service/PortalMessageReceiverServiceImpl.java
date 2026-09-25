package com.akkkka.admin.module.business.funcampus.portalUser.service;

import com.akkkka.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.admin.module.system.message.domain.MessageReceiverQueryForm;
import com.akkkka.admin.module.system.message.domain.MessageReceiverVO;
import com.akkkka.admin.module.system.message.service.PortalMessageReceiverService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.util.SmartPageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前台用户消息接收人查询实现：供 system 模块消息服务按接收人类型检索 portal_user
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 */
@Service
@AllArgsConstructor
public class PortalMessageReceiverServiceImpl implements PortalMessageReceiverService {

    private final PortalUserManager portalUserManager;

    @Override
    public PageResult<MessageReceiverVO> queryPortalReceiverPage(MessageReceiverQueryForm queryForm) {
        Page<PortalUserEntity> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        LambdaQueryWrapper<PortalUserEntity> wrapper = new LambdaQueryWrapper<PortalUserEntity>()
                .select(PortalUserEntity::getId, PortalUserEntity::getUsername, PortalUserEntity::getPhone)
                .eq(PortalUserEntity::getDeletedFlag, false)
                .like(StringUtils.isNotBlank(queryForm.getKeyword()), PortalUserEntity::getUsername, queryForm.getKeyword())
                .orderByAsc(PortalUserEntity::getId);
        Page<PortalUserEntity> resultPage = portalUserManager.page(page, wrapper);
        List<MessageReceiverVO> list = resultPage.getRecords().stream().map(entity -> {
            MessageReceiverVO vo = new MessageReceiverVO();
            vo.setId(entity.getId());
            vo.setUserType(UserTypeEnum.PORTAL_USER.getValue());
            vo.setUsername(entity.getUsername());
            vo.setPhone(entity.getPhone());
            return vo;
        }).toList();
        return SmartPageUtil.convert2PageResult(resultPage, list);
    }

}
