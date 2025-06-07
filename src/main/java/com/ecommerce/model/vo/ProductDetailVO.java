package com.ecommerce.model.vo;

import com.ecommerce.model.entity.Inventory;
import com.ecommerce.model.entity.Product;
import com.ecommerce.model.entity.Store;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDetailVO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer salesCount;
    private Integer status;
    private String storeName;
    private Integer stock;
    private Boolean lowStock;

    public ProductDetailVO(Product product, Store store, Inventory inventory) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.salesCount = product.getSalesCount();
        this.status = product.getStatus();
        this.storeName = store.getName();
        this.stock = inventory.getStock();
        this.lowStock = inventory.isLowStock();
    }
}