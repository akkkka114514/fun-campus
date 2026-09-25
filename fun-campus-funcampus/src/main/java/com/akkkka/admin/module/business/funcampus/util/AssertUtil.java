package com.akkkka.admin.module.business.funcampus.util;

import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.exception.BusinessException;
import org.apache.catalina.User;

/**
 * author:akkkka114514
 * create at 2026-05-07 15:39
 */
public class AssertUtil {
    public static void ifTrueThrowParamError(Boolean condition){
        if(condition) throw new BusinessException(UserErrorCode.PARAM_ERROR);
    }
    public static void ifTrueThrowSysError(Boolean condition){
        if(condition) throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
    }
    public static void ifFalseThrowSysError(Boolean condition){
        if(!condition) throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
    }
}
