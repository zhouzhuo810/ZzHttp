package me.zhouzhuo.zzhttpdemo.api;

import me.zhouzhuo.zzhttp.ZzHttp;

/**
 * Created by ZZ on 2016/8/31.
 */
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
