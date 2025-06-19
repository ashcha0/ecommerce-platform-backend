package com.ecommerce.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Delivery {
    private Long id;
    private Long orderId; // 订单ID
    private String trackingNo; // 物流单号
    private String shipper; // 物流公司
    private String consigneeName; // 收货人姓名
    private String consigneePhone; // 收货人电话
    private String deliveryAddress; // 配送地址
    private String remark; // 备注
    private DeliveryStatus status; // 配送状态
    private LocalDateTime shipTime; // 发货时间
    private LocalDateTime estimateTime; // 预计到达时间
    private LocalDateTime deliveryTime; // 实际到达时间
    private LocalDateTime createTime; // 创建时间

    // 配送状态枚举
    public enum DeliveryStatus {
        PAYING("待付款"),
        SHIPPING("待发货"),
        RECEIPTING("待收货"),
        COMPLETED("已完成"),
        CANCELLED("已取消"),
        PROCESSING("售后处理中"),
        PROCESSED("售后处理完成");

        private final String desc;

        DeliveryStatus(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}