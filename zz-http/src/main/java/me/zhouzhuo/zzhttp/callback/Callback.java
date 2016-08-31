package me.zhouzhuo.zzhttp.callback;

/**
 * Created by zz on 2016/8/29.
 */
public interface Callback {

    interface ZzCallback<T> extends Callback {

        void onSuccess(T result);

        void onFailure(String error);
    }

    interface ProgressUploadCallback<T> extends ZzCallback<T> {
        void onProgress(float progress, int currentSize, int totalSize);
    }

    interface ProgressDownloadCallback extends ZzCallback<String> {
        void onProgress(float progress, int currentSize, int totalSize);
    }
}
