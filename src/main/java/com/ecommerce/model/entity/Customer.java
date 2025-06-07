package com.ecommerce.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Customer {
    private Long id;
    private String username; // 用户名
    private String password; // 密码
    private String realName; // 真实姓名
    private String phone; // 手机号
    private String email; // 邮箱
    private String address; // 地址
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    // 用于创建新用户的便捷方法
    public static Customer create(String username, String password, String phone) {
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(password);
        customer.setPhone(phone);
        customer.setCreateTime(LocalDateTime.now());
        return customer;
    }
}