package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class OrderCreateDTO {
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotEmpty(message = "订单项不能为空")
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotNull(message = "商品数量不能为空")
        @Min(value = 1, message = "商品数量必须大于0")
        private Integer quantity;
    }

    // 生成订单号
    public String generateOrderNo(String prefix) {
        return prefix + System.currentTimeMillis();
    }
}