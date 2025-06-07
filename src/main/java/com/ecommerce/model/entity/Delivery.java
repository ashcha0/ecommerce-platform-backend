package com.ecommerce.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Delivery {
    private Long id;
    private Long orderId; // 订单ID
    private String trackingNo; // 物流单号
    private String shipper; // 物流公司
    private DeliveryStatus status; // 配送状态
    private LocalDateTime shipTime; // 发货时间
    private LocalDateTime estimateTime; // 预计到达时间
    private LocalDateTime deliveryTime; // 实际到达时间
    private LocalDateTime createTime; // 创建时间

    // 配送状态枚举
    public enum DeliveryStatus {
        PENDING("待发货"),
        SHIPPED("已发货"),
        IN_TRANSIT("运输中"),
        DELIVERED("已签收");

        private final String desc;

        DeliveryStatus(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}