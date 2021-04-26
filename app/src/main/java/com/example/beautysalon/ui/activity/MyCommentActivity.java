package com.example.beautysalon.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.EvaluateDao;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityMyCommentBinding;
import com.example.beautysalon.ui.adapter.MyCommentAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyCommentActivity extends AppCompatActivity {

    private ActivityMyCommentBinding mBinding;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private UserDao mUserDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMyCommentBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
        requestForData();
    }

    public void initData() {
        mUserDao = (UserDao) getIntent().getExtras().getSerializable("user");
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }

    private void setListeners() {
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    requestForData();
                    //刷新完成
                    mBinding.swipeLayout.setRefreshing(false);
                    Toast.makeText(MyCommentActivity.this, "已更新", Toast.LENGTH_SHORT).show();
                }, 400);
            }
        });
    }

    public void requestForData() {
        NetClient.getNetClient().callNet(NetworkSettings.MY_COMMENT_DATA,
                RequestBody.create(JSON.toJSONString(mUserDao), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_USER_COMMENT_FAILED;
                        mHandler.post(()-> Utils.showMessage(MyCommentActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.REQUEST_USER_COMMENT_SUCCESS) {
                                    HashMap<String, Object> map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    List<ReserveDao> reserveDaoList = Utils.jsonToList(ReserveDao.class, JSON.parseObject(map.get("reserve").toString(), List.class));
                                    List<StylistDao> stylistDaoList = Utils.jsonToList(StylistDao.class, JSON.parseObject(map.get("stylist").toString(), List.class));
                                    List<EvaluateDao> evaluateDaoList = Utils.jsonToList(EvaluateDao.class, JSON.parseObject(map.get("comment").toString(), List.class));
                                    Collections.reverse(reserveDaoList);
                                    Collections.reverse(stylistDaoList);
                                    Collections.reverse(evaluateDaoList);
                                    mHandler.post(() -> {
                                        MyCommentAdapter adapter = new MyCommentAdapter(evaluateDaoList, stylistDaoList, reserveDaoList);
                                        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(MyCommentActivity.this));
                                        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view, null);
                                        adapter.setEmptyView(emptyView);
                                        mBinding.recyclerView.setAdapter(adapter);
                                    });
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}