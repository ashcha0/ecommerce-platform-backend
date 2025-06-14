package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class InventoryUpdateDTO {
    
    @NotNull(message = "商品ID不能为空")
    @Positive(message = "商品ID必须为正数")
    private Long productId;
    
    @NotNull(message = "库存变化量不能为空")
    private Integer stockChange; // 库存变化量，正数为增加，负数为减少
    
    @PositiveOrZero(message = "低库存阈值不能为负数")
    private Integer lowStockThreshold; // 低库存阈值（可选）
    
    private String reason; // 库存变更原因（可选）
}