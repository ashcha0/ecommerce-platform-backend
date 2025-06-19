package com.ecommerce.controller;

import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.result.Result;
import com.ecommerce.model.dto.DeliveryCreateDTO;
import com.ecommerce.model.dto.DeliveryQueryDTO;
import com.ecommerce.model.dto.DeliveryUpdateDTO;
import com.ecommerce.model.entity.Delivery;
import com.ecommerce.model.vo.DeliveryVO;
import com.ecommerce.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 配送管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/delivery")
@Tag(name = "配送管理", description = "配送相关的API接口")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Operation(summary = "分页查询配送列表")
    @GetMapping
    public Result<PageResult<DeliveryVO>> getDeliveryList(
            @Parameter(description = "查询条件")
            DeliveryQueryDTO queryDTO) {
        
        log.info("分页查询配送列表，查询条件: {}", queryDTO);
        
        PageResult<DeliveryVO> result = deliveryService.getDeliveryList(queryDTO);
        return Result.success(result, "查询成功");
    }

    @Operation(summary = "根据订单ID查询配送信息")
    @GetMapping("/order/{orderId}")
    public Result<DeliveryVO> getDeliveryByOrderId(
            @Parameter(description = "订单ID", required = true)
            @PathVariable("orderId") 
            @NotNull(message = "订单ID不能为空") 
            @Positive(message = "订单ID必须为正数") 
            Long orderId) {
        
        log.info("查询配送信息，订单ID: {}", orderId);
        
        Delivery delivery = deliveryService.getDeliveryByOrderId(orderId);
        if (delivery != null) {
            DeliveryVO deliveryVO = DeliveryVO.from(delivery);
            return Result.success(deliveryVO, "查询成功");
        }
        return Result.fail(404, "未找到该订单的配送信息");
    }

    @Operation(summary = "根据配送ID查询配送信息")
    @GetMapping("/{deliveryId}")
    public Result<DeliveryVO> getDeliveryById(
            @Parameter(description = "配送ID", required = true)
            @PathVariable("deliveryId") 
            @NotNull(message = "配送ID不能为空") 
            @Positive(message = "配送ID必须为正数") 
            Long deliveryId) {
        
        log.info("查询配送信息，配送ID: {}", deliveryId);
        
        Delivery delivery = deliveryService.getDeliveryById(deliveryId);
        if (delivery != null) {
            DeliveryVO deliveryVO = DeliveryVO.from(delivery);
            return Result.success(deliveryVO, "查询成功");
        }
        return Result.fail(404, "未找到配送信息");
    }

    @Operation(summary = "创建配送信息")
    @PostMapping
    public Result<DeliveryVO> createDelivery(
            @Parameter(description = "配送创建信息", required = true)
            @Valid @RequestBody DeliveryCreateDTO createDTO) {
        
        log.info("创建配送信息，请求数据: {}", createDTO);
        
        try {
            Delivery delivery = deliveryService.createDeliveryWithDetails(createDTO);
            DeliveryVO deliveryVO = DeliveryVO.from(delivery);
            log.info("配送信息创建成功，配送ID: {}", delivery.getId());
            return Result.success(deliveryVO, "配送信息创建成功");
        } catch (Exception e) {
            log.error("创建配送信息失败，请求数据: {}", createDTO, e);
            return Result.fail(500, "创建配送信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据订单ID创建配送信息")
    @PostMapping("/order/{orderId}")
    public Result<DeliveryVO> createDeliveryByOrderId(
            @Parameter(description = "订单ID", required = true)
            @PathVariable("orderId") 
            @NotNull(message = "订单ID不能为空") 
            @Positive(message = "订单ID必须为正数") 
            Long orderId) {
        
        log.info("根据订单ID创建配送信息，订单ID: {}", orderId);
        
        try {
            Delivery delivery = deliveryService.createDelivery(orderId);
            DeliveryVO deliveryVO = DeliveryVO.from(delivery);
            return Result.success(deliveryVO, "配送信息创建成功");
        } catch (Exception e) {
            log.error("创建配送信息失败，订单ID: {}", orderId, e);
            return Result.fail(500, "创建配送信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新配送信息")
    @PutMapping("/order/{orderId}")
    public Result<Void> updateDelivery(
            @Parameter(description = "订单ID", required = true)
            @PathVariable("orderId") 
            @NotNull(message = "订单ID不能为空") 
            @Positive(message = "订单ID必须为正数") 
            Long orderId,
            @Parameter(description = "配送更新信息", required = true)
            @Valid @RequestBody DeliveryUpdateDTO updateDTO) {
        
        log.info("更新配送信息，订单ID: {}, 更新内容: {}", orderId, updateDTO);
        
        boolean success = deliveryService.updateDelivery(orderId, updateDTO);
        if (success) {
            return Result.success(null, "配送信息更新成功");
        }
        return Result.fail(400, "配送信息更新失败");
    }

    @Operation(summary = "更新配送状态")
    @PutMapping("/order/{orderId}/status")
    public Result<Void> updateDeliveryStatus(
            @Parameter(description = "订单ID", required = true)
            @PathVariable("orderId") 
            @NotNull(message = "订单ID不能为空") 
            @Positive(message = "订单ID必须为正数") 
            Long orderId,
            @Parameter(description = "配送状态", required = true)
            @RequestParam("status") 
            @NotBlank(message = "配送状态不能为空") 
            String status) {
        
        log.info("更新配送状态，订单ID: {}, 状态: {}", orderId, status);
        
        // 管理系统拥有最高权限，可以无视状态规则直接修改任何状态
        log.info("管理系统强制更新配送状态，跳过状态验证");
        
        boolean success = deliveryService.updateDeliveryStatus(orderId, status);
        if (success) {
            return Result.success(null, "配送状态更新成功");
        }
        return Result.fail(400, "配送状态更新失败");
    }

    @Operation(summary = "发货")
    @PostMapping("/order/{orderId}/ship")
    public Result<Void> shipOrder(
            @Parameter(description = "订单ID", required = true)
            @PathVariable("orderId") 
            @NotNull(message = "订单ID不能为空") 
            @Positive(message = "订单ID必须为正数") 
            Long orderId,
            @Parameter(description = "物流单号", required = true)
            @RequestParam("trackingNo") 
            @NotBlank(message = "物流单号不能为空") 
            String trackingNo,
            @Parameter(description = "物流公司", required = true)
            @RequestParam("shipper") 
            @NotBlank(message = "物流公司不能为空") 
            String shipper) {
        
        log.info("=== 发货接口开始处理 ===");
        log.info("接收到发货请求 - 订单ID: {}, 物流单号: {}, 物流公司: {}", orderId, trackingNo, shipper);
        
        try {
            log.info("开始调用发货服务");
            boolean success = deliveryService.shipOrder(orderId, trackingNo, shipper);
            log.info("发货服务调用结果: {}", success ? "成功" : "失败");
            
            if (success) {
                log.info("发货处理成功，返回成功响应");
                return Result.success(null, "发货成功");
            } else {
                log.error("发货处理失败，返回失败响应");
                return Result.fail(400, "发货失败");
            }
        } catch (Exception e) {
            log.error("发货处理异常: {}", e.getMessage(), e);
            return Result.fail(500, "发货处理异常: " + e.getMessage());
        } finally {
            log.info("=== 发货接口处理结束 ===");
        }
    }

    @Operation(summary = "确认收货")
    @PostMapping("/order/{orderId}/confirm")
    public Result<Void> confirmDelivery(
            @Parameter(description = "订单ID", required = true)
            @PathVariable("orderId") 
            @NotNull(message = "订单ID不能为空") 
            @Positive(message = "订单ID必须为正数") 
            Long orderId) {
        
        log.info("确认收货，订单ID: {}", orderId);
        
        boolean success = deliveryService.confirmDelivery(orderId);
        if (success) {
            return Result.success(null, "确认收货成功");
        }
        return Result.fail(400, "确认收货失败");
    }

    @Operation(summary = "确认付款")
    @PostMapping("/order/{orderId}/confirm-payment")
    public Result<Void> confirmPayment(
            @Parameter(description = "订单ID", required = true)
            @PathVariable("orderId") 
            @NotNull(message = "订单ID不能为空") 
            @Positive(message = "订单ID必须为正数") 
            Long orderId) {
        
        log.info("确认付款，订单ID: {}", orderId);
        
        boolean success = deliveryService.confirmPayment(orderId);
        if (success) {
            return Result.success(null, "确认付款成功");
        }
        return Result.fail(400, "确认付款失败");
    }

    @Operation(summary = "取消订单")
    @PostMapping("/order/{orderId}/cancel")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单ID", required = true)
            @PathVariable("orderId") 
            @NotNull(message = "订单ID不能为空") 
            @Positive(message = "订单ID必须为正数") 
            Long orderId) {
        
        log.info("取消订单，订单ID: {}", orderId);
        
        boolean success = deliveryService.cancelOrder(orderId);
        if (success) {
            return Result.success(null, "取消订单成功");
        }
        return Result.fail(400, "取消订单失败");
    }

    @Operation(summary = "申请售后")
    @PostMapping("/order/{orderId}/apply-after-sale")
    public Result<Void> applyAfterSale(
            @Parameter(description = "订单ID", required = true)
            @PathVariable("orderId") 
            @NotNull(message = "订单ID不能为空") 
            @Positive(message = "订单ID必须为正数") 
            Long orderId) {
        
        log.info("申请售后，订单ID: {}", orderId);
        
        boolean success = deliveryService.applyAfterSale(orderId);
        if (success) {
            return Result.success(null, "申请售后成功");
        }
        return Result.fail(400, "申请售后失败");
    }

    @Operation(summary = "完成售后")
    @PostMapping("/order/{orderId}/complete-after-sale")
    public Result<Void> completeAfterSale(
            @Parameter(description = "订单ID", required = true)
            @PathVariable("orderId") 
            @NotNull(message = "订单ID不能为空") 
            @Positive(message = "订单ID必须为正数") 
            Long orderId) {
        
        log.info("完成售后，订单ID: {}", orderId);
        
        boolean success = deliveryService.completeAfterSale(orderId);
        if (success) {
            return Result.success(null, "完成售后成功");
        }
        return Result.fail(400, "完成售后失败");
    }
}