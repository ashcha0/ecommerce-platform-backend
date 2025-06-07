package com.ecommerce.model.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItem {
    private Long id;
    private Long orderId; // 订单ID
    private Long productId; // 商品ID
    private Integer quantity; // 购买数量
    private BigDecimal unitPrice; // 商品单价
    private BigDecimal itemAmount; // 小计金额

    // 计算小计金额
    public BigDecimal calculateItemAmount() {
        if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}