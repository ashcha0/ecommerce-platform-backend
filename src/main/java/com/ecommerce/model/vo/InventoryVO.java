package com.ecommerce.model.vo;

import com.ecommerce.model.entity.Inventory;
import com.ecommerce.model.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryVO {
    
    private Long id; // 库存记录ID
    private Long productId; // 商品ID
    private String productName; // 商品名称
    private BigDecimal productPrice; // 商品价格
    private Integer stock; // 当前库存
    private Integer lowStockThreshold; // 低库存阈值
    private Boolean isLowStock; // 是否低库存
    private LocalDateTime updateTime; // 更新时间
    private String stockStatus; // 库存状态描述
    
    public InventoryVO() {}
    
    public InventoryVO(Inventory inventory) {
        this.id = inventory.getId();
        this.productId = inventory.getProductId();
        this.stock = inventory.getStock();
        this.lowStockThreshold = inventory.getLowStockThreshold();
        this.isLowStock = inventory.isLowStock();
        this.updateTime = inventory.getUpdateTime();
        this.stockStatus = getStockStatusDescription();
    }
    
    public InventoryVO(Inventory inventory, Product product) {
        this(inventory);
        if (product != null) {
            this.productName = product.getName();
            this.productPrice = product.getPrice();
        }
    }
    
    /**
     * 获取库存状态描述
     */
    private String getStockStatusDescription() {
        if (stock == null) {
            return "未知";
        }
        if (stock == 0) {
            return "缺货";
        }
        if (isLowStock != null && isLowStock) {
            return "库存不足";
        }
        return "库存充足";
    }
}