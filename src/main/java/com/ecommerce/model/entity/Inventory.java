package com.ecommerce.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Inventory {
    private Long id;
    private Long productId; // 商品ID
    private Integer stock; // 当前库存
    private Integer lockedStock; // 锁定库存
    private Integer lowStockThreshold; // 低库存阈值
    private LocalDateTime updateTime; // 更新时间

    public boolean isLowStock() {
        return stock <= lowStockThreshold;
    }
    
    /**
     * 获取可用库存（总库存 - 锁定库存）
     * @return 可用库存数量
     */
    public Integer getAvailableStock() {
        return stock - (lockedStock != null ? lockedStock : 0);
    }
    
    /**
     * 检查是否有足够的可用库存
     * @param quantity 需要的数量
     * @return 是否有足够库存
     */
    public boolean hasEnoughStock(Integer quantity) {
        return getAvailableStock() >= quantity;
    }
}