package com.ecommerce.common.handler;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.constant.ErrorCode; // 导入 ErrorCode 类
import com.ecommerce.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.dao.DataAccessException;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("参数验证异常: {}", message, e);
        return Result.fail(ErrorCode.PARAM_ERROR, "参数验证失败: " + message);
    }

    /**
     * 处理约束验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        log.error("约束验证异常: {}", message, e);
        return Result.fail(ErrorCode.PARAM_ERROR, "参数验证失败: " + message);
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public Result<?> handleRedisConnectionException(RedisConnectionFailureException e) {
        log.error("Redis连接异常: {}", e.getMessage(), e);
        return Result.fail(ErrorCode.INTERNAL_SERVER_ERROR, "Redis服务连接失败，请检查Redis服务状态: " + e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public Result<?> handleDataAccessException(DataAccessException e) {
        log.error("数据访问异常: {}", e.getMessage(), e);
        return Result.fail(ErrorCode.INTERNAL_SERVER_ERROR, "数据访问失败: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        String errorMessage = "系统繁忙，请稍后再试";

        // 在开发环境下提供更详细的错误信息
        if (log.isDebugEnabled()) {
            errorMessage += " (详细信息: " + e.getMessage() + ")";
        }

        return Result.fail(ErrorCode.INTERNAL_SERVER_ERROR, errorMessage); // 修改为 ErrorCode.INTERNAL_SERVER_ERROR 和
                                                                           // Result.fail
    }
}