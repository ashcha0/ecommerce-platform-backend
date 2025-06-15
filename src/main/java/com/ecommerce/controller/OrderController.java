package com.ecommerce.controller;

import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.result.Result;
import com.ecommerce.model.dto.OrderCreateDTO;
import com.ecommerce.model.dto.OrderQueryDTO;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.view.OrderDetailsView;
import com.ecommerce.model.vo.OrderDetailVO;
import com.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单", description = "根据商品信息创建新订单")
    public Result<Order> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        Order order = orderService.createOrder(dto);
        return Result.success(order, "订单创建成功");
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "查询订单详情", description = "根据订单ID查询订单详细信息")
    public Result<OrderDetailVO> getOrderDetail(
        @Parameter(description = "订单ID")
        @PathVariable("orderId") @NotNull(message = "订单ID不能为空") @Positive(message = "订单ID必须为正数") Long orderId) {
        OrderDetailVO orderDetail = orderService.getOrderDetail(orderId);
        return Result.success(orderDetail);
    }

    @GetMapping
    @Operation(summary = "查询订单列表", description = "分页查询订单列表，支持条件筛选")
    public Result<PageResult<OrderDetailsView>> searchOrders(
        @Parameter(description = "查询条件") OrderQueryDTO queryDTO) {
        PageResult<OrderDetailsView> result = orderService.searchOrders(queryDTO);
        return Result.success(result);
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "取消指定订单")
    public Result<Void> cancelOrder(
        @Parameter(description = "订单ID")
        @PathVariable("orderId") @NotNull(message = "订单ID不能为空") @Positive(message = "订单ID必须为正数") Long orderId) {
        orderService.cancelOrder(orderId);
        return Result.success(null, "订单取消成功");
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "更新订单状态", description = "更新订单状态")
    public Result<Void> updateOrderStatus(
        @Parameter(description = "订单ID")
        @PathVariable("orderId") @NotNull(message = "订单ID不能为空") @Positive(message = "订单ID必须为正数") Long orderId,
        @Parameter(description = "订单状态") @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return Result.success(null, "订单状态更新成功");
    }

    @PutMapping("/{orderId}/pay")
    @Operation(summary = "支付订单", description = "标记订单为已支付状态")
    public Result<Void> payOrder(
        @Parameter(description = "订单ID")
        @PathVariable("orderId") @NotNull(message = "订单ID不能为空") @Positive(message = "订单ID必须为正数") Long orderId) {
        orderService.updateOrderStatus(orderId, "PAID");
        return Result.success(null, "订单支付成功");
    }

    @PutMapping("/{orderId}/confirm")
    @Operation(summary = "确认收货", description = "确认收货，订单完成")
    public Result<Void> confirmOrder(
        @Parameter(description = "订单ID")
        @PathVariable("orderId") @NotNull(message = "订单ID不能为空") @Positive(message = "订单ID必须为正数") Long orderId) {
        orderService.updateOrderStatus(orderId, "COMPLETED");
        return Result.success(null, "确认收货成功");
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "查询订单商品明细", description = "获取订单的商品明细信息")
    public Result<OrderDetailVO> getOrderItems(
        @Parameter(description = "订单ID")
        @PathVariable("orderId") @NotNull(message = "订单ID不能为空") @Positive(message = "订单ID必须为正数") Long orderId) {
        OrderDetailVO orderDetail = orderService.getOrderDetail(orderId);
        return Result.success(orderDetail);
    }

    @GetMapping("/{orderId}/delivery")
    @Operation(summary = "查询订单配送信息", description = "获取订单的配送信息")
    public Result<OrderDetailVO.DeliveryVO> getOrderDeliveryInfo(
        @Parameter(description = "订单ID")
        @PathVariable("orderId") @NotNull(message = "订单ID不能为空") @Positive(message = "订单ID必须为正数") Long orderId) {
        OrderDetailVO.DeliveryVO deliveryInfo = orderService.getOrderDeliveryInfo(orderId);
        return Result.success(deliveryInfo);
    }

    @GetMapping("/stats/customer/{customerId}")
    @Operation(summary = "客户订单统计", description = "统计指定客户的订单信息")
    public Result<Map<String, Object>> getCustomerOrderStats(
            @Parameter(description = "客户ID") @PathVariable Long customerId) {
        // TODO: 实现客户订单统计逻辑
        return Result.success(Map.of("message", "功能开发中"));
    }

    @GetMapping("/stats/status")
    @Operation(summary = "订单状态统计", description = "统计各状态订单数量")
    public Result<Map<String, Object>> getOrderStatusStats() {
        // TODO: 实现订单状态统计逻辑
        return Result.success(Map.of("message", "功能开发中"));
    }

    @GetMapping("/stats/amount")
    @Operation(summary = "订单金额统计", description = "统计订单金额信息")
    public Result<Map<String, Object>> getOrderAmountStats() {
        // TODO: 实现订单金额统计逻辑
        return Result.success(Map.of("message", "功能开发中"));
    }
}