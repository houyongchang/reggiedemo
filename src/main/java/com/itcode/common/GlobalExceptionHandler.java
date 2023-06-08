package com.itcode.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理器
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> ExceptionHandler(SQLIntegrityConstraintViolationException exception){
        String exceptionMessage = exception.getMessage();
        if (exceptionMessage.contains("Duplicate entry")){
            String[] split = exceptionMessage.split(" ");
            String msg = split[2] + "重复";
            return R.error(msg);
        }
        return  R.error("未知异常");
    }

    @ExceptionHandler(CustException.class)
    public R<String> ExceptionHandler( CustException exception){

        return  R.error(exception.getMessage());
    }
}
