package com.ecommerce.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 简单订单信息VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOrderVO {
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 客户姓名
     */
    private String customerName;
    
    /**
     * 客户电话
     */
    private String customerPhone;
    
    /**
     * 配送地址
     */
    private String deliveryAddress;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}