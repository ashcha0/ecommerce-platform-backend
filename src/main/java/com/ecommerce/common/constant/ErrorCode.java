package com.ecommerce.common.constant;

public class ErrorCode {

    // 系统错误码
    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_SERVER_ERROR = 500;

    // 业务错误码
    public static final int PARAM_ERROR = 10001;
    public static final int USER_NOT_EXIST = 10002;
    public static final int PASSWORD_ERROR = 10003;
    public static final int ORDER_CREATE_FAILED = 20001;
}