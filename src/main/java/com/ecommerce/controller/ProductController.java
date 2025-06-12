package com.ecommerce.controller;

import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.result.Result;
import com.ecommerce.model.dto.ProductCreateDTO;
import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.model.dto.ProductUpdateDTO;
import com.ecommerce.model.entity.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.common.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.constant.ErrorCode;

@RestController
@RequestMapping("/products")
@Slf4j
@Validated
@Tag(name = "商品管理", description = "商品相关的API接口，包括商品的增删改查、状态管理、库存查询等功能")
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

    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "根据条件搜索商品，支持按名称、分类、价格范围、状态等条件进行筛选，支持分页查询")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "搜索成功", content = @Content(schema = @Schema(implementation = Result.class), examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"操作成功\", \"data\": {\"records\": [], \"total\": 0, \"pageNum\": 1, \"pageSize\": 10}}"))),
            @ApiResponse(responseCode = "400", description = "参数验证失败", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public PageResult<Product> searchProducts(
            @Parameter(description = "商品查询条件") ProductQueryDTO queryDTO) {
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

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品的详细信息，包括名称、描述、价格、库存、状态等")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = Result.class), examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"操作成功\", \"data\": {\"id\": 1, \"name\": \"商品名称\", \"price\": 99.99}}"))),
            @ApiResponse(responseCode = "404", description = "商品不存在", content = @Content(schema = @Schema(implementation = Result.class), examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"商品不存在\", \"data\": null}")))
    })
    public Result<Product> getProductDetail(
            @Parameter(description = "商品ID", required = true, example = "1") @PathVariable("id") @NotNull(message = "商品ID不能为空") Long id) {
        try {
            Product product = productService.getProductDetail(id);
            return Result.success(product);
        } catch (Exception e) {
            log.error("获取商品详情失败，商品ID: {}", id, e);
            throw e; // 让全局异常处理器处理
        }
    }

    @PostMapping
    @Operation(summary = "创建商品", description = "创建新的商品信息，包括商品名称、描述、价格、库存等基本信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "400", description = "参数验证失败", content = @Content(schema = @Schema(implementation = Result.class), examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"商品名称不能为空\", \"data\": null}"))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public Result<Product> createProduct(
            @Parameter(description = "商品创建信息", required = true) @RequestBody @Valid ProductCreateDTO createDTO) {
        try {
            Product product = productService.createProduct(createDTO);
            return Result.success(product);
        } catch (Exception e) {
            log.error("创建商品失败，商品名称: {}", createDTO.getName(), e);
            throw e; // 让全局异常处理器处理
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品信息", description = "根据商品ID更新商品的基本信息，如名称、描述、价格、库存等")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "400", description = "参数验证失败", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "商品不存在", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public Result<String> updateProduct(
            @Parameter(description = "商品ID", required = true, example = "1") @PathVariable("id") Long id,
            @Parameter(description = "商品更新信息", required = true) @Valid @RequestBody ProductUpdateDTO productUpdateDTO) {
        try {
            return productService.updateProduct(id, productUpdateDTO);
        } catch (BusinessException e) {
            log.error("更新商品失败，商品ID: {}, 错误: {}", id, e.getMessage());
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新商品异常，商品ID: {}", id, e);
            return Result.fail(ErrorCode.INTERNAL_SERVER_ERROR, "更新商品失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "根据商品ID删除商品信息，删除后商品将无法恢复")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "商品不存在", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public Result<Void> deleteProduct(
            @Parameter(description = "商品ID", required = true, example = "1") @PathVariable("id") @NotNull(message = "商品ID不能为空") Long id) {
        try {
            productService.deleteProduct(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除商品失败，商品ID: {}", id, e);
            throw e; // 让全局异常处理器处理
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "切换商品状态", description = "切换商品的启用/禁用状态，启用状态的商品可以正常销售，禁用状态的商品不可销售")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "状态切换成功", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "商品不存在", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public Result<Void> toggleProductStatus(
            @Parameter(description = "商品ID", required = true, example = "1") @PathVariable("id") @NotNull(message = "商品ID不能为空") Long id,
            @Parameter(description = "商品状态：0-下架，1-上架", required = true) @RequestParam("status") @NotNull(message = "商品状态不能为空") Integer status) {
        try {
            productService.toggleProductStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            log.error("切换商品状态失败，商品ID: {}, 状态: {}", id, status, e);
            throw e; // 让全局异常处理器处理
        }
    }
}