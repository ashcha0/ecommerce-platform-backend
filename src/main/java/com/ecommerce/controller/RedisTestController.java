package com.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import com.ecommerce.common.result.Result;

@RestController
@RequestMapping("/test")
public class RedisTestController {

    private static final Logger log = LoggerFactory.getLogger(RedisTestController.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/redis/connect")
    public Result<?> testConnection() {
        try {
            log.info("开始Redis连接测试");
            String key = "test:connection";
            String value = "redis-test-" + System.currentTimeMillis();

            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            log.debug("尝试设置Redis键值对: key={}, value={}", key, value);
            ops.set(key, value, Duration.ofSeconds(30));

            String storedValue = ops.get(key);
            log.debug("从Redis获取的值: {}", storedValue);
            if (!value.equals(storedValue)) {
                log.error("Redis值验证失败: 期望值={}, 实际值={}", value, storedValue);
                return Result.fail(500, "Redis值验证失败: 期望值=" + value + ", 实际值=" + storedValue);
            }

            log.info("Redis连接测试成功");
            return Result.success("Redis连接测试成功!");
        } catch (RedisConnectionFailureException e) {
            log.error("Redis连接失败", e);
            return Result.fail(500, "Redis连接失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("Redis测试过程中发生未知异常", e);
            throw e; // 抛出异常让全局异常处理器处理
        }
    }
}