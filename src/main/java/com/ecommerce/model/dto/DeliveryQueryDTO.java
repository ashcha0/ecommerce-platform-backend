package com.ecommerce.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 配送查询条件DTO
 */
@Data
public class DeliveryQueryDTO {
    /**
     * 订单ID
     */
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
     * 配送状态
     */
    private String status;
    
    /**
     * 发货时间开始
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime shipTimeStart;
    
    /**
     * 发货时间结束
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime shipTimeEnd;
    
    /**
     * 创建时间开始
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createTimeStart;
    
    /**
     * 创建时间结束
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createTimeEnd;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 计算偏移量
     * @return 偏移量
     */
    public Integer getOffset() {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        return (pageNum - 1) * pageSize;
    }
}