package com.ecommerce.common.result;

import com.github.pagehelper.PageInfo;
import lombok.Data;

@Data
public class PageResult<T> {
    private int code;
    private String message;
    private PageInfo<T> data;

    public static <T> PageResult<T> success(PageInfo<T> data) {
        PageResult<T> result = new PageResult<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> PageResult<T> error(int code, String message) {
        PageResult<T> result = new PageResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}