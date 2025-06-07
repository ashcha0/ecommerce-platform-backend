package com.ecommerce.model.view;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSalesStatsView {
    private Long productId;
    private String productName;
    private String storeName;
    private Integer totalSales;
    private BigDecimal totalRevenue;
    private Integer orderCount;
    private Integer customerCount;
    private Integer stock;
    private Integer lowStockThreshold;

    public boolean isLowStock() {
        return stock <= lowStockThreshold;
    }

    public BigDecimal getAvgRevenuePerCustomer() {
        if (customerCount == null || customerCount == 0)
            return BigDecimal.ZERO;
        return totalRevenue.divide(BigDecimal.valueOf(customerCount), 2, BigDecimal.ROUND_HALF_UP);
    }
}