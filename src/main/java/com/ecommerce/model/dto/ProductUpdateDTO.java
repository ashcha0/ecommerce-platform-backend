package com.ecommerce.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

/**
 * 商品更新数据传输对象
 * 用于商品信息更新操作的数据传输
 */
@Data
public class ProductUpdateDTO {
    
    /**
     * 商品名称
     */
    @Size(min = 1, max = 100, message = "商品名称长度必须在1-100个字符之间")
    private String name;
    
    /**
     * 商品描述
     */
    @Size(max = 1000, message = "商品描述不能超过1000个字符")
    private String description;
    
    /**
     * 商品价格
     */
    @DecimalMin(value = "0.01", message = "商品价格必须大于0.01")
    @DecimalMax(value = "999999.99", message = "商品价格不能超过999999.99")
    @Digits(integer = 6, fraction = 2, message = "商品价格格式不正确，最多6位整数，2位小数")
    private BigDecimal price;
    
    /**
     * 商品图片URL
     */
    @Size(max = 500, message = "图片URL不能超过500个字符")
    @URL(message = "图片URL格式不正确")
    private String imageUrl;
    
    /**
     * 商品状态：0-下架，1-上架
     */
    @Min(value = 0, message = "商品状态值不能小于0")
    @Max(value = 1, message = "商品状态值不能大于1")
    private Integer status;
}