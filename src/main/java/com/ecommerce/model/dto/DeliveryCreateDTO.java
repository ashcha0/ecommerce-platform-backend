package com.ecommerce.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * 配送创建DTO
 */
@Data
public class DeliveryCreateDTO {
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    @Positive(message = "订单ID必须为正数")
    private Long orderId;
    
    /**
     * 物流单号
     */
    private String trackingNo;
    
    /**
     * 物流公司
     */
    private String shipper;
    
    /**
     * 预计到达时间
     */
    private LocalDateTime estimateTime;
}