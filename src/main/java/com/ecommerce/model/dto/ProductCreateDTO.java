package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class ProductCreateDTO {
    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotBlank(message = "商品描述不能为空")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "店铺ID不能为空")
    private Long storeId;

    // @NotNull(message = "库存数量不能为空")
    // @Min(value = 0, message = "库存数量不能为负数")
    // private Integer stock; // 库存数量 - 数据库表中不存在此字段

    // private String category; // 商品分类 - 数据库表中不存在此字段
    
    private String imageUrl; // 商品图片URL
}