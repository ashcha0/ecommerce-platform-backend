package com.ecommerce.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
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
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String consigneeName;
    
    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "收货人电话格式不正确")
    private String consigneePhone;
    
    /**
     * 配送地址
     */
    @NotBlank(message = "配送地址不能为空")
    private String deliveryAddress;
    
    /**
     * 预计到达时间
     */
    private LocalDateTime estimateTime;
    
    /**
     * 备注
     */
    private String remark;
}