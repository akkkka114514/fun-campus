package com.akkkka.system.controller;

import com.akkkka.common.security.annotation.RequiresPermissions;
import com.akkkka.common.security.utils.SecurityUtils;
import com.akkkka.system.feign.StepUpAuthFeign;
import com.akkkka.system.service.IPortalCommentService;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: akkkka114514
 * @create: 2025-06-23 15:42
 * @description:
 */
@Service
public class PortalCommentController implements IPortalCommentService {
    @Resource
    private IPortalCommentService portalCommentService;
    @Resource
    private StepUpAuthFeign stepUpAuthFeign;

    @Override
    @RequiresPermissions("system:comment:delete")
    public void deleteComment(Integer commentId) {
        stepUpAuthFeign.openStepUpAuth();

    }
}
