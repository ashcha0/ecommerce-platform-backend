package com.ecommerce.common.constant;

public class ErrorCode {

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
    public static final int PRODUCT_NOT_FOUND = 11001;
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
    public static final int ORDER_NOT_FOUND = 20002;
    public static final int ORDER_STATUS_INVALID = 20003;
    
    // 库存相关错误码
    public static final int INVENTORY_NOT_ENOUGH = 30001;
    public static final int INVENTORY_NOT_FOUND = 30002;
    
    // 店铺相关错误码
    public static final int STORE_NOT_FOUND = 40001;
    public static final int STORE_ACCESS_DENIED = 40002;
}