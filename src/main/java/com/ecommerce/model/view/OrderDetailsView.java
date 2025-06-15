package com.ecommerce.model.view;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailsView {
    private Long orderId;
    private String orderNo;
    private LocalDateTime orderTime;
    private BigDecimal totalAmount;
    private String orderStatus; // 订单状态描述
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long deliveryId;
    private String trackingNo;
    private String shipper;
    private String deliveryStatus; // 配送状态描述
    private String products; // 商品信息字符串
    private Integer itemCount; // 商品项数量

    // 解析商品列表
    public String[] getProductList() {
        return products != null ? products.split(";") : new String[0];
    }
}