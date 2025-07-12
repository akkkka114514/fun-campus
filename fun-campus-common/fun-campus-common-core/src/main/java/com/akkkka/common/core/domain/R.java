package com.akkkka.common.core.domain;

import java.io.Serializable;
import com.akkkka.common.core.constant.Constants;
import com.akkkka.common.core.enums.ResponseEnum;
import lombok.Data;

/**
 * 响应信息主体
 *
 * @author ruoyi
 */
@Data
public class R<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String code;

    private String msg;

    private T data;

    public static <T> R<T> ok()
    {
        return restResult(null, ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage());
    }

    public static <T> R<T> ok(T data)
    {
        return restResult(data,ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage());
    }

    public static <T> R<T> fail(T data, String code, String msg){
        return restResult(data, code, msg);
    }
    public static <Void> R<Void> fail(String code, String msg){
        return fail(null, code, msg);
    }
    public static <T> R<T> fail(ResponseEnum fail)
    {
        return restResult(null, fail.getCode(), fail.getMessage());
    }


    public static <T> R<T> fail(ResponseEnum fail,T data)
    {
        return restResult(data, fail.getCode(), fail.getMessage());
    }


    public static <T> R<T> fail(String msg)
    {
        return restResult(null, null, msg);
    }
    private static <T> R<T> restResult(T data, String code, String msg)
    {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }
}
