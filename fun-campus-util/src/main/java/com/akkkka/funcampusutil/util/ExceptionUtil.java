package com.akkkka.funcampusutil.util;


import com.akkkka.funcampusutil.common.CommonResponse;
import com.akkkka.funcampusutil.common.ResponseEnum;

import java.util.Objects;

/**
 * @author akkkka
 * 2023/10/7
 * 自定义异常工具类
 * */
public class ExceptionUtil {
    public static void throwIfNullInDb(Object toCheck, String moreInfo){
        if(toCheck == null){
            throw new CustomException(ResponseEnum.NO_SUCH_RECORD_IN_DB, moreInfo);
        }
    }

    public static void throwIfNullInDb(Object toCheck){
        if(toCheck == null){
            throw new CustomException(ResponseEnum.NO_SUCH_RECORD_IN_DB);
        }
    }

    public static void throwIfIsDeletedInDb(Byte isDeleted){
        if(isDeleted == 1){
            throw new CustomException(ResponseEnum.RECORD_DELETED_LOGICALLY);
        }
    }

    public static void throwIfIsDeletedInDb(Byte isDeleted, String moreInfo){
        if(isDeleted == 1){
            throw new CustomException(ResponseEnum.RECORD_DELETED_LOGICALLY, moreInfo);
        }
    }

    public static void throwIfExistsInDb(Object toCheck){
        if (toCheck != null){
            throw new CustomException(ResponseEnum.EXISTS_IN_DB);
        }
    }

    public static void throwIfExistsInDb(Object toCheck, String moreInfo){
        if (toCheck!= null){
            throw new CustomException(ResponseEnum.EXISTS_IN_DB, moreInfo);
        }
    }
    public static void throwIfNotCorrespondToRecordInDb(Object toCheck1, Object toCheck2){
        if (!Objects.equals(toCheck1,toCheck2)){
            throw new CustomException(ResponseEnum.NOT_CORRESPOND_TO_RECORD_IN_DB);
        }
    }

    public static void throwIfNotCorrespondToRecordInDb(Object toCheck1, Object toCheck2, String moreInfo){
        if (!Objects.equals(toCheck1,toCheck2)){
            throw new CustomException(ResponseEnum.NOT_CORRESPOND_TO_RECORD_IN_DB, moreInfo);
        }
    }
    public static void throwIfIdNotValid(Integer id){
        if(id == null || id < 0){
            throw new CustomException(ResponseEnum.PARAM_NOT_VALIDATE);
        }
    }

    public static void throwIfIdNotValid(Integer id, String moreInfo){
        if(id == null || id < 0){
            throw new CustomException(ResponseEnum.PARAM_NOT_VALIDATE, moreInfo);
        }
    }
}
