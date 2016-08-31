package me.zhouzhuo.zzhttpdemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import me.zhouzhuo.zzhttp.ZzHttp;
import me.zhouzhuo.zzhttp.callback.Callback;
import me.zhouzhuo.zzhttp.params.HttpParams;
import me.zhouzhuo.zzhttp.utils.Logger;
import me.zhouzhuo.zzhttpdemo.api.Api;
import me.zhouzhuo.zzhttpdemo.bean.UploadEntity;
import me.zhouzhuo.zzhttpdemo.bean.WeatherEntity;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private SwipeRefreshLayout refreshLayout;
    private String filePath1;
    private ImageView ivOne;
    private TextView tv_result;
    private TextView tv_download_result;
    private ImageView ivDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.rf);
        tv = (TextView) findViewById(R.id.tv_content_one);
        tv_result = (TextView) findViewById(R.id.tv_result);
        tv_download_result = (TextView) findViewById(R.id.tv_down_result);
        ivOne = (ImageView) findViewById(R.id.iv_one);
        ivDown = (ImageView) findViewById(R.id.iv_down);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromNetwork();
            }
        });

        getDataFromNetwork();

    }

    private void getDataFromNetwork() {
        //get string
/*        ZzHttp.getInstance()
                .setBaseUrl("http://api.k780.com:88/")
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
                });*/


        //post string
/*        ZzHttp.getInstance()
                .setBaseUrl("http://api.k780.com:88/")
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
                });*/

    }


    public void getEntity(View v) {
        //get entity
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
    }

    public void postEntity(View v) {
        //post entity
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
    }

    //choose a picture
    public void pickPhoto(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 0x01);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0x01) {
                getPathOne(data.getData());
            }
        }
    }

    //get the picture path
    private void getPathOne(Uri source) {
        ivOne.setImageURI(source);
        String[] projFour = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoaderFour = new CursorLoader(MainActivity.this, source, projFour, null, null, null);
        Cursor actualimagecursorFour = cursorLoaderFour.loadInBackground();
        int actual_image_column_index_four = actualimagecursorFour.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursorFour.moveToFirst();
        String img_path_four = actualimagecursorFour.getString(actual_image_column_index_four);
        actualimagecursorFour.close();
        filePath1 = img_path_four;
        Logger.e(filePath1);
    }


    //upload the picture
    public void doUpload(View v) {
        final File file1 = new File(filePath1);
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
    }

    //download the uploaded picture
    public void doDownload(View v) {
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
    }


}

