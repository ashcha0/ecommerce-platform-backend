package com.ecommerce.common.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 系统错误
    SYSTEM_ERROR(1000, "系统错误"),

    // 用户相关
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_DUPLICATE_USERNAME(2002, "用户名已存在"),

    // 商品相关
    PRODUCT_NOT_FOUND(3001, "商品不存在"),
    PRODUCT_INSUFFICIENT_STOCK(3002, "商品库存不足"),

    // 订单相关
    ORDER_NOT_FOUND(4001, "订单不存在"),
    ORDER_ALREADY_PAID(4002, "订单已支付，无法取消"),
    ORDER_ALREADY_CANCELLED(4003, "订单已取消"),

    // 库存相关
    INVENTORY_LOCK_FAILED(5001, "库存锁定失败"),
    INVENTORY_UPDATE_FAILED(5002, "库存更新失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // 系统错误码
    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_SERVER_ERROR = 500;

    // 通用业务错误码
    public static final int PARAM_ERROR = 10001;
    public static final int USER_NOT_EXIST = 10002;
    public static final int PASSWORD_ERROR = 10003;
    
    // 商品相关错误码
    public static final int PRODUCT_NAME_DUPLICATE = 11002;
    public static final int PRODUCT_STATUS_INVALID = 11003;
    public static final int PRODUCT_PRICE_INVALID = 11004;
    public static final int PRODUCT_CREATE_FAILED = 11005;
    public static final int PRODUCT_UPDATE_FAILED = 11006;
    public static final int PRODUCT_DELETE_FAILED = 11007;
    public static final int PRODUCT_STATUS_CHANGE_FAILED = 11008;
    public static final int PRODUCT_STATUS_UPDATE_FAILED = 11009;
    
    // 订单相关错误码
    public static final int ORDER_CREATE_FAILED = 20001;
    public static final int ORDER_STATUS_INVALID = 20003;
    
    // 库存相关错误码
    public static final int INVENTORY_NOT_ENOUGH = 30001;
    public static final int INVENTORY_NOT_FOUND = 30002;
    
    // 店铺相关错误码
    public static final int STORE_NOT_FOUND = 40001;
    public static final int STORE_ACCESS_DENIED = 40002;
}