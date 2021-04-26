package com.example.beautysalon.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.PointsDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityMyPointsBinding;
import com.example.beautysalon.ui.adapter.MyPointsAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.IOException;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyPointsActivity extends AppCompatActivity {

    private List<PointsDao> mPointsDaoList;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private ActivityMyPointsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMyPointsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
        requestForData();
    }

    public void initData() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }

    public void setListeners() {
        mBinding.recyclerView.setLoadingMoreEnabled(true);
        mBinding.recyclerView.setLoadingMoreProgressStyle(ProgressStyle.LineScale);
        mBinding.recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    requestForData();
                    mBinding.recyclerView.refreshComplete();
                },400);
            }
            @Override
            public void onLoadMore() {
                mBinding.recyclerView.loadMoreComplete();
            }
        });
    }

    public void requestForData() {
        UserDao userDao = (UserDao) getIntent().getExtras().getSerializable("user");
        NetClient.getNetClient().callNet(NetworkSettings.POINTS_DATA,
                RequestBody.create(JSON.toJSONString(userDao.getUserId()), Utils.MEDIA_TYPE),
                new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_POINTS_DATA_FAILED;
                        mHandler.post(()-> Utils.showMessage(MyPointsActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.REQUEST_POINTS_DATA_SUCCESS) {
                                    mPointsDaoList = Utils.jsonToList(PointsDao.class, JSON.parseObject(restResponse.getData().toString(), List.class));
                                    mHandler.post(() -> {
                                        MyPointsAdapter adapter = new MyPointsAdapter(MyPointsActivity.this, mPointsDaoList);
                                        XRecyclerView.LayoutManager manager = new LinearLayoutManager(MyPointsActivity.this);
                                        mBinding.recyclerView.setLayoutManager(manager);
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