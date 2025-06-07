package com.ecommerce.service;

import com.ecommerce.model.dto.ProductCreateDTO;
import com.ecommerce.model.entity.Product;
import com.ecommerce.model.vo.PageResult;
import com.ecommerce.model.vo.ProductDetailVO;

public interface ProductService {
    /**
     * 创建新商品
     */
    Product createProduct(ProductCreateDTO dto);

    /**
     * 更新商品信息
     */
    Product updateProduct(Long id, ProductCreateDTO dto);

    /**
     * 获取商品详情
     */
    ProductDetailVO getProductDetail(Long id);

    /**
     * 商品上下架操作
     */
    void toggleProductStatus(Long id, boolean onShelf);

    /**
     * 分页查询商品列表
     */
    PageResult<Product> searchProducts(String keyword, Long storeId, int page, int size);
}