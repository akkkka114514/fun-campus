package com.akkkka.system.service.impl;

import com.akkkka.common.core.enums.ResponseEnum;
import com.akkkka.common.core.exception.GlobalException;
import com.akkkka.common.redis.service.RedisService;
import com.akkkka.common.security.auth.AuthUtil;
import com.akkkka.common.security.utils.SecurityUtils;
import com.akkkka.system.feign.PortalCommentFeign;
import com.akkkka.system.feign.StepUpAuthFeign;
import com.akkkka.system.service.IPortalCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: akkkka114514
 * @create: 2025-06-21 16:20
 * @description:
 */
@Service
public class PortalCommentServiceImpl implements IPortalCommentService {

    @Resource
    private RedisService redis;
    @Autowired
    private PortalCommentFeign portalCommentFeign;
    @Qualifier("com.akkkka.system.feign.StepUpAuthFeign")
    @Autowired
    private StepUpAuthFeign stepUpAuthFeign;

    /** 
    * <p>
    * description: 
    * </p>
    *
    * @param commentId
    * @return:  
    * @author: akkkka114514
    * @date: 16:37:08 2025-06-21
    */ 
    @Override

    public void deleteComment(Integer commentId) {
        String prefix = stepUpAuthFeign.getStepUpAuthRedisKeyPrefix().getData();
        String token = SecurityUtils.getToken();
        //二次确认
        if(redis.getCacheObject(prefix + token)==null){
            throw new GlobalException(ResponseEnum.AUTHENTICATION_FAILED);
        }
        //删除
        portalCommentFeign.delete(commentId);
        //TODO 发通知
    }
}
