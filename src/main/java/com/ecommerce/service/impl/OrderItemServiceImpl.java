package com.ecommerce.service.impl;

import com.ecommerce.common.constant.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.mapper.OrderItemMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.entity.OrderItem;
import com.ecommerce.model.entity.Product;
import com.ecommerce.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderItem createOrderItem(OrderItem orderItem) {
        log.info("创建订单项，订单ID: {}, 商品ID: {}", orderItem.getOrderId(), orderItem.getProductId());
        
        // 验证商品是否存在
        Product product = productMapper.selectById(orderItem.getProductId());
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品不存在");
        }
        
        // 设置商品单价（如果未设置）
        if (orderItem.getUnitPrice() == null) {
            orderItem.setUnitPrice(product.getPrice());
        }
        
        // 计算小计金额
        orderItem.setItemAmount(orderItem.calculateItemAmount());
        
        int result = orderItemMapper.insert(orderItem);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单项创建失败");
        }
        
        log.info("订单项创建成功，ID: {}", orderItem.getId());
        return orderItem;
    }

    @Override
    public OrderItem getOrderItemById(Long id) {
        log.info("查询订单项，ID: {}", id);
        
        OrderItem orderItem = orderItemMapper.selectById(id);
        if (orderItem == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "订单项不存在");
        }
        
        return orderItem;
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        log.info("查询订单项列表，订单ID: {}", orderId);
        
        return orderItemMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderItemQuantity(Long itemId, Integer quantity) {
        log.info("更新订单项数量，ID: {}, 数量: {}", itemId, quantity);
        
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "商品数量必须大于0");
        }
        
        OrderItem orderItem = orderItemMapper.selectById(itemId);
        if (orderItem == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "订单项不存在");
        }
        
        int result = orderItemMapper.updateQuantity(itemId, quantity);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单项数量更新失败");
        }
        
        log.info("订单项数量更新成功，ID: {}", itemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrderItem(Long itemId) {
        log.info("删除订单项，ID: {}", itemId);
        
        OrderItem orderItem = orderItemMapper.selectById(itemId);
        if (orderItem == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "订单项不存在");
        }
        
        int result = orderItemMapper.deleteById(itemId);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单项删除失败");
        }
        
        log.info("订单项删除成功，ID: {}", itemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateOrderItems(List<OrderItem> orderItems) {
        log.info("批量创建订单项，数量: {}", orderItems.size());
        
        if (orderItems == null || orderItems.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "订单项列表不能为空");
        }
        
        // 验证每个订单项并计算金额
        for (OrderItem orderItem : orderItems) {
            Product product = productMapper.selectById(orderItem.getProductId());
            if (product == null) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品不存在");
            }
            
            if (orderItem.getUnitPrice() == null) {
                orderItem.setUnitPrice(product.getPrice());
            }
            
            orderItem.setItemAmount(orderItem.calculateItemAmount());
        }
        
        int result = orderItemMapper.batchInsert(orderItems);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "批量创建订单项失败");
        }
        
        log.info("批量创建订单项成功，数量: {}", result);
    }

    @Override
    public BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        log.info("计算订单项总金额，订单项数量: {}", orderItems.size());
        
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getItemAmount() != null) {
                totalAmount = totalAmount.add(orderItem.getItemAmount());
            } else {
                // 如果小计金额为空，则重新计算
                BigDecimal itemAmount = orderItem.calculateItemAmount();
                totalAmount = totalAmount.add(itemAmount);
            }
        }
        
        log.info("订单项总金额计算完成: {}", totalAmount);
        return totalAmount;
    }
}