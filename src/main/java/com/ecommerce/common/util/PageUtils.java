package com.ecommerce.common.util;

public class PageUtils {

    public static void validatePageParams(int pageNum, int pageSize) {
        if (pageNum < 1) {
            throw new IllegalArgumentException("页码不能小于1");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("每页大小必须在1-100之间");
        }
    }
}