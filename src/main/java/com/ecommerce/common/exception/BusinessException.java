package com.ecommerce.common.exception;

import com.ecommerce.common.constant.ErrorCode;
public class BusinessException extends RuntimeException {

    private int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode(); // 假设 ErrorCode 有 getCode 方法
    }

    public int getCode() {
        return code;
    }
}