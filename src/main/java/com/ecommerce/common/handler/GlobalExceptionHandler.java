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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    /**
     * 处理参数类型转换异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("参数类型转换异常: {}", e.getMessage(), e);
        String message = String.format("参数 '%s' 的值 '%s' 类型不正确，期望类型为 %s",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());
        return Result.fail(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持异常: {}", e.getMessage(), e);
        String message = String.format("请求方法 '%s' 不支持，支持的方法: %s",
                e.getMethod(), String.join(", ", e.getSupportedMethods()));
        return Result.fail(ErrorCode.BAD_REQUEST, message);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("请求路径不存在: {}", e.getRequestURL(), e);
        return Result.fail(ErrorCode.NOT_FOUND, "请求的资源不存在: " + e.getRequestURL());
    }

    /**
     * 处理JSON解析异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("JSON解析异常: {}", e.getMessage(), e);
        String message = "请求体格式错误，请检查JSON格式";
        if (e.getMessage().contains("JSON parse error")) {
            message = "JSON格式错误，请检查请求体格式";
        } else if (e.getMessage().contains("Required request body is missing")) {
            message = "请求体不能为空";
        }
        return Result.fail(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理IllegalArgumentException异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数异常: {}", e.getMessage(), e);
        return Result.fail(ErrorCode.PARAM_ERROR, "参数错误: " + e.getMessage());
    }

    /**
     * 处理NullPointerException异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<Void> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常: {}", e.getMessage(), e);
        return Result.fail(ErrorCode.INTERNAL_SERVER_ERROR, "系统内部错误，请联系管理员");
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