package com.shanzhu.book.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果类
 * 修复：补全缺失的 error() 无参方法
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 0);
        put("msg", "success");
    }

    // --- 核心修复点：添加无参 error 方法 ---
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

    // --- 兼容旧代码的方法 ---
    public static HashMap<String, Object> getResultMap(Integer status, String message) {
        return error(status, message);
    }

    public static HashMap<String, Object> getResultMap(Integer status, String message, Object data) {
        R r = new R();
        r.put("code", status);
        r.put("msg", message);
        r.put("data", data);
        return r;
    }

    public static HashMap<String, Object> getListResultMap(Integer status, String message, Integer count, Object data) {
        R r = new R();
        r.put("code", status);
        r.put("msg", message);
        r.put("count", count);
        r.put("data", data);
        return r;
    }
}