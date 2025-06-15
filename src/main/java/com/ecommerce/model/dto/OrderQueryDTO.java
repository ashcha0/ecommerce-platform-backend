package com.ecommerce.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderQueryDTO {
    private String orderNo;
    private Long customerId;
    private OrderStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int pageNum = 1;
    private int pageSize = 20;
    private int offset;

    public enum OrderStatus {
        CREATED, PAID, SHIPPING, COMPLETED, CANCELLED
    }
}