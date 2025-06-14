package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class InventoryCreateDTO {
    
    @NotNull(message = "商品ID不能为空")
    @Positive(message = "商品ID必须为正数")
    private Long productId;
    
    @NotNull(message = "初始库存不能为空")
    @PositiveOrZero(message = "初始库存不能为负数")
    private Integer initialStock; // 初始库存数量
    
    @PositiveOrZero(message = "低库存阈值不能为负数")
    private Integer lowStockThreshold = 10; // 低库存阈值，默认为10
}