package com.ecommerce.service;

import com.ecommerce.common.result.PageResult;
import com.ecommerce.model.dto.InventoryCreateDTO;
import com.ecommerce.model.dto.InventoryQueryDTO;
import com.ecommerce.model.dto.InventoryUpdateDTO;
import com.ecommerce.model.entity.Inventory;
import com.ecommerce.model.vo.InventoryVO;

import java.util.List;
import java.util.Map;

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
    
    /**
     * 创建商品库存记录
     */
    void createInventory(InventoryCreateDTO createDTO);
    
    /**
     * 批量更新库存
     */
    void batchUpdateInventory(List<InventoryUpdateDTO> updateDTOList);
    
    /**
     * 根据条件查询库存列表
     */
    PageResult<InventoryVO> queryInventory(InventoryQueryDTO queryDTO);
    
    /**
     * 检查库存是否充足
     * @param productId 商品ID
     * @param quantity 需要的数量
     * @return 是否有足够库存
     */
    boolean checkStock(Long productId, Integer quantity);
    
    /**
     * 锁定库存
     * @param productId 商品ID
     * @param quantity 锁定数量
     */
    void lockStock(Long productId, Integer quantity);
    
    /**
     * 释放锁定的库存
     * @param productId 商品ID
     * @param quantity 释放数量
     */
    void releaseStock(Long productId, Integer quantity);
    
    /**
     * 获取库存统计信息
     */
    Map<String, Object> getInventoryStats();
    
    /**
     * 检查商品是否有库存记录
     */
    boolean hasInventoryRecord(Long productId);
    
    /**
     * 删除商品库存记录
     */
    void deleteInventory(Long productId);
}