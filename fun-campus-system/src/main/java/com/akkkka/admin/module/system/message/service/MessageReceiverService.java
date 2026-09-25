package com.akkkka.admin.module.system.message.service;

import com.akkkka.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import com.akkkka.admin.module.system.backendUser.manager.BackendUserManager;
import com.akkkka.admin.module.system.message.domain.MessageReceiverQueryForm;
import com.akkkka.admin.module.system.message.domain.MessageReceiverVO;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.enumeration.UserTypeEnum;
import com.akkkka.common.exception.BusinessException;
import com.akkkka.common.util.SmartPageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息接收人查询服务：发送消息时按类型检索可选的接收人
 * <p>
 * 支持 后台用户(1) 与 前台用户(2，学生/组织者)；后台数据直查 backend_user，
 * 前台数据通过 {@link PortalMessageReceiverService} 由业务模块提供实现
 *
 * @Author akkkka114514
 * @Date 2026-09-22
 * @Copyright akkkka114514
 */
@Service
@AllArgsConstructor
public class MessageReceiverService {

    private final BackendUserManager backendUserManager;
    private final PortalMessageReceiverService portalMessageReceiverService;

    /**
     * 分页查询接收人
     */
    public PageResult<MessageReceiverVO> queryReceiverPage(MessageReceiverQueryForm queryForm) {
        if (UserTypeEnum.ADMIN_BACKEND_USER.getValue().equals(queryForm.getReceiverUserType())) {
            return queryBackendUser(queryForm);
        }
        if (UserTypeEnum.PORTAL_USER.getValue().equals(queryForm.getReceiverUserType())) {
            return portalMessageReceiverService.queryPortalReceiverPage(queryForm);
        }
        throw new BusinessException(UserErrorCode.PARAM_ERROR, "不支持的接收人类型");
    }

    private PageResult<MessageReceiverVO> queryBackendUser(MessageReceiverQueryForm queryForm) {
        Page<BackendUserEntity> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        LambdaQueryWrapper<BackendUserEntity> wrapper = new LambdaQueryWrapper<BackendUserEntity>()
                .select(BackendUserEntity::getId, BackendUserEntity::getUsername)
                .eq(BackendUserEntity::getDeletedFlag, false)
                .like(StringUtils.isNotBlank(queryForm.getKeyword()), BackendUserEntity::getUsername, queryForm.getKeyword())
                .orderByAsc(BackendUserEntity::getId);
        Page<BackendUserEntity> resultPage = backendUserManager.page(page, wrapper);
        List<MessageReceiverVO> list = resultPage.getRecords().stream().map(entity -> {
            MessageReceiverVO vo = new MessageReceiverVO();
            vo.setId(entity.getId());
            vo.setUserType(UserTypeEnum.ADMIN_BACKEND_USER.getValue());
            vo.setUsername(entity.getUsername());
            return vo;
        }).toList();
        return SmartPageUtil.convert2PageResult(resultPage, list);
    }
}
