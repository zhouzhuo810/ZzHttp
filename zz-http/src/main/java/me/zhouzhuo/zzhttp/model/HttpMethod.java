package me.zhouzhuo.zzhttp.model;

/**
 * Created by zz on 2016/8/29.
 */
public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
