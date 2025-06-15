package com.ecommerce.common.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static long generateSnowflakeId() {
        // TODO: 实现雪花算法ID生成
        return System.currentTimeMillis();
    }
    
    /**
     * 生成唯一ID
     * @return 唯一ID
     */
    public long nextId() {
        return generateSnowflakeId();
    }
}