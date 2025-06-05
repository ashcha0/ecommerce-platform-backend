package com.ecommerce.common.handler;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.constant.ErrorCode; // 导入 ErrorCode 类
import com.ecommerce.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage()); // 修改为 getCode() 和 Result.fail
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.fail(ErrorCode.INTERNAL_SERVER_ERROR, "系统繁忙，请稍后再试"); // 修改为 ErrorCode.INTERNAL_SERVER_ERROR 和
                                                                           // Result.fail
    }
}