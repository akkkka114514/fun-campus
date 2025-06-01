package com.akkkka.funcampusportal.handler;

import com.akkkka.common.core.domain.R;
import com.akkkka.common.core.exception.GlobalException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public R<Void> handleException(GlobalException e) {
        return R.fail(e.getCode(), e.getMessage());
    }
}
