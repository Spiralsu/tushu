package com.shanzhu.book.utils;

import java.util.HashMap;

/**
 * 统一返回结果类
 * 修复：将 getListResultMap 等方法的返回类型从 HashMap 改为 R，解决类型转换报错
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 0);
        put("msg", "success");
    }

    // 获取状态码
    public Integer getCode() {
        return (Integer) this.get("code");
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok() {
        return new R();
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    // --- 修复以下兼容方法的返回类型为 R ---

    public static R getResultMap(Integer status, String message) {
        return error(status, message);
    }

    public static R getResultMap(Integer status, String message, Object data) {
        R r = new R();
        r.put("code", status);
        r.put("msg", message);
        r.put("data", data);
        return r;
    }

    // 修复：返回类型改为 R
    public static R getListResultMap(Integer status, String message, Integer count, Object data) {
        R r = new R();
        r.put("code", status);
        r.put("msg", message);
        r.put("count", count);
        r.put("data", data);
        return r;
    }
}