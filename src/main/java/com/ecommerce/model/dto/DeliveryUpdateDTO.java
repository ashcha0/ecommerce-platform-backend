package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class DeliveryUpdateDTO {
    @NotBlank(message = "物流单号不能为空")
    private String trackingNo;

    @NotBlank(message = "物流公司不能为空")
    private String shipper;

    private DeliveryStatus status;
    private LocalDateTime shipTime;

    public enum DeliveryStatus {
        PAYING, SHIPPING, RECEIPTING, COMPLETED, CANCELLED, PROCESSING, PROCESSED
    }
}