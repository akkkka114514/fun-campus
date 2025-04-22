package com.akkkka.funcampusportal.util;

import com.akkkka.common.core.enums.ResponseEnum;
import com.akkkka.common.core.exception.GlobalException;

/**
*@author: akkkka114514
*@create: 2025-04-21 16:16
*@description: 
*/
public class ParamCheckUtil {
    public static void checkPositiveInteger(Integer value){
        if (value==null || value < 0){
            throw new GlobalException(ResponseEnum.PARAM_NOT_VALIDATE);
        }
    }

    public static void checkPositiveInteger(Integer... values){
        for(Integer value:values) {
            if (value == null || value < 0) {
                throw new GlobalException(ResponseEnum.PARAM_NOT_VALIDATE);
            }
        }
    }
}
