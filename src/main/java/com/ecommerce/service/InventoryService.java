package com.ecommerce.service;

import com.ecommerce.common.result.PageResult;
import com.ecommerce.model.dto.DeliveryUpdateDTO;
import com.ecommerce.model.entity.Inventory;

public interface InventoryService {
    /**
     * 获取商品库存
     */
    Inventory getProductInventory(Long productId);

    /**
     * 更新库存信息
     */
    void updateInventory(Long productId, int stockChange);

    /**
     * 更新低库存阈值
     */
    void updateLowStockThreshold(Long productId, int threshold);

    /**
     * 获取低库存商品列表
     */
    PageResult<Inventory> getLowStockProducts(int page, int size);
}