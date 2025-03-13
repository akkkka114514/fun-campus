package com.akkkka.funcampusportal.handler;

import com.akkkka.funcampusutil.util.CommonResponse;
import com.akkkka.funcampusutil.util.CustomException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public CommonResponse<Void> handleException(CustomException e) {
        return new CommonResponse<>(e.getResponseEnum().getCode()
                ,e.getResponseEnum().getMessage()+e.getMoreInfo());
    }
}
