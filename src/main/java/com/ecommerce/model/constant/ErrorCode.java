package com.ecommerce.model.constant;

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
}