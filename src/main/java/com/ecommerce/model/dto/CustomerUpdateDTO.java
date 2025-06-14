package com.ecommerce.model.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class CustomerUpdateDTO {
    
    @NotNull(message = "客户ID不能为空")
    private Long id;
    
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;
}