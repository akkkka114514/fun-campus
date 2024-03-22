package com.akkkka.funcampusutil.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.io.Serializable;


/**
 * Copied from <a href="http://t.csdn.cn/Jnk3g">...</a> on 2023/8/5.
 */
/**
 * @author akkkka
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
public class CommonResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String message;

    private T data;

    public CommonResponse(String code,String message){
        this.code = code;
        this.message = message;
    }

    public CommonResponse(String code,String message,T data){
        this(code,message);
        this.data = data;
    }

    public static <T> CommonResponse<T> success(){
        return new CommonResponse<>(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getMessage());
    }

    public static <T> CommonResponse<T> success(T data){
        return new CommonResponse<>(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getMessage(),data);
    }

    public static <T> CommonResponse<T> fail(ResponseEnum responseEnum){
        return new CommonResponse<>(responseEnum.getCode(),responseEnum.getMessage());
    }

    public static <T> CommonResponse<T> fail(ResponseEnum responseEnum,T data){
        return new CommonResponse<>(responseEnum.getCode(), responseEnum.getMessage(),data);
    }
}
