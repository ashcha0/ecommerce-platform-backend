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

        /**
         * 根据状态码获取枚举
         */
        public static ProductStatus fromCode(int code) {
            for (ProductStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("无效的商品状态码: " + code);
        }

        /**
         * 验证状态码是否有效
         */
        public static boolean isValidCode(Integer code) {
            if (code == null) {
                return false;
            }
            for (ProductStatus status : values()) {
                if (status.code == code) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 判断商品是否上架
     */
    public boolean isOnShelf() {
        return ProductStatus.ON_SHELF.code == status;
    }

    /**
     * 判断商品是否下架
     */
    public boolean isOffShelf() {
        return ProductStatus.OFF_SHELF.code == status;
    }

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        return ProductStatus.fromCode(status).getDesc();
    }

    /**
     * 设置商品状态
     */
    public void setProductStatus(ProductStatus productStatus) {
        this.status = productStatus.getCode();
    }

    /**
     * 获取商品状态枚举
     */
    public ProductStatus getProductStatus() {
        return ProductStatus.fromCode(status);
    }

    /**
     * 验证商品状态切换是否合法
     */
    public boolean canChangeStatusTo(Integer newStatus) {
        if (!ProductStatus.isValidCode(newStatus)) {
            return false;
        }
        // 所有状态之间都可以切换
        return true;
    }
}