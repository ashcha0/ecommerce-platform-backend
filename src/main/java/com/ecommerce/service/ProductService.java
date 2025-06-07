package com.ecommerce.service;

import com.ecommerce.model.dto.ProductCreateDTO;
import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.model.entity.Product;

import java.util.List;

public interface ProductService {
    PageResult<Product> searchProducts(ProductQueryDTO queryDTO);

    Product getProductDetail(Long productId);

    Product createProduct(ProductCreateDTO dto);

    Product updateProduct(Long productId, ProductCreateDTO dto);

    void deleteProduct(Long productId);

    void toggleProductStatus(Long productId, Integer status);
}