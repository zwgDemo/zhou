package com.atguigu.lease.common.exception;


import com.atguigu.lease.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result handler(Exception e){
        e.getMessage();
        e.printStackTrace();
        return Result.fail();
    }


    @ExceptionHandler(LeaseException.class)
    @ResponseBody
    public Result leaseHandler(LeaseException e){
        e.printStackTrace();
        return Result.fail(e.getCode(),e.getMessage());
    }

}
