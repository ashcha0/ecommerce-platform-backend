package com.ecommerce.model.vo;

import java.util.List;

public class PageResult<T> {
    private int pageNum; // 当前页码
    private int pageSize; // 每页记录数
    private long total; // 总记录数
    private int pages; // 总页数
    private List<T> list; // 当前页数据列表

    public PageResult() {
    }

    public PageResult(List<T> list) {
        this.list = list;
    }

    public PageResult(int pageNum, int pageSize, long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = (int) Math.ceil((double) total / pageSize);
        this.list = list;
    }

    // 静态创建方法
    public static <T> PageResult<T> success(int pageNum, int pageSize, long total, List<T> list) {
        return new PageResult<>(pageNum, pageSize, total, list);
    }

    // 静态创建方法（使用PageHelper插件时的转换方法）
    public static <T> PageResult<T> success(com.github.pagehelper.Page<T> page) {
        return new PageResult<>(page.getPageNum(), page.getPageSize(), page.getTotal(), page.getResult());
    }

    // Getters and Setters
    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}