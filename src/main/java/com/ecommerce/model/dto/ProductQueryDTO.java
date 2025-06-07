package com.ecommerce.model.dto;

import lombok.Data;

@Data
public class ProductQueryDTO {
    private String name; // 商品名称（模糊查询）
    private Long storeId; // 店铺ID
    private Integer minPrice; // 最低价格
    private Integer maxPrice; // 最高价格
    private Integer status; // 商品状态：0-下架, 1-上架
    // private Boolean inStock; // 是否有库存 - 数据库表中不存在stock字段
    private String category; // 商品类别
    private int pageNum = 1; // 页码，默认第1页
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