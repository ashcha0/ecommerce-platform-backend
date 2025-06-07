package com.ecommerce.service.impl;

import com.ecommerce.model.dto.ProductCreateDTO;
import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.entity.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageResult<Product> searchProducts(ProductQueryDTO queryDTO) {
        // 配置分页参数
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 执行查询
        List<Product> products = productMapper.selectByCondition(queryDTO);

        // 转换为分页结果对象
        return PageResult.success(new PageInfo<>(products));
    }

    @Override
    public Product getProductDetail(Long productId) {
        if (productId == null) {
            throw new BusinessException(400, "商品ID不能为空");
        }
        
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        return product;
    }

    @Override
    @Transactional
    public Product createProduct(ProductCreateDTO dto) {
        if (dto == null) {
            throw new BusinessException(400, "商品信息不能为空");
        }
        
        // 创建商品实体
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        
        // 设置默认值
        product.setSalesCount(0);
        product.setStatus(Product.ProductStatus.ON_SHELF.getCode());
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        
        // 插入数据库
        int result = productMapper.insert(product);
        if (result <= 0) {
            throw new BusinessException(500, "创建商品失败");
        }
        
        log.info("成功创建商品，ID: {}, 名称: {}", product.getId(), product.getName());
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductCreateDTO dto) {
        if (productId == null) {
            throw new BusinessException(400, "商品ID不能为空");
        }
        if (dto == null) {
            throw new BusinessException(400, "更新信息不能为空");
        }
        
        // 检查商品是否存在
        Product existingProduct = productMapper.selectById(productId);
        if (existingProduct == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        // 更新商品信息
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setId(productId);
        product.setUpdateTime(LocalDateTime.now());
        
        // 执行更新
        int result = productMapper.update(product);
        if (result <= 0) {
            throw new BusinessException(500, "更新商品失败");
        }
        
        log.info("成功更新商品，ID: {}, 名称: {}", productId, dto.getName());
        
        // 返回更新后的商品信息
        return productMapper.selectById(productId);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        if (productId == null) {
            throw new BusinessException(400, "商品ID不能为空");
        }
        
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        // 执行删除
        int result = productMapper.deleteById(productId);
        if (result <= 0) {
            throw new BusinessException(500, "删除商品失败");
        }
        
        log.info("成功删除商品，ID: {}, 名称: {}", productId, product.getName());
    }

    @Override
    @Transactional
    public void toggleProductStatus(Long productId, Integer status) {
        if (productId == null) {
            throw new BusinessException(400, "商品ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(400, "商品状态值无效，只能为0（下架）或1（上架）");
        }
        
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        // 如果要上架，可以直接上架（库存管理由独立的Inventory模块处理）
        // if (status == 1 && (product.getStock() == null || product.getStock() <= 0)) {
        //     throw new BusinessException(400, "库存不足，无法上架商品");
        // }
        
        // 执行状态更新
        int result = productMapper.updateStatus(productId, status);
        if (result <= 0) {
            throw new BusinessException(500, "更新商品状态失败");
        }
        
        String statusDesc = status == 1 ? "上架" : "下架";
        log.info("成功{}商品，ID: {}, 名称: {}", statusDesc, productId, product.getName());
    }
}