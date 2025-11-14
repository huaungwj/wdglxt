package com.example.dms.common;

import lombok.Data;

@Data
public class R<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> R<T> ok(T data){
        R<T> r = new R<>();
        r.code = 0; r.msg = "ok"; r.data = data; return r;
    }
    public static <T> R<T> ok(){ return ok(null); }
    public static <T> R<T> fail(String msg){
        R<T> r = new R<>(); r.code = -1; r.msg = msg; return r;
    }
}
