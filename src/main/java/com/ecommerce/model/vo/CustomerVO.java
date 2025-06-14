package com.ecommerce.model.vo;

import com.ecommerce.model.entity.Customer;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerVO {
    private Long id;
    private String username; // 用户名
    private String realName; // 真实姓名
    private String phone; // 手机号
    private String email; // 邮箱
    private String address; // 地址
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    
    public CustomerVO(Customer customer) {
        this.id = customer.getId();
        this.username = customer.getUsername();
        this.realName = customer.getRealName();
        this.phone = customer.getPhone();
        this.email = customer.getEmail();
        this.address = customer.getAddress();
        this.createTime = customer.getCreateTime();
        this.updateTime = customer.getUpdateTime();
    }
    
    // 无参构造函数
    public CustomerVO() {
    }
}