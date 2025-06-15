package com.ecommerce.mapper;

import com.ecommerce.model.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface InventoryMapper {
    
    /**
     * 根据商品ID查询库存信息
     * @param productId 商品ID
     * @return 库存信息
     */
    Inventory selectByProductId(Long productId);
    
    /**
     * 插入库存记录
     * @param inventory 库存信息
     * @return 影响行数
     */
    int insert(Inventory inventory);
    
    /**
     * 更新库存信息
     * @param inventory 库存信息
     * @return 影响行数
     */
    int update(Inventory inventory);
    
    /**
     * 更新库存数量
     * @param productId 商品ID
     * @param stockChange 库存变化量（正数为增加，负数为减少）
     * @return 影响行数
     */
    int updateStock(@Param("productId") Long productId, @Param("stockChange") Integer stockChange);
    
    /**
     * 更新低库存阈值
     * @param productId 商品ID
     * @param threshold 低库存阈值
     * @return 影响行数
     */
    int updateLowStockThreshold(@Param("productId") Long productId, @Param("threshold") Integer threshold);
    
    /**
     * 查询低库存商品列表
     * @return 低库存商品列表
     */
    List<Inventory> selectLowStockProducts();
    
    /**
     * 根据商品ID删除库存记录
     * @param productId 商品ID
     * @return 影响行数
     */
    int deleteByProductId(Long productId);
    
    /**
     * 批量查询库存信息
     * @param productIds 商品ID列表
     * @return 库存信息列表
     */
    List<Inventory> selectByProductIds(@Param("list") List<Long> productIds);
    
    /**
     * 查询库存统计信息
     * @return 统计信息
     */
    Map<String, Object> selectInventoryStats();
    
    /**
     * 根据库存范围查询
     * @param minStock 最小库存
     * @param maxStock 最大库存
     * @return 库存信息列表
     */
    List<Inventory> selectByStockRange(@Param("minStock") Integer minStock, @Param("maxStock") Integer maxStock);
    
    /**
     * 检查商品是否有库存记录
     * @param productId 商品ID
     * @return 是否存在
     */
    boolean existsByProductId(Long productId);
    
    /**
     * 查询所有库存记录
     * @return 所有库存信息列表
     */
    List<Inventory> selectAll();
}