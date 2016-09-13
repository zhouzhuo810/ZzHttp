package me.zhouzhuo.zzhttpdemo.application;

import android.app.Application;

import me.zhouzhuo.zzhttp.ZzHttp;

/**
 * Created by zz on 2016/8/29.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ZzHttp.setDebug(true);

    }
}
