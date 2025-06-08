package com.ecommerce.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 定义切点：拦截Controller层的所有方法
     */
    @Pointcut("execution(* com.ecommerce.controller..*.*(..))")
    public void controllerPointCut() {
    }

    /**
     * 定义切点：拦截Service层的所有方法
     */
    @Pointcut("execution(* com.ecommerce.service..*.*(..))")
    public void servicePointCut() {
    }

    /**
     * 环绕通知：记录Controller方法的执行日志
     */
    @Around("controllerPointCut()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        
        try {
            // 记录请求开始日志
            logRequestStart(methodName, joinPoint.getArgs(), request);
            
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 记录请求成功日志
            long endTime = System.currentTimeMillis();
            log.info("[{}] 执行成功，耗时: {}ms", methodName, endTime - startTime);
            
            return result;
        } catch (Exception e) {
            // 记录请求失败日志
            long endTime = System.currentTimeMillis();
            log.error("[{}] 执行失败，耗时: {}ms，异常: {}", methodName, endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 环绕通知：记录Service方法的执行日志
     */
    @Around("servicePointCut()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        
        try {
            // 记录方法开始执行
            log.debug("[Service] {} 开始执行，参数: {}", methodName, Arrays.toString(joinPoint.getArgs()));
            
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 记录方法执行成功
            long endTime = System.currentTimeMillis();
            log.debug("[Service] {} 执行成功，耗时: {}ms", methodName, endTime - startTime);
            
            return result;
        } catch (Exception e) {
            // 记录方法执行失败
            long endTime = System.currentTimeMillis();
            log.error("[Service] {} 执行失败，耗时: {}ms，异常: {}", methodName, endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 记录请求开始日志
     */
    private void logRequestStart(String methodName, Object[] args, HttpServletRequest request) {
        try {
            if (request != null) {
                // 获取请求头信息
                Map<String, String> headers = new HashMap<>();
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    // 过滤敏感信息
                    if (!headerName.toLowerCase().contains("authorization") && 
                        !headerName.toLowerCase().contains("cookie")) {
                        headers.put(headerName, request.getHeader(headerName));
                    }
                }
                
                log.info("[{}] 请求开始 - URL: {}, Method: {}, IP: {}, User-Agent: {}, 参数: {}", 
                        methodName, 
                        request.getRequestURL().toString(),
                        request.getMethod(),
                        getClientIpAddress(request),
                        request.getHeader("User-Agent"),
                        Arrays.toString(args));
            } else {
                log.info("[{}] 方法开始执行，参数: {}", methodName, Arrays.toString(args));
            }
        } catch (Exception e) {
            log.warn("记录请求日志失败: {}", e.getMessage());
        }
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}