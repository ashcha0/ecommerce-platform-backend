package com.ecommerce.service;

import com.ecommerce.model.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    
    /**
     * 创建订单项
     */
    OrderItem createOrderItem(OrderItem orderItem);
    
    /**
     * 根据ID查询订单项
     */
    OrderItem getOrderItemById(Long id);
    
    /**
     * 根据订单ID查询订单项列表
     */
    List<OrderItem> getOrderItemsByOrderId(Long orderId);
    
    /**
     * 更新订单项数量
     */
    void updateOrderItemQuantity(Long itemId, Integer quantity);
    
    /**
     * 删除订单项
     */
    void deleteOrderItem(Long itemId);
    
    /**
     * 批量创建订单项
     */
    void batchCreateOrderItems(List<OrderItem> orderItems);
    
    /**
     * 计算订单项总金额
     */
    java.math.BigDecimal calculateTotalAmount(List<OrderItem> orderItems);
}