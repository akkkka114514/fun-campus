package com.akkkka.funcampusutil.util;

import com.akkkka.funcampusutil.common.ResponseEnum;
import lombok.Getter;
/**
 * @author akkkka
 */
@Getter
public class CustomException extends RuntimeException{
    protected ResponseEnum responseEnum;
    // 附加信息、更详细的信息
    protected String moreInfo;

    public CustomException(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
    }

    public CustomException(ResponseEnum responseEnum, String moreInfo) {
        this.responseEnum = responseEnum;
        this.moreInfo = moreInfo;
    }

    public CustomException(ResponseEnum responseEnum, Throwable cause) {
        super(cause);
        this.responseEnum = responseEnum;

    }
}
