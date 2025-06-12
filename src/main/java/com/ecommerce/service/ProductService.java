package com.ecommerce.service;

import com.ecommerce.model.dto.ProductCreateDTO;
import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.model.dto.ProductUpdateDTO;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.result.Result;
import com.ecommerce.model.entity.Product;

import java.util.List;

public interface ProductService {
    PageResult<Product> searchProducts(ProductQueryDTO queryDTO);

    Product getProductDetail(Long productId);

    /**
     * 创建商品
     * @param dto 商品创建信息
     * @return 创建的商品，如果URL格式无效会返回警告信息
     */
    Result<Product> createProduct(ProductCreateDTO dto);

    /**
     * 更新商品信息
     * @param id 商品ID
     * @param productUpdateDTO 商品更新信息
     * @return 更新结果，如果URL格式无效会返回警告信息
     */
    Result<String> updateProduct(Long id, ProductUpdateDTO productUpdateDTO);

    void deleteProduct(Long productId);

    void toggleProductStatus(Long productId, Integer status);
}