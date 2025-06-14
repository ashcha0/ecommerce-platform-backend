package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class InventoryQueryDTO {
    
    @Positive(message = "商品ID必须为正数")
    private Long productId; // 商品ID
    
    @PositiveOrZero(message = "最小库存不能为负数")
    private Integer minStock; // 最小库存
    
    @PositiveOrZero(message = "最大库存不能为负数")
    private Integer maxStock; // 最大库存
    
    private Boolean isLowStock; // 是否只查询低库存商品
    
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1; // 页码，默认为1
    
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10; // 每页大小，默认为10
    
    private String sortBy = "updateTime"; // 排序字段，默认按更新时间
    
    private String sortOrder = "DESC"; // 排序方向，默认降序
}