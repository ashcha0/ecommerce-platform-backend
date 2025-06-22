package com.ecommerce.service.impl;

import com.ecommerce.common.constant.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.mapper.InventoryMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.dto.InventoryCreateDTO;
import com.ecommerce.model.dto.InventoryQueryDTO;
import com.ecommerce.model.dto.InventoryUpdateDTO;
import com.ecommerce.model.entity.Inventory;
import com.ecommerce.model.entity.Product;
import com.ecommerce.model.vo.InventoryVO;
import com.ecommerce.service.InventoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Inventory getProductInventory(Long productId) {
        if (productId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "商品ID不能为空");
        }
        
        Inventory inventory = inventoryMapper.selectByProductId(productId);
        if (inventory == null) {
            log.warn("商品ID: {} 的库存信息不存在", productId);
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品库存信息不存在");
        }
        
        return inventory;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInventory(Long productId, int stockChange) {
        if (productId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "商品ID不能为空");
        }
        
        // 检查库存是否存在
        Inventory inventory = inventoryMapper.selectByProductId(productId);
        if (inventory == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品库存信息不存在");
        }
        
        // 检查库存是否足够（如果是减少库存）
        if (stockChange < 0 && inventory.getStock() + stockChange < 0) {
            throw new BusinessException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK, "库存不足，无法完成操作");
        }
        
        // 更新库存
        int result = inventoryMapper.updateStock(productId, stockChange);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存更新失败");
        }
        
        log.info("商品ID: {} 库存更新成功，变化量: {}", productId, stockChange);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLowStockThreshold(Long productId, int threshold) {
        if (productId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "商品ID不能为空");
        }
        
        if (threshold < 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "低库存阈值不能为负数");
        }
        
        // 检查库存是否存在
        Inventory inventory = inventoryMapper.selectByProductId(productId);
        if (inventory == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品库存信息不存在");
        }
        
        // 更新低库存阈值
        int result = inventoryMapper.updateLowStockThreshold(productId, threshold);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "低库存阈值更新失败");
        }
        
        log.info("商品ID: {} 低库存阈值更新成功，新阈值: {}", productId, threshold);
    }

    @Override
    public PageResult<Inventory> getLowStockProducts(int page, int size) {
        if (page <= 0 || size <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "分页参数错误");
        }
        
        // 配置分页参数
        PageHelper.startPage(page, size);
        
        // 查询低库存商品
        List<Inventory> lowStockProducts = inventoryMapper.selectLowStockProducts();
        
        // 转换为分页结果对象
        PageInfo<Inventory> pageInfo = new PageInfo<>(lowStockProducts);
        
        return PageResult.success(pageInfo);
    }
    
    /**
     * 创建商品库存记录
     * @param productId 商品ID
     * @param initialStock 初始库存
     * @param lowStockThreshold 低库存阈值
     */
    @Transactional(rollbackFor = Exception.class)
    public void createInventory(Long productId, Integer initialStock, Integer lowStockThreshold) {
        if (productId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "商品ID不能为空");
        }
        
        // 检查是否已存在库存记录
        Inventory existingInventory = inventoryMapper.selectByProductId(productId);
        if (existingInventory != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "商品库存记录已存在");
        }
        
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setStock(initialStock != null ? initialStock : 0);
        inventory.setLowStockThreshold(lowStockThreshold != null ? lowStockThreshold : 10);
        inventory.setUpdateTime(LocalDateTime.now());
        
        int result = inventoryMapper.insert(inventory);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存记录创建失败");
        }
        
        log.info("商品ID: {} 库存记录创建成功", productId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createInventory(InventoryCreateDTO createDTO) {
        if (createDTO == null || createDTO.getProductId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "创建参数不能为空");
        }
        
        // 检查商品是否存在
        Product product = productMapper.selectById(createDTO.getProductId());
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品不存在");
        }
        
        // 检查是否已存在库存记录
        if (hasInventoryRecord(createDTO.getProductId())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "商品库存记录已存在");
        }
        
        Inventory inventory = new Inventory();
        inventory.setProductId(createDTO.getProductId());
        inventory.setStock(createDTO.getInitialStock());
        inventory.setLowStockThreshold(createDTO.getLowStockThreshold());
        inventory.setUpdateTime(LocalDateTime.now());
        
        int result = inventoryMapper.insert(inventory);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存记录创建失败");
        }
        
        log.info("商品ID: {} 库存记录创建成功", createDTO.getProductId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateInventory(List<InventoryUpdateDTO> updateDTOList) {
        if (updateDTOList == null || updateDTOList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "批量更新参数不能为空");
        }
        
        for (InventoryUpdateDTO updateDTO : updateDTOList) {
            updateInventory(updateDTO.getProductId(), updateDTO.getStockChange());
            
            // 如果指定了低库存阈值，也一并更新
            if (updateDTO.getLowStockThreshold() != null) {
                updateLowStockThreshold(updateDTO.getProductId(), updateDTO.getLowStockThreshold());
            }
        }
        
        log.info("批量更新库存完成，共更新 {} 个商品", updateDTOList.size());
    }
    
    @Override
    public PageResult<InventoryVO> queryInventory(InventoryQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new InventoryQueryDTO();
        }
        
        // 配置分页参数
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        List<Inventory> inventoryList;
        
        if (queryDTO.getIsLowStock() != null && queryDTO.getIsLowStock()) {
            // 查询低库存商品
            inventoryList = inventoryMapper.selectLowStockProducts();
        } else if (queryDTO.getIsLowStock() != null && !queryDTO.getIsLowStock()) {
            // 查询非低库存商品（库存充足的商品）
            inventoryList = inventoryMapper.selectNonLowStockProducts();
        } else if (queryDTO.getMinStock() != null || queryDTO.getMaxStock() != null) {
            // 按库存范围查询
            inventoryList = inventoryMapper.selectByStockRange(queryDTO.getMinStock(), queryDTO.getMaxStock());
        } else if (queryDTO.getProductId() != null) {
            // 查询单个商品库存
            Inventory inventory = inventoryMapper.selectByProductId(queryDTO.getProductId());
            inventoryList = inventory != null ? List.of(inventory) : List.of();
        } else {
            // 查询所有库存
            inventoryList = inventoryMapper.selectAll();
        }
        
        // 转换为VO对象
        List<InventoryVO> inventoryVOList = inventoryList.stream().map(inventory -> {
            Product product = productMapper.selectById(inventory.getProductId());
            return new InventoryVO(inventory, product);
        }).toList();
        
        PageInfo<InventoryVO> pageInfo = new PageInfo<>(inventoryVOList);
        return PageResult.success(pageInfo);
    }
    
    @Override
    public Map<String, Object> getInventoryStats() {
        return inventoryMapper.selectInventoryStats();
    }
    
    @Override
    public boolean checkStock(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            return false;
        }
        
        Inventory inventory = inventoryMapper.selectByProductId(productId);
        if (inventory == null) {
            return false;
        }
        
        return inventory.hasEnoughStock(quantity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数不能为空或无效");
        }
        
        Inventory inventory = inventoryMapper.selectByProductId(productId);
        if (inventory == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品库存信息不存在");
        }
        
        // 检查可用库存是否充足
        if (!inventory.hasEnoughStock(quantity)) {
            throw new BusinessException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK, "可用库存不足，无法锁定");
        }
        
        // 锁定库存
        int result = inventoryMapper.lockStock(productId, quantity);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存锁定失败");
        }
        
        log.info("商品ID: {} 库存锁定成功，锁定数量: {}", productId, quantity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseStock(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数不能为空或无效");
        }
        
        Inventory inventory = inventoryMapper.selectByProductId(productId);
        if (inventory == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品库存信息不存在");
        }
        
        // 检查锁定库存是否充足
        Integer lockedStock = inventory.getLockedStock() != null ? inventory.getLockedStock() : 0;
        if (lockedStock < quantity) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "锁定库存不足，无法释放");
        }
        
        // 释放库存
        int result = inventoryMapper.releaseStock(productId, quantity);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存释放失败");
        }
        
        log.info("商品ID: {} 库存释放成功，释放数量: {}", productId, quantity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数不能为空或无效");
        }
        
        Inventory inventory = inventoryMapper.selectByProductId(productId);
        if (inventory == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品库存信息不存在");
        }
        
        // 检查库存是否充足
        if (inventory.getStock() < quantity) {
            throw new BusinessException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK, "库存不足，无法扣减");
        }
        
        // 扣减库存（减少实际库存数量）
        int result = inventoryMapper.updateStock(productId, -quantity);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存扣减失败");
        }
        
        log.info("商品ID: {} 库存扣减成功，扣减数量: {}", productId, quantity);
    }
    
    @Override
    public boolean hasInventoryRecord(Long productId) {
        if (productId == null) {
            return false;
        }
        return inventoryMapper.existsByProductId(productId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInventory(Long productId) {
        if (productId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "商品ID不能为空");
        }
        
        // 检查库存是否存在
        if (!hasInventoryRecord(productId)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品库存记录不存在");
        }
        
        int result = inventoryMapper.deleteByProductId(productId);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除库存记录失败");
        }
        
        log.info("商品ID: {} 库存记录删除成功", productId);
    }
}