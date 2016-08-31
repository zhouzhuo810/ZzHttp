package me.zhouzhuo.zzhttp.model;

import me.zhouzhuo.zzhttp.callback.Callback;
import me.zhouzhuo.zzhttp.params.HttpParams;

/**
 * Created by zz on 2016/8/29.
 */
public interface HttpManager {

    <T> void get(String path, HttpParams params, Class<T> t, Callback callback);

    <T> void get(HttpParams params, Class<T> t, Callback callback);

    <T> void post(String path, HttpParams params, Class<T> t,  Callback callback);

    <T> void post(HttpParams params, Class<T> t, Callback callback);

     void download(String url, String dir, Callback callback);

}
