package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class ProductQueryDTO {
    @Size(max = 255, message = "商品名称长度不能超过255个字符")
    private String name; // 商品名称（模糊查询）
    
    @Positive(message = "店铺ID必须为正数")
    private Long storeId; // 店铺ID
    
    @DecimalMin(value = "0.01", message = "最低价格必须大于0.01")
    @Digits(integer = 6, fraction = 2, message = "最低价格格式不正确")
    private BigDecimal minPrice; // 最低价格
    
    @DecimalMin(value = "0.01", message = "最高价格必须大于0.01")
    @Digits(integer = 6, fraction = 2, message = "最高价格格式不正确")
    private BigDecimal maxPrice; // 最高价格
    
    @Min(value = 0, message = "商品状态值不能为负数")
    @Max(value = 1, message = "商品状态值只能为0或1")
    private Integer status; // 商品状态：0-下架, 1-上架
    
    private Boolean inStock; // 是否有库存
    // private String category; // 商品类别 - 数据库表中不存在此字段
    
    @Min(value = 1, message = "页码必须大于0")
    @Max(value = 10000, message = "页码不能超过10000")
    private int pageNum = 1; // 页码，默认第1页
    
    @Min(value = 1, message = "每页数量必须大于0")
    @Max(value = 100, message = "每页数量不能超过100")
    private int pageSize = 10; // 每页数量，默认10条

    // 判断是否是管理员查询（根据storeId是否为空）
    public boolean isAdminQuery() {
        return storeId != null;
    }

    // 获取偏移量（用于SQL分页）
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}