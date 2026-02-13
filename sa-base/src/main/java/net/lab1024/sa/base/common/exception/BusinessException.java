package net.lab1024.sa.base.common.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.lab1024.sa.base.common.code.ErrorCode;

/**
 * 业务逻辑异常,全局异常拦截后统一返回ResponseCodeConst.SYSTEM_ERROR
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/8/25 21:57
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {

    private int code;

    private String detailMsg;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code=errorCode.getCode();
    }
    public BusinessException(ErrorCode errorCode,String detailMsg){
        super(errorCode.getMsg()+":"+detailMsg);
        this.code=errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode,String detailMsg,Throwable t){
        super(errorCode.getMsg()+":"+detailMsg,t);
        this.code=errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode,Throwable t){
        super(errorCode.getMsg(),t);
        this.code=errorCode.getCode();
    }

}
