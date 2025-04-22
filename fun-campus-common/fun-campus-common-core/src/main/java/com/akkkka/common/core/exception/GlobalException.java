package com.akkkka.common.core.exception;

import com.akkkka.common.core.enums.ResponseEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 全局异常
 * 
 * @author ruoyi
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String code;

    private String message;

    /**
     * 错误明细，内部调试错误
     *
     */
    private String detail;

    public GlobalException(ResponseEnum responseEnum) {
        super(responseEnum.getMessage());
        this.code = responseEnum.getCode();
    }
}