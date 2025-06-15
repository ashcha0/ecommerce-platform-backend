package com.ecommerce.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.ecommerce.model.entity.Order;

@Data
public class OrderQueryDTO {
    private String orderNo;
    private Long customerId;
    private Order.OrderStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;
    private int pageNum = 1;
    private int pageSize = 20;
    private int offset;
}