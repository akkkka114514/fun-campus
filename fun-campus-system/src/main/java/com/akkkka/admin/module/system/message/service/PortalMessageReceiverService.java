package com.akkkka.admin.module.system.message.service;

import com.akkkka.admin.module.system.message.domain.MessageReceiverQueryForm;
import com.akkkka.admin.module.system.message.domain.MessageReceiverVO;
import com.akkkka.common.domain.PageResult;

/**
 * 前台用户消息接收人查询接口：由业务模块（funcampus）提供实现
 * <p>
 * system 模块不感知 portal_user 表结构，通过接口反向注入业务实现，避免 system → funcampus 模块依赖
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 */
public interface PortalMessageReceiverService {

    /**
     * 分页查询前台用户接收人
     */
    PageResult<MessageReceiverVO> queryPortalReceiverPage(MessageReceiverQueryForm queryForm);

}
