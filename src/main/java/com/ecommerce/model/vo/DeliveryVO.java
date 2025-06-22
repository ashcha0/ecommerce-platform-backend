package com.ecommerce.model.vo;

import com.ecommerce.model.entity.Delivery;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 配送信息视图对象
 */
@Data
public class DeliveryVO {
    private Long id;
    private Long orderId;
    private String trackingNo;
    private String shipper;
    private String consigneeName;
    private String consigneePhone;
    private String deliveryAddress;
    private String status;
    private String statusDesc;
    private LocalDateTime shipTime;
    private LocalDateTime estimateTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime createTime;

    public DeliveryVO() {
    }

    public DeliveryVO(Delivery delivery) {
        if (delivery != null) {
            this.id = delivery.getId();
            this.orderId = delivery.getOrderId();
            this.trackingNo = delivery.getTrackingNo();
            this.shipper = delivery.getShipper();
            this.consigneeName = delivery.getConsigneeName();
            this.consigneePhone = delivery.getConsigneePhone();
            this.deliveryAddress = delivery.getDeliveryAddress();
            if (delivery.getStatus() != null) {
                this.status = delivery.getStatus().name();
                this.statusDesc = delivery.getStatus().getDesc();
            }
            this.shipTime = delivery.getShipTime();
            this.estimateTime = delivery.getEstimateTime();
            this.deliveryTime = delivery.getDeliveryTime();
            this.createTime = delivery.getCreateTime();
        }
    }

    /**
     * 静态工厂方法，从Delivery实体创建VO
     */
    public static DeliveryVO from(Delivery delivery) {
        return new DeliveryVO(delivery);
    }
}