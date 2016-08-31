package me.zhouzhuo.zzhttp.params;

import java.io.File;
import java.util.HashMap;

/**
 * Created by zz on 2016/8/29.
 */
public class HttpParams {

    private int connectTimeout = 5000;
    private int readTimeout = 5000;
    HashMap<String, String> stringParams;
    HashMap<String, File> fileParams;

    public HttpParams() {
        this.stringParams = new HashMap<>();
        this.fileParams = new HashMap<>();
    }

    public HttpParams addStringParam(String key, String value) {
        stringParams.put(key, value);
        return this;
    }

    public HttpParams addBodyParam(String key, File body) {
        fileParams.put(key, body);
        return this;
    }

    public HashMap<String, String> getStringParams() {
        return stringParams;
    }

    public HashMap<String, File> getFileParams() {
        return fileParams;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public HttpParams setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public HttpParams setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }
}
