package com.ecommerce.model.vo;

import com.ecommerce.model.entity.Customer;
import com.ecommerce.model.entity.Delivery;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {
    private String orderNo;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderTime;
    private CustomerVO customer;
    private DeliveryVO delivery;
    private List<OrderItemVO> items;

    @Data
    public static class CustomerVO {
        private Long id;
        private String username;
        private String realName;
        private String phone;
        private String address;

        public CustomerVO(Customer customer) {
            this.id = customer.getId();
            this.username = customer.getUsername();
            this.realName = customer.getRealName();
            this.phone = customer.getPhone();
            this.address = customer.getAddress();
        }
    }

    @Data
    public static class DeliveryVO {
        private String trackingNo;
        private String shipper;
        private String status;
        private LocalDateTime shipTime;
        private LocalDateTime deliveryTime;

        public DeliveryVO(Delivery delivery) {
            this.trackingNo = delivery.getTrackingNo();
            this.shipper = delivery.getShipper();
            this.status = delivery.getStatus().name();
            this.shipTime = delivery.getShipTime();
            this.deliveryTime = delivery.getDeliveryTime();
        }
    }

    @Data
    public static class OrderItemVO {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal itemAmount;

        public OrderItemVO(OrderItem item, String productName) {
            this.productId = item.getProductId();
            this.productName = productName;
            this.quantity = item.getQuantity();
            this.unitPrice = item.getUnitPrice();
            this.itemAmount = item.getItemAmount();
        }
    }
}