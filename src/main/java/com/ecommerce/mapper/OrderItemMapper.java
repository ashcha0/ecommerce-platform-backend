package com.ecommerce.mapper;

import com.ecommerce.model.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    
    /**
     * 插入订单项
     */
    int insert(OrderItem orderItem);
    
    /**
     * 根据ID查询订单项
     */
    OrderItem selectById(Long id);
    
    /**
     * 根据订单ID查询订单项列表
     */
    List<OrderItem> selectByOrderId(Long orderId);
    
    /**
     * 根据商品ID查询订单项列表
     */
    List<OrderItem> selectByProductId(Long productId);
    
    /**
     * 更新订单项数量
     */
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 删除订单项
     */
    int deleteById(Long id);
    
    /**
     * 根据订单ID删除订单项
     */
    int deleteByOrderId(Long orderId);
    
    /**
     * 批量插入订单项
     */
    int batchInsert(List<OrderItem> orderItems);
}