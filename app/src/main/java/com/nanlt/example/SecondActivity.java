package com.nanlt.example;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.nanlt.example.utils.ParamsSort;
import com.nanlt.example.utils.Upload;
import com.nanlt.hotfixtinker.FixDexUtils;
import com.nanlt.hotfixtinker.utils.Constants;
import com.nanlt.hotfixtinker.utils.FileUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SecondActivity extends BaseActivity {
    private String PATH = "http://yapi.demo.qunar.com/mock/18782/nanlt/text/getDexName";
    private String FILE_PATH = "http://192.168.137.1:8848/test/filepath/classes2.dex";
    private ProgressDialog progressBar;
    private String dexName=Constants.DEX_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getDexname();
    }

    public void show(View view) {
        new ParamsSort().math(this);
    }
    public void test(View view) {
        new ParamsSort().show(this,null);
    }
    //修复
    public void fix(View view) {
        //复制 下载的修复包 到私有目录
        //模拟网路下载到 sdCard 中

        File sourceFile =new File(Environment.getExternalStorageDirectory(), dexName);

        File targetFile =new File(getDir(Constants.DEX_DIR, Context.MODE_PRIVATE).getAbsolutePath()
                +File.separator+dexName);
//        //如果 私有目录 有存在 的修复包，说明是之前修复的
        if(targetFile.exists()){
            targetFile.delete();
            Toast.makeText(this, "删除之前的dex", Toast.LENGTH_SHORT).show();
        }

        try {
            FileUtils.copyFile(sourceFile,targetFile);
            Log.i("nanhai","---1---->copyFile");
            FixDexUtils.loadFixedDex(BaseApplication.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void download(View view) {
        if (progressBar == null) {
            progressBar = new ProgressDialog(this);
        }
        progressBar.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Upload.downLoadFromUrl(FILE_PATH,
                            dexName, Environment.getExternalStorageDirectory().getAbsolutePath(), new Upload.LoadListener() {
                                @Override
                                public void onStart(int contentLength) {
                                }

                                @Override
                                public void onLoadIng(int currentLength) {
                                    System.out.println("nanlt---onLoadIng---> currentLength-->" + currentLength);
                                }

                                @Override
                                public void onSuccess() {
                                    System.out.println("nanlt---onSuccess---> download success");
                                    if (progressBar != null)
                                        progressBar.dismiss();
                                }
                            });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    private void getDexname() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String resultStr = "";
                try {
                    URL url = new URL(PATH);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("GET");
                    connection.connect();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            resultStr += line;
                        }
                        JSONObject jsonObject = new JSONObject(resultStr);
                        dexName = jsonObject.optString("dexname");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void newVersion(View view) {
        Toast.makeText(this, "新版本", Toast.LENGTH_SHORT).show();
    }
}
