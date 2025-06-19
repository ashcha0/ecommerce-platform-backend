package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class DeliveryUpdateDTO {
    @NotBlank(message = "物流单号不能为空")
    private String trackingNo;

    @NotBlank(message = "物流公司不能为空")
    private String shipper;

    private DeliveryStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shipTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimateTime;

    public enum DeliveryStatus {
        PAYING, SHIPPING, RECEIPTING, COMPLETED, CANCELLED, PROCESSING, PROCESSED
    }
}