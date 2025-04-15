package com.akkkka.funcampusportal.handler;

import com.akkkka.common.core.domain.R;
import com.akkkka.funcampusutil.util.CustomException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public R<Void> handleException(CustomException e) {
        return new R<>(e.getResponseEnum().getCode()
                ,e.getResponseEnum().getMessage()+e.getMoreInfo());
    }
}
