package com.ecommerce.model.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private Long storeId; // 关联店铺ID
    private String name; // 商品名称
    private String description; // 商品描述
    private BigDecimal price; // 销售价格
    private Integer salesCount; // 销量
    // private Integer stock; // 库存数量 - 数据库表中不存在此字段
    // private String category; // 商品分类 - 数据库表中不存在此字段
    private String imageUrl; // 商品图片URL
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    private Integer status; // 状态(0:下架, 1:上架)

    // 商品状态枚举
    public enum ProductStatus {
        OFF_SHELF(0, "下架"),
        ON_SHELF(1, "上架");

        private final int code;
        private final String desc;

        ProductStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public boolean isOnShelf() {
        return ProductStatus.ON_SHELF.code == status;
    }
}