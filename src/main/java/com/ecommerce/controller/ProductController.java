package com.ecommerce.controller;

import com.ecommerce.model.dto.ProductCreateDTO;
import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.model.dto.ProductUpdateDTO;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.result.Result;
import com.ecommerce.model.entity.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.common.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Tag(name = "商品管理", description = "商品相关API接口")
@RestController
@RequestMapping("/api/products")
@Slf4j
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "获取商品列表", description = "获取所有商品列表，支持分页")
    @GetMapping
    public PageResult<Product> getProducts(
            @Parameter(description = "页码，默认为1") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小，默认为10") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            // 预处理分页参数，确保参数有效
            if (pageNum <= 0) {
                pageNum = 1;
            }
            if (pageSize <= 0 || pageSize > 100) {
                pageSize = 10;
            }
            
            // 验证分页参数（在预处理之后进行验证）
            PageUtils.validatePageParams(pageNum, pageSize);

            // 创建查询DTO，只设置分页参数
            ProductQueryDTO queryDTO = new ProductQueryDTO();
            queryDTO.setPageNum(pageNum);
            queryDTO.setPageSize(pageSize);

            return productService.searchProducts(queryDTO);
        } catch (IllegalArgumentException e) {
            // 参数验证异常，返回具体错误信息
            return PageResult.error(400, "参数错误: " + e.getMessage());
        } catch (Exception e) {
            // 其他异常，记录日志并返回通用错误信息
            log.error("获取商品列表时发生异常", e);
            return PageResult.error(500, "获取商品列表失败，请稍后重试");
        }
    }

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

    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详细信息")
    @GetMapping("/{id}")
    public Result<Product> getProductDetail(
            @Parameter(description = "商品ID", required = true)
            @PathVariable("id") @NotNull(message = "商品ID不能为空") Long id) {
        try {
            Product product = productService.getProductDetail(id);
            return Result.success(product);
        } catch (Exception e) {
            log.error("获取商品详情失败，商品ID: {}", id, e);
            throw e; // 让全局异常处理器处理
        }
    }

    @Operation(summary = "创建商品", description = "创建新的商品")
    @PostMapping
    public Result<Product> createProduct(
            @Parameter(description = "商品创建信息", required = true)
            @RequestBody @Valid ProductCreateDTO createDTO) {
        try {
            Product product = productService.createProduct(createDTO);
            return Result.success(product);
        } catch (Exception e) {
            log.error("创建商品失败，商品名称: {}", createDTO.getName(), e);
            throw e; // 让全局异常处理器处理
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品信息", description = "根据商品ID更新商品信息")
    public Result<Void> updateProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO productUpdateDTO) {
        try {
            productService.updateProduct(id, productUpdateDTO);
            return Result.success();
        } catch (BusinessException e) {
            log.error("更新商品失败，商品ID: {}, 错误: {}", id, e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新商品异常，商品ID: {}", id, e);
            return Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "更新商品失败");
        }
    }

    @Operation(summary = "删除商品", description = "根据商品ID删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(
            @Parameter(description = "商品ID", required = true)
            @PathVariable("id") @NotNull(message = "商品ID不能为空") Long id) {
        try {
            productService.deleteProduct(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除商品失败，商品ID: {}", id, e);
            throw e; // 让全局异常处理器处理
        }
    }

    @Operation(summary = "切换商品状态", description = "切换商品的上架/下架状态")
    @PatchMapping("/{id}/status")
    public Result<Void> toggleProductStatus(
            @Parameter(description = "商品ID", required = true)
            @PathVariable("id") @NotNull(message = "商品ID不能为空") Long id,
            @Parameter(description = "商品状态：0-下架，1-上架", required = true)
            @RequestParam("status") @NotNull(message = "商品状态不能为空") Integer status) {
        try {
            productService.toggleProductStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            log.error("切换商品状态失败，商品ID: {}, 状态: {}", id, status, e);
            throw e; // 让全局异常处理器处理
        }
    }
}