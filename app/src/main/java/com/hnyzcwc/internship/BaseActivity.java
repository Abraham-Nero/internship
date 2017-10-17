package com.hnyzcwc.internship;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by hnyzCWChen@63.com on 2017/10/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    static{
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
    private static AlertDialog dialog = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initEvents();
        initDatas();

    }
    protected abstract void initViews();
    protected abstract void initEvents();
    protected abstract void initDatas();
    protected void get(String url, final CallBack callBack){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 321);
                return ;
            }
        }
        OkHttpUtils
            .get()
            .url(url)
            .addHeader("Authorization","Bearer a62b88ea291c0d0e5b9295fdb8930936f945027bb84ff747ef6b89f8a9cd4da1")
            .build()
            .execute(new StringCallback()
            {
                @Override
                public void onError(Call call, Exception e, int id) {
                    callBack.onError(e.getMessage());
                }
                @Override
                public void onResponse(String response, int id) {
                    callBack.onSuccess(response);
                }
            });

    }

    /**
     * get Data from assets , just for test
     * @param callBack
     */
    protected void get(CallBack callBack){
        Scanner in = null;
        try {
            in = new Scanner(getAssets().open("data.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(null!=in){
            StringBuffer buffer = new StringBuffer();
            while(in.hasNextLine()){
                buffer.append(in.nextLine());
            }
            callBack.onSuccess(buffer.toString());
        }
    }
    protected void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showTip(String msg){
        synchronized ("DIALOG_LOCK"){
            if(null == dialog){
                dialog = new AlertDialog.Builder(this)
                        .setTitle("系统提示")
                        .setMessage(msg)
                        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }else{
                dialog.setMessage(msg);
                if(!dialog.isShowing()){
                    dialog.show();
                }
            }
        }
    }

    private long mTimeStamp=0L;
    protected void back(){
        if(null != dialog && dialog.isShowing()){
            dialog.dismiss();
            return;
        }
        long now = System.currentTimeMillis();
        if(now - mTimeStamp < 1500){
            finish();
        }else{
            toast("再按一次退出系统！");
        }
        mTimeStamp = now;
    }
    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        back();
        return true;
    }
}
