package com.hnyzcwc.internship;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private static volatile int page = 1;
    private static final int PER_PAGE = 30;
    private static final String URL="https://api.dribbble.com/v1/shots?sort=recent&page=%d&per_page=%d";//"https://kyfw.12306.cn/otn/";
    private RecyclerView mList;
    private Button mLast;
    private Button mNext;
    private Button mFlesh;
    private DataCallBack mCallBack=null;
    private ActionBar mActionbar;
    private RelativeLayout mProgress;

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_main);
        mList = (RecyclerView) findViewById(R.id.rv_list);
        mList.setLayoutManager(new GridLayoutManager(this,3));

        mLast = (Button) findViewById(R.id.btn_last);
        mFlesh = (Button) findViewById(R.id.btn_flesh);
        mNext = (Button) findViewById(R.id.btn_next);

        mActionbar = getSupportActionBar();
        if(null!=mActionbar){
            mActionbar.setDisplayHomeAsUpEnabled(true);
        }

        mProgress = (RelativeLayout) findViewById(R.id.rl_progress);

    }

    @Override
    protected void initEvents() {
        mLast.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mFlesh.setOnClickListener(this);

    }

    @Override
    protected void initDatas() {
        MainAdapter adapter = new MainAdapter(this, new ArrayList<String>());
        mList.setAdapter(adapter);
        initCallBack(adapter);
        flesh();
        ImageView imageView = (ImageView) findViewById(R.id.iv_progress);
        Glide.with(this).load(R.drawable.loading).asGif().into(imageView);
    }
    private void initCallBack(MainAdapter adapter){
        mCallBack = new DataCallBack(adapter);
    }
    private void protectedBtn(boolean status){
        mLast.setEnabled(status);
        mNext.setEnabled(status);
        mFlesh.setEnabled(status);
        if(false == status){
            mProgress.setVisibility(View.VISIBLE);
        }else{
            mProgress.setVisibility(View.GONE);
        }
    }
    private void flesh(){
        protectedBtn(false);
        get(String.format(URL,page,PER_PAGE),mCallBack);
    }
    private void lastPage(){
        if(page==1){
            toast("已经到第1页了");
            mLast.setVisibility(View.INVISIBLE);
            return ;
        }
        mNext.setVisibility(View.VISIBLE);
        page--;
        flesh();
    }
    private void nextPage(){
        if(0 == page){
            toast("没有更多数据了");
            mNext.setVisibility(View.INVISIBLE);
            return ;
        }
        mLast.setVisibility(View.VISIBLE);
        page++;
        flesh();
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_last:
                lastPage();
                break;
            case R.id.btn_next:
                nextPage();
                break;
            case R.id.btn_flesh:
                flesh();
            default:
                break;
        }
    }

    /**
     * 网络数据访问回调
     */
    private class DataCallBack implements CallBack{
        private MainAdapter mAdapter;
        public DataCallBack(MainAdapter adapter){
            mAdapter = adapter;
        }
        @Override
        public void onSuccess(String response) {
            JSONArray json = null;
            try {
                json = new JSONArray(response);
                JSONObject obj;
                final List<String> list = new ArrayList<>();
                if(json.length()>0){
                    for(int i=0;i<json.length();i++){
                        obj = json.getJSONObject(i);
                        obj = obj.getJSONObject("images");
                        list.add(obj.getString("teaser"));
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(null!=mActionbar){
                                mActionbar.setTitle(getString(R.string.app_name)+"：第"+MainActivity.page+"页");
                            }

                            List<String>datas;
                            if(null != (datas=mAdapter.getDataList())){
                                for(int i=0;i<list.size();i++){
                                    if(i>=datas.size()){
                                        mAdapter.addDataItem(list.get(i));
                                    }else{
                                        mAdapter.setDataItem(list.get(i),i);
                                    }
                                }
                            }else {
                                mAdapter.setDataList(list);
                            }

                        }
                    });
                }else{
                    MainActivity.page=0;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                protectedBtn(true);
            }

        }

        @Override
        public void onError(String mes) {
            MainActivity.this.showTip("获取服务器信息失败：\n"+mes);
            protectedBtn(true);
        }
    }
}
