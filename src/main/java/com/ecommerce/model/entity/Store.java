package com.ecommerce.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Store {
    private Long id;
    private String name; // 店铺名称
    private String description; // 店铺描述
    private String owner; // 店主
    private String contactPhone; // 联系电话
    private LocalDateTime createTime; // 创建时间

    public static Store create(String name, String owner, String contactPhone) {
        Store store = new Store();
        store.setName(name);
        store.setOwner(owner);
        store.setContactPhone(contactPhone);
        store.setCreateTime(LocalDateTime.now());
        return store;
    }
}