package com.ecommerce.model.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String orderNo; // 订单编号
    private Long customerId; // 客户ID
    private LocalDateTime orderTime; // 下单时间
    private BigDecimal totalAmount; // 订单总额
    private OrderStatus status; // 订单状态
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    
    // 关联查询字段（非数据库字段）
    private String customerName; // 客户姓名
    private String customerPhone; // 客户电话
    private String deliveryAddress; // 配送地址

    // 订单状态枚举
    public enum OrderStatus {
        CREATED("已创建"),
        PAID("已支付"),
        SHIPPING("配送中"),
        COMPLETED("已完成"),
        CANCELLED("已取消");

        private final String desc;

        OrderStatus(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    // 用于创建新订单的便捷方法
    public static Order create(String orderNo, Long customerId, BigDecimal totalAmount) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setCustomerId(customerId);
        order.setOrderTime(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CREATED);
        order.setCreateTime(LocalDateTime.now());
        return order;
    }
}