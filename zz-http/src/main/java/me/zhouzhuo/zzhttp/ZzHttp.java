package me.zhouzhuo.zzhttp;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.zhouzhuo.zzhttp.callback.Callback;
import me.zhouzhuo.zzhttp.model.HttpManager;
import me.zhouzhuo.zzhttp.model.HttpMethod;
import me.zhouzhuo.zzhttp.params.HttpParams;
import me.zhouzhuo.zzhttp.utils.IOUtils;
import me.zhouzhuo.zzhttp.utils.ZzLogger;

/**
 * Created by zz on 2016/8/29.
 */
public class ZzHttp implements HttpManager {

    private static String BOUNDARY = "----------------7da3d81520810";

    private static ZzHttp http;

    private String baseUrl;

    private ZzHttp() {
    }

    public static ZzHttp getInstance() {
        if (http == null) {
            synchronized (ZzHttp.class) {
                if (http == null) {
                    http = new ZzHttp();
                }
            }
        }
        return http;
    }

    public static ZzHttp newInstance() {
        return new ZzHttp();
    }

    public ZzHttp setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public static void setDebug(boolean debug) {
        if (debug)
            ZzLogger.on();
        else
            ZzLogger.off();
    }

    @Override
    public <T> void get(String path, HttpParams params, Class<T> t, final Callback callback) {
        doGetWithPath(path, params, t, callback);
    }

    private <T> void doGetWithPath(final String path, HttpParams params, final Class<T> t, final Callback callback) {
        new AsyncTask<HttpParams, Void, ResultEntity>() {
            @Override
            protected ResultEntity doInBackground(HttpParams... params) {
                StringBuilder mPath = new StringBuilder();
                boolean hasParams = false;
                int readTimeout = 5000;
                int connectTimeout = 5000;
                if (params != null && params.length > 0 && params[0] != null) {
                    HashMap<String, String> map = params[0].getStringParams();
                    readTimeout = params[0].getReadTimeout();
                    connectTimeout = params[0].getConnectTimeout();
                    if (map != null && map.size() > 0) {
                        hasParams = true;
                        mPath.append("?");
                        for (String key : map.keySet()) {
                            String value = map.get(key);
                            mPath.append(key).append("=").append(value).append("&");
                        }
                    }
                }
                //if has param, remove the last of "&"
                if (hasParams) {
                    mPath.deleteCharAt(mPath.length() - 1);
                }
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(baseUrl == null ? "" : baseUrl + path + File.separator + mPath.toString());
                    ZzLogger.d(url.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(HttpMethod.GET.toString());
                    urlConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                    urlConnection.setRequestProperty("Accept-Language", "zh-cn");
                    urlConnection.setRequestProperty("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.0.2; Letv X500 Build/DBXCNOP5501304131S)");
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setReadTimeout(readTimeout);
                    urlConnection.setConnectTimeout(connectTimeout);
                    urlConnection.setInstanceFollowRedirects(true);
                    urlConnection.setUseCaches(true);
                    urlConnection.connect();
                    InputStream is = urlConnection.getInputStream();
                    return new ResultEntity(ResultEntity.TYPE_SUCCESS, IOUtils.inputStream2String(is));
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResultEntity(ResultEntity.TYPE_FAILURE, e.getMessage() + "," + e.getLocalizedMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute(ResultEntity entity) {
                super.onPostExecute(entity);
                switch (entity.getType()) {
                    case ResultEntity.TYPE_SUCCESS:
                        ZzLogger.d(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ZzCallback) {
                                if (t != null && !t.getName().equals(String.class.getName())) {
                                    Gson gson = new Gson();
                                    T x = gson.fromJson(entity.getResult(), t);
                                    ((Callback.ZzCallback) callback).onSuccess(x);
                                } else {
                                    ((Callback.ZzCallback) callback).onSuccess(entity.getResult());
                                }
                            }
                        }
                        break;
                    case ResultEntity.TYPE_FAILURE:
                        ZzLogger.e(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ZzCallback) {
                                ((Callback.ZzCallback) callback).onFailure(entity.getResult());
                            }
                        }
                        break;
                    case ResultEntity.TYPE_PROGRESS:
                        ZzLogger.d(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ProgressUploadCallback) {
                                ((Callback.ProgressUploadCallback) callback).onProgress(entity.getProgress(), entity.getCurrentSize(), entity.getTotalSize());
                            }
                        }
                        break;
                }

            }
        }.execute(params);
    }

    /**
     * do http get.
     *
     * @param params   http params
     * @param callback callback
     */
    @Override
    public <T> void get(HttpParams params, Class<T> t, Callback callback) {
        doGet(params, t, callback);
    }

    @Override
    public <T> void post(String path, HttpParams params, Class<T> t, Callback callback) {
        doPostWithPath(path, params, t, callback);
    }

    private <T> void doPostWithPath(final String path, HttpParams params, final Class<T> t, final Callback callback) {
        new AsyncTask<HttpParams, ResultEntity, ResultEntity>() {
            @Override
            protected ResultEntity doInBackground(HttpParams... params) {
                int readTimeout = 5000;
                int connectTimeout = 5000;

                HttpURLConnection urlConnection = null;
                OutputStream os = null;

                String mPath = "";
                if (params != null && params.length > 0 && params[0] != null) {
                    readTimeout = params[0].getReadTimeout();
                    connectTimeout = params[0].getConnectTimeout();
                    mPath = generatePath(path, params[0]);
                }

                try {
                    URL url = new URL(baseUrl == null ? "" : baseUrl + mPath);
                    ZzLogger.d(url.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(HttpMethod.POST.toString());
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setUseCaches(false);

                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setRequestProperty("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.0.2; Letv X500 Build/DBXCNOP5501304131S)");
                    urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    urlConnection.setReadTimeout(readTimeout);
                    urlConnection.setConnectTimeout(connectTimeout);
                    urlConnection.setInstanceFollowRedirects(true);

                    urlConnection.connect();

                    os = new DataOutputStream(urlConnection.getOutputStream());
                    final ResultEntity resultEntity = new ResultEntity(ResultEntity.TYPE_PROGRESS, null);
                    if (params != null && params.length > 0 && params[0] != null) {
                        writeOutStream(params[0], os, new OnProgressUpdateListener() {
                            @Override
                            public void progress(float progress, int currentSize, int totalSize) {
                                publishProgress(resultEntity.setProgress(progress).setCurrentSize(currentSize).setTotalSize(totalSize));
                            }
                        });
                    }

                    String inString = IOUtils.inputStream2String(urlConnection.getInputStream());

                    return new ResultEntity(ResultEntity.TYPE_SUCCESS, inString);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResultEntity(ResultEntity.TYPE_FAILURE, e.getMessage() + "," + e.getLocalizedMessage());
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }

            @Override
            protected void onProgressUpdate(ResultEntity... entity) {
                super.onProgressUpdate(entity);
                if (callback != null) {
                    if (callback instanceof Callback.ProgressUploadCallback) {
                        ((Callback.ProgressUploadCallback) callback).onProgress(entity[0].getProgress(), entity[0].getCurrentSize(), entity[0].getTotalSize());
                    }
                }
            }

            @Override
            protected void onPostExecute(ResultEntity entity) {
                super.onPostExecute(entity);
                switch (entity.getType()) {
                    case ResultEntity.TYPE_SUCCESS:
                        ZzLogger.d(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ZzCallback) {
                                if (t != null && !t.getName().equals(String.class.getName())) {
                                    Gson gson = new Gson();
                                    T x = gson.fromJson(entity.getResult(), t);
                                    ((Callback.ZzCallback) callback).onSuccess(x);
                                } else {
                                    ((Callback.ZzCallback) callback).onSuccess(entity.getResult());
                                }
                            }
                        }
                        break;
                    case ResultEntity.TYPE_FAILURE:
                        ZzLogger.e(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ZzCallback) {
                                ((Callback.ZzCallback) callback).onFailure(entity.getResult());
                            }
                        }
                        break;
                }
            }
        }.execute(params);
    }

    private <T> void doGet(HttpParams params, final Class<T> t, final Callback callback) {
        new AsyncTask<HttpParams, Void, ResultEntity>() {
            @Override
            protected ResultEntity doInBackground(HttpParams... params) {
                StringBuilder mPath = new StringBuilder();
                boolean hasParams = false;
                int readTimeout = 5000;
                int connectTimeout = 5000;
                if (params != null && params.length > 0 && params[0] != null) {
                    HashMap<String, String> map = params[0].getStringParams();
                    readTimeout = params[0].getReadTimeout();
                    connectTimeout = params[0].getConnectTimeout();
                    if (map != null && map.size() > 0) {
                        hasParams = true;
                        mPath.append("?");
                        for (String key : map.keySet()) {
                            String value = map.get(key);
                            mPath.append(key).append("=").append(value).append("&");
                        }
                    }
                }
                //if has param, remove the last of "&"
                if (hasParams) {
                    mPath.deleteCharAt(mPath.length() - 1);
                }
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(baseUrl + File.separator + mPath.toString());
                    ZzLogger.d(url.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(HttpMethod.GET.toString());
                    urlConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                    urlConnection.setRequestProperty("Accept-Language", "zh-cn");
                    urlConnection.setRequestProperty("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.0.2; Letv X500 Build/DBXCNOP5501304131S)");
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setReadTimeout(readTimeout);
                    urlConnection.setConnectTimeout(connectTimeout);
                    urlConnection.setInstanceFollowRedirects(true);
                    urlConnection.setUseCaches(true);
                    urlConnection.connect();
                    InputStream is = urlConnection.getInputStream();
                    return new ResultEntity(ResultEntity.TYPE_SUCCESS, IOUtils.inputStream2String(is));
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResultEntity(ResultEntity.TYPE_FAILURE, e.getMessage() + "," + e.getLocalizedMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute(ResultEntity entity) {
                super.onPostExecute(entity);
                switch (entity.getType()) {
                    case ResultEntity.TYPE_SUCCESS:
                        ZzLogger.d(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ZzCallback) {
                                if (t != null && !t.getName().equals(String.class.getName())) {
                                    Gson gson = new Gson();
                                    T x = gson.fromJson(entity.getResult(), t);
                                    ((Callback.ZzCallback) callback).onSuccess(x);
                                } else {
                                    ((Callback.ZzCallback) callback).onSuccess(entity.getResult());
                                }
                            }
                        }
                        break;
                    case ResultEntity.TYPE_FAILURE:
                        ZzLogger.e(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ZzCallback) {
                                ((Callback.ZzCallback) callback).onFailure(entity.getResult());
                            }
                        }
                        break;
                }

            }
        }.execute(params);
    }

    private class ResultEntity {
        private static final int TYPE_SUCCESS = 0x01;
        private static final int TYPE_PROGRESS = 0x02;
        private static final int TYPE_FAILURE = 0x03;

        private int type;
        private String result;
        private float progress;
        private int currentSize;
        private int totalSize;


        public ResultEntity(int type, String result) {
            this.type = type;
            this.result = result;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public float getProgress() {
            return progress;
        }

        public ResultEntity setProgress(float progress) {
            this.progress = progress;
            return this;
        }

        public int getCurrentSize() {
            return currentSize;
        }

        public ResultEntity setCurrentSize(int currentSize) {
            this.currentSize = currentSize;
            return this;
        }

        public int getTotalSize() {
            return totalSize;
        }

        public ResultEntity setTotalSize(int totalSize) {
            this.totalSize = totalSize;
            return this;
        }
    }


    @Override
    public <T> void post(HttpParams params, Class<T> t, Callback callback) {
        doPost(params, t, callback);
    }

    @Override
    public void download(String url, String dir, Callback callback) {
        startDownload(url, dir, callback);
    }

    private void startDownload(final String url, final String dir, final Callback callback) {
        new AsyncTask<Void, ResultEntity, ResultEntity>() {
            @Override
            protected ResultEntity doInBackground(Void... params) {
                int readTimeout = 5000;
                int connectTimeout = 0;
                String fileName = url.substring(url.lastIndexOf("/"), url.length());
                HttpURLConnection urlConnection = null;
                try {
                    URL mUrl = new URL(url);
                    ZzLogger.d(mUrl.toString());
                    urlConnection = (HttpURLConnection) mUrl.openConnection();
                    urlConnection.setRequestMethod(HttpMethod.GET.toString());
                    urlConnection.setRequestProperty("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.0.2; Letv X500 Build/DBXCNOP5501304131S)");
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setRequestProperty("Accept-Encoding", "identity");
                    urlConnection.setReadTimeout(readTimeout);
                    urlConnection.setConnectTimeout(connectTimeout);
                    urlConnection.connect();
                    int file_leng = urlConnection.getContentLength();
                    File file = new File(dir + File.separator + fileName);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    BufferedInputStream is = new BufferedInputStream(urlConnection.getInputStream());
                    FileOutputStream out = new FileOutputStream(file);
                    int currentSize = 0;
                    int temp = 0;
                    byte[] buf = new byte[1024];
                    final ResultEntity resultEntity = new ResultEntity(ResultEntity.TYPE_PROGRESS, null);
                    while ((temp = is.read(buf)) != -1) {
                        currentSize += temp;
                        out.write(buf, 0, temp);
                        publishProgress(resultEntity.setProgress(currentSize * 100.0f / file_leng).setCurrentSize(currentSize).setTotalSize(file_leng));
                    }
                    is.close();
                    out.flush();
                    out.close();
                    return new ResultEntity(ResultEntity.TYPE_SUCCESS, "success");
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResultEntity(ResultEntity.TYPE_FAILURE, e.getMessage() + "," + e.getLocalizedMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }

            @Override
            protected void onProgressUpdate(ResultEntity... entity) {
                super.onProgressUpdate(entity);
                if (callback != null) {
                    if (callback instanceof Callback.ProgressDownloadCallback) {
                        ((Callback.ProgressDownloadCallback) callback).onProgress(entity[0].getProgress(), entity[0].getCurrentSize(), entity[0].getTotalSize());
                    }
                }
            }

            @Override
            protected void onPostExecute(ResultEntity entity) {
                super.onPostExecute(entity);
                switch (entity.getType()) {
                    case ResultEntity.TYPE_SUCCESS:
                        ZzLogger.d(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ZzCallback) {
                                ((Callback.ZzCallback) callback).onSuccess(entity.getResult());
                            }
                        }
                        break;
                    case ResultEntity.TYPE_FAILURE:
                        ZzLogger.e(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ZzCallback) {
                                ((Callback.ZzCallback) callback).onFailure(entity.getResult());
                            }
                        }
                        break;
                }

            }
        }.execute();
    }

    private <T> void doPost(HttpParams params, final Class<T> t, final Callback callback) {
        new AsyncTask<HttpParams, ResultEntity, ResultEntity>() {
            @Override
            protected ResultEntity doInBackground(HttpParams... params) {

                int readTimeout = 5000;
                int connectTimeout = 5000;

                HttpURLConnection urlConnection = null;
                OutputStream os = null;

                String mPath = "";
                if (params != null && params.length > 0 && params[0] != null) {
                    readTimeout = params[0].getReadTimeout();
                    connectTimeout = params[0].getConnectTimeout();
                    mPath = generatePath(null, params[0]);
                }

                try {
                    URL url = new URL(baseUrl + mPath);
                    ZzLogger.d(url.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(HttpMethod.POST.toString());
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setUseCaches(false);

                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setRequestProperty("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.0.2; Letv X500 Build/DBXCNOP5501304131S)");
                    urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    urlConnection.setReadTimeout(readTimeout);
                    urlConnection.setConnectTimeout(connectTimeout);
                    urlConnection.setInstanceFollowRedirects(true);

                    urlConnection.connect();

                    os = new DataOutputStream(urlConnection.getOutputStream());
                    final ResultEntity resultEntity = new ResultEntity(ResultEntity.TYPE_PROGRESS, null);
                    if (params != null && params.length > 0 && params[0] != null) {
                        writeOutStream(params[0], os, new OnProgressUpdateListener() {
                            @Override
                            public void progress(float progress, int currentSize, int totalSize) {
                                publishProgress(resultEntity.setProgress(progress).setCurrentSize(currentSize).setTotalSize(totalSize));
                            }
                        });
                    }

                    String inString = IOUtils.inputStream2String(urlConnection.getInputStream());

                    return new ResultEntity(ResultEntity.TYPE_SUCCESS, inString);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResultEntity(ResultEntity.TYPE_FAILURE, e.getMessage() + "," + e.getLocalizedMessage());
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }

            @Override
            protected void onProgressUpdate(ResultEntity... entity) {
                super.onProgressUpdate(entity);
                if (callback != null) {
                    if (callback instanceof Callback.ProgressUploadCallback) {
                        ((Callback.ProgressUploadCallback) callback).onProgress(entity[0].getProgress(), entity[0].getCurrentSize(), entity[0].getTotalSize());
                    }
                }
            }

            @Override
            protected void onPostExecute(ResultEntity entity) {
                super.onPostExecute(entity);
                switch (entity.getType()) {
                    case ResultEntity.TYPE_SUCCESS:
                        ZzLogger.d(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ZzCallback) {
                                if (t != null && !t.getName().equals(String.class.getName())) {
                                    Gson gson = new Gson();
                                    T x = gson.fromJson(entity.getResult(), t);
                                    ((Callback.ZzCallback) callback).onSuccess(x);
                                } else {
                                    ((Callback.ZzCallback) callback).onSuccess(entity.getResult());
                                }
                            }
                        }
                        break;
                    case ResultEntity.TYPE_FAILURE:
                        ZzLogger.e(entity.getResult());
                        if (callback != null) {
                            if (callback instanceof Callback.ZzCallback) {
                                ((Callback.ZzCallback) callback).onFailure(entity.getResult());
                            }
                        }
                        break;
                }
            }
        }.execute(params);
    }

    private String generatePath(String path, HttpParams params) {
        StringBuilder mPath = new StringBuilder();
        if (path != null) {
            mPath.append(path);
        }
        boolean hasParams = false;
        HashMap<String, String> map = params.getStringParams();
        if (map != null && map.size() > 0) {
            hasParams = true;
            mPath.append("?");
            for (String key : map.keySet()) {
                String value = map.get(key);
                mPath.append(key).append("=").append(value).append("&");
            }
        }
        //if has param, remove the last of "&"
        if (hasParams) {
            mPath.deleteCharAt(mPath.length() - 1);
        }
        return mPath.toString();
    }


    private void writeOutStream(HttpParams params, OutputStream os, OnProgressUpdateListener listener) throws IOException {
        StringBuilder pm = new StringBuilder();
        List<String> filePaths = new ArrayList<String>();
        List<String> keys = new ArrayList<>();
        boolean hasStringParams = false;
        if (params != null) {
            HashMap<String, String> map = params.getStringParams();
            if (map != null && map.size() > 0) {
                for (String key : map.keySet()) {
                    hasStringParams = true;
                    String value = map.get(key);
                    pm.append(key).append("=").append(value).append("&");
                }
            }
            HashMap<String, File> fileParams = params.getFileParams();
            if (fileParams != null && fileParams.size() > 0) {
                for (String key : fileParams.keySet()) {
                    File file = fileParams.get(key);
                    if (file != null && file.exists()) {
                        filePaths.add(file.getAbsolutePath());
                        keys.add(key);
                    }
                }
            }
        }
        //if has param, remove the last of "&"
        if (hasStringParams) {
            pm.deleteCharAt(pm.length() - 1);
        }
        byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
        int leng = filePaths.size();
        int count = 0;
        int current = 0;
        for (int i = 0; i < leng; i++) {
            String filePath = filePaths.get(i);
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    count += file.length();
                }
            }
        }
        for (int i = 0; i < leng; i++) {
            String filePath = filePaths.get(i);
            File file = new File(filePath);
            String sb = "--" + BOUNDARY + "\r\n" +
                    "Content-Disposition: form-data; name=\"" + keys.get(i) + "\";" + "filename=\"" + file.getName() + "\"\r\n" +
                    "Content-Type: " + getContentType(file.getName()) + "\r\n\r\n";
            byte[] data = sb.getBytes();

            os.write(data);

            DataInputStream is = new DataInputStream(new FileInputStream(file));

            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = is.read(bufferOut)) != -1) {
                os.write(bufferOut, 0, bytes);
                current += bytes;
                if (listener != null) {
                    listener.progress(current * 100.0f / count, current, count);
                }
            }
            is.close();
            os.write("\r\n".getBytes());
        }
        os.write(end_data);
        os.flush();
        os.close();
    }

    private String getContentType(String filename) {
        String contentType = HttpURLConnection.guessContentTypeFromName(filename);
        if (TextUtils.isEmpty(contentType)) {
            contentType = "application/octet-stream";
        } else {
            contentType = contentType.replaceFirst("\\/jpg$", "/jpeg");
        }
        return contentType;
    }

    interface OnProgressUpdateListener {
        void progress(float progress, int currentSize, int totalSize);
    }

}
