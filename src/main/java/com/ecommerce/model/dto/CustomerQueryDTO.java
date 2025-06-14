package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class CustomerQueryDTO {
    
    private String username; // 用户名（模糊查询）
    
    private String phone; // 手机号（模糊查询）
    
    private String email; // 邮箱（模糊查询）
    
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1; // 页码，默认第1页
    
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 10; // 每页大小，默认10条
    
    // 计算偏移量
    public Integer getOffset() {
        return (page - 1) * size;
    }
}