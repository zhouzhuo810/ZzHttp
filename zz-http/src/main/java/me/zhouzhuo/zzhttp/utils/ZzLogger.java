package me.zhouzhuo.zzhttp.utils;

import android.util.Log;

/**
 * Created by zz on 2016/8/29.
 */
public class ZzLogger {

    private static final String TAG = "ZzHttp_";

    private static boolean isPrint = false;

    public static void on() {
        isPrint = true;
    }

    public static void off() {
        isPrint = false;
    }

    public static void v(String msg) {
        if (isPrint) {
            StackTraceElement element = Thread.currentThread().getStackTrace()[3];
            String tag = TAG + element.getClassName();
            Log.v(tag, msg);
        }
    }

    public static void d(String msg) {
        if (isPrint) {
            StackTraceElement element = Thread.currentThread().getStackTrace()[3];
            String tag = TAG + element.getClassName();
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        if (isPrint) {
            StackTraceElement element = Thread.currentThread().getStackTrace()[3];
            String tag = TAG + element.getClassName();
            Log.i(tag, msg);
        }
    }

    public static void w(String msg) {
        if (isPrint) {
            StackTraceElement element = Thread.currentThread().getStackTrace()[3];
            String tag = TAG + element.getClassName();
            Log.w(tag, msg);
        }
    }

    public static void e(String msg) {
        if (isPrint) {
            StackTraceElement element = Thread.currentThread().getStackTrace()[3];
            String tag = TAG + element.getClassName();
            Log.e(tag, msg);
        }
    }
}
