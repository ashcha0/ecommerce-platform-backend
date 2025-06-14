package com.ecommerce.controller;

import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.result.Result;
import com.ecommerce.model.dto.InventoryCreateDTO;
import com.ecommerce.model.dto.InventoryQueryDTO;
import com.ecommerce.model.dto.InventoryUpdateDTO;
import com.ecommerce.model.entity.Inventory;
import com.ecommerce.model.vo.InventoryVO;
import com.ecommerce.service.InventoryService;
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

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/inventory")
@Slf4j
@Validated
@Tag(name = "库存管理", description = "商品库存相关的API接口，包括库存查询、更新、低库存预警等功能")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Operation(summary = "获取商品库存信息", description = "根据商品ID获取库存详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(schema = @Schema(implementation = Result.class), 
                    examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"操作成功\", \"data\": {\"id\": 1, \"productId\": 1, \"stock\": 100, \"lowStockThreshold\": 10, \"updateTime\": \"2024-01-01T10:00:00\"}}"))),
            @ApiResponse(responseCode = "400", description = "参数错误", 
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "库存信息不存在", 
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("/product/{productId}")
    public Result<Inventory> getProductInventory(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable("productId") @NotNull(message = "商品ID不能为空") @Positive(message = "商品ID必须为正数") Long productId) {

        try {
            Inventory inventory = inventoryService.getProductInventory(productId);
            return Result.success(inventory);
        } catch (Exception e) {
            log.error("获取商品库存信息失败，商品ID: {}", productId, e);
            return Result.fail(500, "获取库存信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新商品库存", description = "更新指定商品的库存数量，支持增加或减少")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功", 
                    content = @Content(schema = @Schema(implementation = Result.class), 
                    examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"库存更新成功\", \"data\": null}"))),
            @ApiResponse(responseCode = "400", description = "参数错误或库存不足", 
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "商品不存在", 
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PutMapping("/product/{productId}/stock")
    public Result<Void> updateInventory(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable("productId") @NotNull(message = "商品ID不能为空") @Positive(message = "商品ID必须为正数") Long productId,
            @Parameter(description = "库存变化量，正数为增加，负数为减少", required = true, example = "10")
            @RequestParam("stockChange") @NotNull(message = "库存变化量不能为空") Integer stockChange) {
        try {
            inventoryService.updateInventory(productId, stockChange);
            return Result.success();
        } catch (Exception e) {
            log.error("更新商品库存失败，商品ID: {}, 变化量: {}", productId, stockChange, e);
            return Result.fail(500, "更新库存失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新低库存阈值", description = "设置指定商品的低库存预警阈值")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功", 
                    content = @Content(schema = @Schema(implementation = Result.class), 
                    examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"低库存阈值更新成功\", \"data\": null}"))),
            @ApiResponse(responseCode = "400", description = "参数错误", 
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "商品不存在", 
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PutMapping("/product/{productId}/threshold")
    public Result<Void> updateLowStockThreshold(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable("productId") @NotNull(message = "商品ID不能为空") @Positive(message = "商品ID必须为正数") Long productId,
            @Parameter(description = "低库存阈值", required = true, example = "10")
            @RequestParam("threshold") @NotNull(message = "低库存阈值不能为空") @PositiveOrZero(message = "低库存阈值不能为负数") Integer threshold) {
        try {
            inventoryService.updateLowStockThreshold(productId, threshold);
            return Result.success();
        } catch (Exception e) {
            log.error("更新低库存阈值失败，商品ID: {}, 阈值: {}", productId, threshold, e);
            return Result.fail(500, "更新低库存阈值失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取低库存商品列表", description = "分页查询当前库存低于阈值的商品列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(schema = @Schema(implementation = PageResult.class), 
                    examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"操作成功\", \"data\": {\"records\": [], \"total\": 0, \"pageNum\": 1, \"pageSize\": 10}}"))),
            @ApiResponse(responseCode = "400", description = "参数验证失败", 
                    content = @Content(schema = @Schema(implementation = PageResult.class)))
    })
    @GetMapping("/low-stock")
    public PageResult<Inventory> getLowStockProducts(
            @Parameter(description = "页码，默认为1", example = "1")
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "每页大小，默认为10，最大100", example = "10")
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        try {
            // 预处理分页参数，确保参数有效
            if (page <= 0) {
                page = 1;
            }
            if (size <= 0 || size > 100) {
                size = 10;
            }
            
            // 验证分页参数
            PageUtils.validatePageParams(page, size);
            
            return inventoryService.getLowStockProducts(page, size);
        } catch (IllegalArgumentException e) {
            // 参数验证异常，返回具体错误信息
            return PageResult.error(400, "参数错误: " + e.getMessage());
        } catch (Exception e) {
            // 其他异常，记录日志并返回通用错误信息
            log.error("获取低库存商品列表时发生异常", e);
            return PageResult.error(500, "获取低库存商品列表失败，请稍后重试");
        }
    }

    @Operation(summary = "批量更新库存", description = "批量更新多个商品的库存数量")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "批量更新成功", 
                    content = @Content(schema = @Schema(implementation = Result.class), 
                    examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"批量库存更新成功\", \"data\": null}"))),
            @ApiResponse(responseCode = "400", description = "参数错误", 
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PutMapping("/batch-update")
    public Result<Void> batchUpdateInventory(
            @Parameter(description = "商品ID列表，用逗号分隔", required = true, example = "1,2,3")
            @RequestParam("productIds") String productIds,
            @Parameter(description = "对应的库存变化量列表，用逗号分隔", required = true, example = "10,-5,20")
            @RequestParam("stockChanges") String stockChanges) {
        try {
            String[] productIdArray = productIds.split(",");
            String[] stockChangeArray = stockChanges.split(",");
            
            if (productIdArray.length != stockChangeArray.length) {
                return Result.fail(400, "商品ID数量与库存变化量数量不匹配");
            }
            
            for (int i = 0; i < productIdArray.length; i++) {
                Long productId = Long.parseLong(productIdArray[i].trim());
                Integer stockChange = Integer.parseInt(stockChangeArray[i].trim());
                inventoryService.updateInventory(productId, stockChange);
            }
            
            return Result.success();
        } catch (NumberFormatException e) {
            return Result.fail(400, "参数格式错误，请检查商品ID和库存变化量格式");
        } catch (Exception e) {
            log.error("批量更新库存失败", e);
            return Result.fail(500, "批量更新库存失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "创建商品库存记录", description = "为新商品创建库存记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功", 
                    content = @Content(schema = @Schema(implementation = Result.class), 
                    examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"库存记录创建成功\", \"data\": null}"))),
            @ApiResponse(responseCode = "400", description = "参数错误或商品已存在库存记录", 
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "商品不存在", 
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PostMapping("/create")
    public Result<Void> createInventory(@RequestBody @jakarta.validation.Valid InventoryCreateDTO createDTO) {
        try {
            inventoryService.createInventory(createDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("创建库存记录失败，商品ID: {}", createDTO.getProductId(), e);
            return Result.fail(500, "创建库存记录失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "条件查询库存列表", description = "根据条件查询库存信息，支持多种筛选条件")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(schema = @Schema(implementation = PageResult.class), 
                    examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"操作成功\", \"data\": {\"records\": [], \"total\": 0, \"pageNum\": 1, \"pageSize\": 10}}"))),
            @ApiResponse(responseCode = "400", description = "参数验证失败", 
                    content = @Content(schema = @Schema(implementation = PageResult.class)))
    })
    @PostMapping("/query")
    public PageResult<InventoryVO> queryInventory(@RequestBody InventoryQueryDTO queryDTO) {
        try {
            return inventoryService.queryInventory(queryDTO);
        } catch (Exception e) {
            log.error("查询库存列表时发生异常", e);
            return PageResult.error(500, "查询库存列表失败，请稍后重试");
        }
    }
    
    @Operation(summary = "获取库存统计信息", description = "获取库存的统计数据，包括总商品数、总库存、低库存商品数等")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(schema = @Schema(implementation = Result.class), 
                    examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"操作成功\", \"data\": {\"totalProducts\": 100, \"totalStock\": 5000, \"lowStockCount\": 5, \"avgStock\": 50.0}}")))
    })
    @GetMapping("/stats")
    public Result<java.util.Map<String, Object>> getInventoryStats() {
        try {
            java.util.Map<String, Object> stats = inventoryService.getInventoryStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取库存统计信息时发生异常", e);
            return Result.fail(500, "获取库存统计信息失败，请稍后重试");
        }
    }
    
    @Operation(summary = "检查商品库存记录", description = "检查指定商品是否已有库存记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(schema = @Schema(implementation = Result.class), 
                    examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"操作成功\", \"data\": true}")))
    })
    @GetMapping("/exists/{productId}")
    public Result<Boolean> hasInventoryRecord(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable @NotNull(message = "商品ID不能为空") @Positive(message = "商品ID必须为正数") Long productId) {
        try {
            boolean exists = inventoryService.hasInventoryRecord(productId);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查库存记录时发生异常，商品ID: {}", productId, e);
            return Result.fail(500, "检查库存记录失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "删除商品库存记录", description = "删除指定商品的库存记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功", 
                    content = @Content(schema = @Schema(implementation = Result.class), 
                    examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"库存记录删除成功\", \"data\": null}"))),
            @ApiResponse(responseCode = "400", description = "参数错误", 
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "库存记录不存在", 
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @DeleteMapping("/product/{productId}")
    public Result<Void> deleteInventory(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable("productId") @NotNull(message = "商品ID不能为空") @Positive(message = "商品ID必须为正数") Long productId) {

        try {
            inventoryService.deleteInventory(productId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除库存记录失败，商品ID: {}", productId, e);
            return Result.fail(500, "删除库存记录失败: " + e.getMessage());
        }
    }
}