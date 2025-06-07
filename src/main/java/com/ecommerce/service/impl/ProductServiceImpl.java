package com.ecommerce.service.impl;

import com.ecommerce.model.dto.ProductCreateDTO;
import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.entity.Product;
import com.ecommerce.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        // 实现获取商品详情的逻辑
        return productMapper.selectById(productId);
    }

    @Override
    public Product createProduct(ProductCreateDTO dto) {
        // 实现创建商品的逻辑
        Product product = new Product();
        // 将dto的属性复制到product
        // ...
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product updateProduct(Long productId, ProductCreateDTO dto) {
        // 实现更新商品的逻辑
        Product product = productMapper.selectById(productId);
        // 将dto的属性更新到product
        // ...
        productMapper.update(product);
        return product;
    }

    @Override
    public void toggleProductStatus(Long productId, boolean status) {
        // 实现切换商品状态的逻辑
        productMapper.updateStatus(productId, status ? 1 : 0);
    }
}