# ZzHttp
A http framework for simply GET and POST.

**Gradle**

```
compile 'me.zhouzhuo.zzhttp:zz-http:1.0.0'
```

**Maven**

```
<dependency>
  <groupId>me.zhouzhuo.zzhttp</groupId>
  <artifactId>zz-http</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

<h2>How to use it ?

Don't forget adding perimssions.
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```


If you want to see log info in logcat.
```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ZzHttp.setDebug(true);

    }
}
```

If there if only one baseUrl.

```java
ZzHttp.getInstance().setBaseUrl("http://api.k780.com:88/")
```


If there is two or more baseUrl.

```java
public class Api {

    private static final String ENDPOINT_ONE = "https://www.baidu.com/";
    private static final String ENDPOINT_TWO = "http://api.k780.com:88/";

    private static ZzHttp apiOne;
    private static ZzHttp apiTwo;

    public static ZzHttp getBaiduApi() {
        if (apiOne == null) {
            synchronized (Api.class) {
                if (apiOne == null) {
                    apiOne = ZzHttp.newInstance().setBaseUrl(ENDPOINT_ONE);
                }
            }
        }
        return apiOne;
    }

    public static ZzHttp getWeatherApi() {
        if (apiTwo == null) {
            synchronized (Api.class) {
                if (apiTwo == null) {
                    apiTwo = ZzHttp.newInstance().setBaseUrl(ENDPOINT_TWO);
                }
            }
        }
        return apiTwo;
    }
}

```

<br/>
<h3>**GET** 

① return String

```java
Api.getWeatherApi()
                .get(new HttpParams()
                        .addStringParam("app", "weather.today")
                        .addStringParam("weaid", "1")
                        .addStringParam("appkey", "10003")
                        .addStringParam("sign", "b59bc3ef6191eb9f747dd4e83c99f2a4")
                        .addStringParam("format", "json"), String.class, new Callback.ZzCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        refreshLayout.setRefreshing(false);
                        tv.setText(result);
                    }

                    @Override
                    public void onFailure(String error) {
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
```

②return Object

```java
        Api.getWeatherApi()
                .get(new HttpParams()
                        .addStringParam("app", "weather.today")
                        .addStringParam("weaid", "1")
                        .addStringParam("appkey", "10003")
                        .addStringParam("sign", "b59bc3ef6191eb9f747dd4e83c99f2a4")
                        .addStringParam("format", "json"), WeatherEntity.class, new Callback.ZzCallback<WeatherEntity>() {
                    @Override
                    public void onSuccess(WeatherEntity result) {
                        refreshLayout.setRefreshing(false);
                        tv.setText(result.toString());
                    }

                    @Override
                    public void onFailure(String error) {
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
```
<br/>
<h3>**POST**

①return String

```
Api.getWeatherApi()
                .post(new HttpParams()
                        .addStringParam("app", "weather.today")
                        .addStringParam("weaid", "1")
                        .addStringParam("appkey", "10003")
                        .addStringParam("sign", "b59bc3ef6191eb9f747dd4e83c99f2a4")
                        .addStringParam("format", "json"), String.class, new Callback.ZzCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        refreshLayout.setRefreshing(false);
                        tv.setText(result);
                    }

                    @Override
                    public void onFailure(String error) {
                        refreshLayout.setRefreshing(false);
                    }
                });
```

②return Object

```
        Api.getWeatherApi()
                .post(new HttpParams()
                        .addStringParam("app", "weather.today")
                        .addStringParam("weaid", "1")
                        .addStringParam("appkey", "10003")
                        .addStringParam("sign", "b59bc3ef6191eb9f747dd4e83c99f2a4")
                        .addStringParam("format", "json"), WeatherEntity.class, new Callback.ZzCallback<WeatherEntity>() {
                    @Override
                    public void onSuccess(WeatherEntity result) {
                        refreshLayout.setRefreshing(false);
                        tv.setText(result.toString());
                    }

                    @Override
                    public void onFailure(String error) {
                        refreshLayout.setRefreshing(false);
                    }
                });
```
<br/>
<h3>**Download**

```
        final String fileName = new File(filePath1).getName();
        String url = "http://192.168.1.102/test/image/" + fileName;
        final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test";
        ZzHttp.getInstance().download(url, dir, new Callback.ProgressDownloadCallback() {
            @Override
            public void onProgress(float progress, int currentSize, int totalSize) {
                tv_download_result.setText(progress + ", " + currentSize + "," + totalSize);
            }

            @Override
            public void onSuccess(String result) {
                ivDown.setImageURI(Uri.fromFile(new File(dir + File.separator + fileName)));
            }

            @Override
            public void onFailure(String error) {

            }
        });

```
<br/>
<h3>**Upload（multipart/form-data）**

```
        ZzHttp.getInstance()
                .setBaseUrl("http://192.168.1.102/")
                .post("test/test.php", new HttpParams()
                        .setConnectTimeout(10000)
                        .setReadTimeout(10000)
                        .addBodyParam("file1", file1)
                        , UploadEntity.class, new Callback.ProgressUploadCallback<UploadEntity>() {

                    @Override
                    public void onProgress(float progress, int currentSize, int totalSize) {
                        tv_result.setText(progress + ", " + currentSize + "," + totalSize);
                    }

                    @Override
                    public void onSuccess(UploadEntity result) {
                        tv_result.setText(result.getCode() + "," + result.getData().getMsg());
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
```
