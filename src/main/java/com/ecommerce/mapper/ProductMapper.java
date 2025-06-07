package com.ecommerce.mapper;

import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.model.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    int insert(Product product);

    int update(Product product);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int deleteById(Long id);

    Product selectById(Long id);

    List<Product> selectByCondition(ProductQueryDTO queryDTO);

    List<Product> selectLowStockProducts(Integer threshold);
}