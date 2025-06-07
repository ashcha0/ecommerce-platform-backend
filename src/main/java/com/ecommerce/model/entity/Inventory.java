package com.ecommerce.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Inventory {
    private Long id;
    private Long productId; // 商品ID
    private Integer stock; // 当前库存
    private Integer lowStockThreshold; // 低库存阈值
    private LocalDateTime updateTime; // 更新时间

    public boolean isLowStock() {
        return stock <= lowStockThreshold;
    }
}