package com.ecommerce.mapper;

import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.model.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProductMapper {
    int insert(Product product);

    int update(Product product);

    int updateStatus(Long id, Integer status);

    Product selectById(Long id);

    List<Product> selectByCondition(ProductQueryDTO queryDTO);

    List<Product> selectLowStockProducts(Integer threshold);
}