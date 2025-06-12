package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import java.math.BigDecimal;

@Data
public class ProductCreateDTO {
    @NotBlank(message = "商品名称不能为空")
    @Size(min = 1, max = 255, message = "商品名称长度必须在1-255个字符之间")
    private String name;

    @NotBlank(message = "商品描述不能为空")
    @Size(min = 1, max = 2000, message = "商品描述长度必须在1-2000个字符之间")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0.01")
    @DecimalMax(value = "999999.99", message = "商品价格不能超过999999.99")
    @Digits(integer = 6, fraction = 2, message = "商品价格格式不正确，最多6位整数和2位小数")
    private BigDecimal price;

    @NotNull(message = "店铺ID不能为空")
    @Positive(message = "店铺ID必须为正数")
    private Long storeId;

    // @NotNull(message = "库存数量不能为空")
    // @Min(value = 0, message = "库存数量不能为负数")
    // private Integer stock; // 库存数量 - 数据库表中不存在此字段

    // private String category; // 商品分类 - 数据库表中不存在此字段
    
    @Size(max = 500, message = "商品图片URL长度不能超过500个字符")
    private String imageUrl; // 商品图片URL
}