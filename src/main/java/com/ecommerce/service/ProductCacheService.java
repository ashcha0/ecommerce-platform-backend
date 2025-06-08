package com.ecommerce.service;

import com.ecommerce.model.entity.Product;
import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.common.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Set;

/**
 * 商品缓存服务
 * 提供商品相关的缓存操作
 */
@Service
@Slf4j
public class ProductCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 缓存键前缀
    private static final String PRODUCT_DETAIL_PREFIX = "product:detail:";
    private static final String PRODUCT_SEARCH_PREFIX = "product:search:";
    private static final String PRODUCT_HOT_PREFIX = "product:hot";
    private static final String PRODUCT_LOW_STOCK_PREFIX = "product:low_stock";

    // 缓存过期时间（分钟）
    private static final long DETAIL_CACHE_EXPIRE = 30;
    private static final long SEARCH_CACHE_EXPIRE = 10;
    private static final long HOT_CACHE_EXPIRE = 60;
    private static final long LOW_STOCK_CACHE_EXPIRE = 5;

    /**
     * 缓存商品详情
     */
    public void cacheProductDetail(Long productId, Product product) {
        try {
            String key = PRODUCT_DETAIL_PREFIX + productId;
            redisTemplate.opsForValue().set(key, product, DETAIL_CACHE_EXPIRE, TimeUnit.MINUTES);
            log.debug("缓存商品详情成功，商品ID: {}", productId);
        } catch (Exception e) {
            log.error("缓存商品详情失败，商品ID: {}", productId, e);
        }
    }

    /**
     * 获取缓存的商品详情
     */
    public Product getCachedProductDetail(Long productId) {
        try {
            String key = PRODUCT_DETAIL_PREFIX + productId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof Product) {
                log.debug("命中商品详情缓存，商品ID: {}", productId);
                return (Product) cached;
            }
        } catch (Exception e) {
            log.error("获取缓存商品详情失败，商品ID: {}", productId, e);
        }
        return null;
    }

    /**
     * 缓存商品搜索结果
     */
    public void cacheProductSearch(ProductQueryDTO queryDTO, PageResult<Product> result) {
        try {
            String key = generateSearchKey(queryDTO);
            redisTemplate.opsForValue().set(key, result, SEARCH_CACHE_EXPIRE, TimeUnit.MINUTES);
            log.debug("缓存商品搜索结果成功，查询条件: {}", queryDTO);
        } catch (Exception e) {
            log.error("缓存商品搜索结果失败，查询条件: {}", queryDTO, e);
        }
    }

    /**
     * 获取缓存的商品搜索结果
     */
    @SuppressWarnings("unchecked")
    public PageResult<Product> getCachedProductSearch(ProductQueryDTO queryDTO) {
        try {
            String key = generateSearchKey(queryDTO);
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof PageResult) {
                log.debug("命中商品搜索缓存，查询条件: {}", queryDTO);
                return (PageResult<Product>) cached;
            }
        } catch (Exception e) {
            log.error("获取缓存商品搜索结果失败，查询条件: {}", queryDTO, e);
        }
        return null;
    }

    /**
     * 缓存热门商品列表
     */
    public void cacheHotProducts(List<Product> products) {
        try {
            redisTemplate.opsForValue().set(PRODUCT_HOT_PREFIX, products, HOT_CACHE_EXPIRE, TimeUnit.MINUTES);
            log.debug("缓存热门商品列表成功，商品数量: {}", products.size());
        } catch (Exception e) {
            log.error("缓存热门商品列表失败", e);
        }
    }

    /**
     * 获取缓存的热门商品列表
     */
    @SuppressWarnings("unchecked")
    public List<Product> getCachedHotProducts() {
        try {
            Object cached = redisTemplate.opsForValue().get(PRODUCT_HOT_PREFIX);
            if (cached instanceof List) {
                log.debug("命中热门商品缓存");
                return (List<Product>) cached;
            }
        } catch (Exception e) {
            log.error("获取缓存热门商品列表失败", e);
        }
        return null;
    }

    /**
     * 缓存低库存商品列表
     */
    public void cacheLowStockProducts(List<Product> products) {
        try {
            redisTemplate.opsForValue().set(PRODUCT_LOW_STOCK_PREFIX, products, LOW_STOCK_CACHE_EXPIRE, TimeUnit.MINUTES);
            log.debug("缓存低库存商品列表成功，商品数量: {}", products.size());
        } catch (Exception e) {
            log.error("缓存低库存商品列表失败", e);
        }
    }

    /**
     * 获取缓存的低库存商品列表
     */
    @SuppressWarnings("unchecked")
    public List<Product> getCachedLowStockProducts() {
        try {
            Object cached = redisTemplate.opsForValue().get(PRODUCT_LOW_STOCK_PREFIX);
            if (cached instanceof List) {
                log.debug("命中低库存商品缓存");
                return (List<Product>) cached;
            }
        } catch (Exception e) {
            log.error("获取缓存低库存商品列表失败", e);
        }
        return null;
    }

    /**
     * 删除商品相关缓存
     */
    public void evictProductCache(Long productId) {
        try {
            // 删除商品详情缓存
            String detailKey = PRODUCT_DETAIL_PREFIX + productId;
            redisTemplate.delete(detailKey);

            // 删除搜索结果缓存（模糊匹配）
            Set<String> searchKeys = redisTemplate.keys(PRODUCT_SEARCH_PREFIX + "*");
            if (searchKeys != null && !searchKeys.isEmpty()) {
                redisTemplate.delete(searchKeys);
            }

            // 删除热门商品缓存
            redisTemplate.delete(PRODUCT_HOT_PREFIX);

            // 删除低库存商品缓存
            redisTemplate.delete(PRODUCT_LOW_STOCK_PREFIX);

            log.debug("删除商品相关缓存成功，商品ID: {}", productId);
        } catch (Exception e) {
            log.error("删除商品相关缓存失败，商品ID: {}", productId, e);
        }
    }

    /**
     * 清空所有商品缓存
     */
    public void evictAllProductCache() {
        try {
            // 删除所有商品详情缓存
            Set<String> detailKeys = redisTemplate.keys(PRODUCT_DETAIL_PREFIX + "*");
            if (detailKeys != null && !detailKeys.isEmpty()) {
                redisTemplate.delete(detailKeys);
            }

            // 删除所有搜索结果缓存
            Set<String> searchKeys = redisTemplate.keys(PRODUCT_SEARCH_PREFIX + "*");
            if (searchKeys != null && !searchKeys.isEmpty()) {
                redisTemplate.delete(searchKeys);
            }

            // 删除热门商品缓存
            redisTemplate.delete(PRODUCT_HOT_PREFIX);

            // 删除低库存商品缓存
            redisTemplate.delete(PRODUCT_LOW_STOCK_PREFIX);

            log.info("清空所有商品缓存成功");
        } catch (Exception e) {
            log.error("清空所有商品缓存失败", e);
        }
    }

    /**
     * 生成搜索缓存键
     */
    private String generateSearchKey(ProductQueryDTO queryDTO) {
        StringBuilder keyBuilder = new StringBuilder(PRODUCT_SEARCH_PREFIX);
        
        if (queryDTO.getName() != null) {
            keyBuilder.append("name:").append(queryDTO.getName()).append(":");
        }
        // if (queryDTO.getCategoryId() != null) {
        //     keyBuilder.append("category:").append(queryDTO.getCategoryId()).append(":");
        // }
        if (queryDTO.getStatus() != null) {
            keyBuilder.append("status:").append(queryDTO.getStatus()).append(":");
        }
        if (queryDTO.getMinPrice() != null) {
            keyBuilder.append("minPrice:").append(queryDTO.getMinPrice()).append(":");
        }
        if (queryDTO.getMaxPrice() != null) {
            keyBuilder.append("maxPrice:").append(queryDTO.getMaxPrice()).append(":");
        }
        keyBuilder.append("page:").append(queryDTO.getPageNum()).append(":");
        keyBuilder.append("size:").append(queryDTO.getPageSize());
        
        return keyBuilder.toString();
    }
}