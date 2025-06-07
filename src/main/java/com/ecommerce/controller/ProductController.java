package com.ecommerce.controller;

import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.model.entity.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.common.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "商品管理", description = "商品相关API接口")
@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "搜索商品", description = "根据条件搜索商品列表，支持分页")
    @GetMapping("/search")
    public PageResult<Product> searchProducts(ProductQueryDTO queryDTO) {
        try {
            // 预处理分页参数，确保参数有效
            if (queryDTO.getPageNum() <= 0) {
                queryDTO.setPageNum(1);
            }
            if (queryDTO.getPageSize() <= 0 || queryDTO.getPageSize() > 100) {
                queryDTO.setPageSize(10);
            }
            
            // 验证分页参数（在预处理之后进行验证）
            PageUtils.validatePageParams(queryDTO.getPageNum(), queryDTO.getPageSize());

            return productService.searchProducts(queryDTO);
        } catch (IllegalArgumentException e) {
            // 参数验证异常，返回具体错误信息
            return PageResult.error(400, "参数错误: " + e.getMessage());
        } catch (Exception e) {
            // 其他异常，记录日志并返回通用错误信息
            log.error("搜索商品时发生异常", e);
            return PageResult.error(500, "搜索商品失败，请稍后重试");
        }
    }
}