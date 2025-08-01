package com.ecommerce.service.impl;

import com.ecommerce.model.dto.ProductCreateDTO;
import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.model.dto.ProductUpdateDTO;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.result.Result;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.entity.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Date;
import com.ecommerce.common.constant.ErrorCode;

import java.time.ZoneId;

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
    @Cacheable(value = "product:detail", key = "#a0", unless = "#result == null")
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
    public Result<Product> createProduct(ProductCreateDTO dto) {
        if (dto == null) {
            throw new BusinessException(400, "商品信息不能为空");
        }
        
        String warningMessage = null;
        
        // 创建商品实体
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        
        // 验证并处理图片URL
        if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
            if (isValidUrl(dto.getImageUrl())) {
                product.setImageUrl(dto.getImageUrl());
            } else {
                // URL格式无效，设置为空
                product.setImageUrl(null);
                warningMessage = "URL格式无效，字段已被设置为空";
                log.warn("创建商品时图片URL格式无效: {}", dto.getImageUrl());
            }
        }
        
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
        
        // 返回结果，如果有警告信息则包含在响应中
        if (warningMessage != null) {
            return Result.success(product, warningMessage);
        } else {
            return Result.success(product);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"product:detail", "product:search"}, key = "#a0")
    public Result<String> updateProduct(Long id, ProductUpdateDTO productUpdateDTO) {
        log.info("更新商品，商品ID: {}", id);
        
        try {
            // 参数校验
            if (id == null || id <= 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "商品ID不能为空或无效");
            }
            
            // 检查商品是否存在
            Product existingProduct = productMapper.selectById(id);
            if (existingProduct == null) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品不存在");
            }
            
            // 构建更新对象，只更新非空字段
            Product product = new Product();
            product.setId(id);
            Date date = new Date();
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            product.setUpdateTime(localDateTime);
            
            boolean hasUpdate = false;
            String warningMessage = null;
            
            if (productUpdateDTO.getName() != null) {
                product.setName(productUpdateDTO.getName());
                hasUpdate = true;
            }
            
            if (productUpdateDTO.getDescription() != null) {
                product.setDescription(productUpdateDTO.getDescription());
                hasUpdate = true;
            }
            
            if (productUpdateDTO.getPrice() != null) {
                product.setPrice(productUpdateDTO.getPrice());
                hasUpdate = true;
            }
            
            if (productUpdateDTO.getImageUrl() != null) {
                // 验证URL格式
                if (isValidUrl(productUpdateDTO.getImageUrl())) {
                    product.setImageUrl(productUpdateDTO.getImageUrl());
                } else {
                    // URL格式无效，设置为空
                    product.setImageUrl(null);
                    warningMessage = "URL格式无效，字段已被设置为空";
                    log.warn("商品ID: {} 的图片URL格式无效: {}", id, productUpdateDTO.getImageUrl());
                }
                hasUpdate = true;
            }
            
            if (productUpdateDTO.getStatus() != null) {
                // 验证状态值并设置
                if (!Product.ProductStatus.isValidCode(productUpdateDTO.getStatus())) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "商品状态值无效");
                }
                
                Product.ProductStatus targetStatus = Product.ProductStatus.fromCode(productUpdateDTO.getStatus());
                if (!existingProduct.canChangeStatusTo(targetStatus.getCode())) {
                    throw new BusinessException(ErrorCode.PRODUCT_STATUS_INVALID,
                            String.format("商品状态不能从%s切换到%s",
                                    existingProduct.getStatusDesc(), targetStatus.getDesc()));
                }
                
                product.setProductStatus(targetStatus);
                hasUpdate = true;
            }
            
            if (!hasUpdate) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "没有需要更新的字段");
            }
            
            // 执行更新
            int result = productMapper.update(product);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.PRODUCT_UPDATE_FAILED, "商品更新失败");
            }
            
            log.info("商品更新成功，商品ID: {}", id);
            
            // 返回结果，如果有警告信息则包含在响应中
            if (warningMessage != null) {
                return Result.success(warningMessage);
            } else {
                return Result.success("商品更新成功");
            }
        } catch (BusinessException e) {
            log.error("更新商品失败，商品ID: {}, 错误: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新商品异常，商品ID: {}", id, e);
            throw new BusinessException(ErrorCode.PRODUCT_UPDATE_FAILED, "商品更新失败");
        }
    }
    
    /**
     * 验证URL格式是否有效
     * @param urlString URL字符串
     * @return 是否为有效URL
     */
    private boolean isValidUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return false;
        }
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"product:detail", "product:search"}, key = "#a0")
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
    @CacheEvict(value = {"product:detail", "product:search"}, key = "#a0")
    public void toggleProductStatus(Long productId, Integer status) {
        log.info("切换商品状态，商品ID: {}, 目标状态: {}", productId, status);
        
        try {
            // 参数校验
            if (productId == null || productId <= 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "商品ID不能为空或无效");
            }
            
            // 使用Product.ProductStatus枚举验证状态值
            if (status == null || !Product.ProductStatus.isValidCode(status)) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "商品状态值无效，只能为0（下架）或1（上架）");
            }
            
            // 检查商品是否存在
            Product existingProduct = productMapper.selectById(productId);
            if (existingProduct == null) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品不存在");
            }
            
            // 获取目标状态枚举
            Product.ProductStatus targetStatus = Product.ProductStatus.fromCode(status);
            
            // 检查状态切换是否合法
            if (!existingProduct.canChangeStatusTo(targetStatus.getCode())) {
                throw new BusinessException(ErrorCode.PRODUCT_STATUS_INVALID,
                        String.format("商品状态不能从%s切换到%s",
                                existingProduct.getStatusDesc(), targetStatus.getDesc()));
            }
            
            // 更新商品状态
            Product product = new Product();
            product.setId(productId);
            product.setProductStatus(targetStatus);
            Date date = new Date();
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            product.setUpdateTime(localDateTime);
            
            int result = productMapper.update(product);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.PRODUCT_STATUS_UPDATE_FAILED, "商品状态更新失败");
            }

            log.info("商品状态切换成功，商品ID: {}, 从{}切换到{}",
                    productId, existingProduct.getStatusDesc(), targetStatus.getDesc());
        } catch (BusinessException e) {
            log.error("切换商品状态失败，商品ID: {}, 目标状态: {}, 错误: {}", productId, status, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("切换商品状态异常，商品ID: {}, 目标状态: {}", productId, status, e);
            throw new BusinessException(ErrorCode.PRODUCT_STATUS_UPDATE_FAILED, "商品状态更新失败");
        }
    }
}