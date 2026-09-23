package com.nova.mall.utils;

import lombok.Data;
import java.io.Serializable;

/** 统一接口响应体 */
@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private int code;
    private String msg;
    private T data;

    public static <T> R<T> ok() { return ok(null); }
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>(); r.setCode(200); r.setMsg("success"); r.setData(data); return r;
    }
    public static <T> R<T> ok(T data, String msg) { R<T> r = ok(data); r.setMsg(msg); return r; }
    public static <T> R<T> fail(String msg) { return fail(500, msg); }
    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>(); r.setCode(code); r.setMsg(msg); return r;
    }
    public static <T> R<T> fail(int code, T data, String msg) {
        R<T> r = fail(code, msg); r.setData(data); return r;
    }
}
