package com.example.beautysalon.ui.activity;

import android.graphics.Color;
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
import com.example.beautysalon.dao.CouponDistributionDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityCouponRecieveBinding;
import com.example.beautysalon.ui.adapter.CouponReceiveAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CouponReceiveActivity extends AppCompatActivity {

    private ActivityCouponRecieveBinding mBinding;
    private UserDao mUser;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private HashMap<String, Object> mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCouponRecieveBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
    }
    public void initData() {
        mBinding.swipeLayout.setColorSchemeColors(Color.parseColor("#5872fd"));
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUser = (UserDao) getIntent().getExtras().getSerializable("user");
        requestForData();
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }
    public void setListeners() {
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    requestForData();
                    //刷新完成
                    mBinding.swipeLayout.setRefreshing(false);
                    Toast.makeText(CouponReceiveActivity.this, "已更新", Toast.LENGTH_SHORT).show();
                }, 400);
            }
        });
//        mBinding.recyclerView.setLoadingMoreEnabled(true);
//        mBinding.recyclerView.setLoadingMoreProgressStyle(ProgressStyle.LineScale);
//        mBinding.recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(() -> {
//                    requestForData();
//                    mBinding.recyclerView.refreshComplete();
//                },400);
//            }
//
//            @Override
//            public void onLoadMore() {
//                mBinding.recyclerView.loadMoreComplete();
//            }
//        });
    }

    public void requestForData() {
        NetClient.getNetClient().callNet(NetworkSettings.COUPON_DISTRIBUTION_DATA,
                RequestBody.create(JSON.toJSONString(mUser), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_COUPON_DISTRIBUTION_DATA_FAILED;
                        mHandler.post(()-> Utils.showMessage(CouponReceiveActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.REQUEST_COUPON_DISTRIBUTION_DATA_SUCCESS) {
                                    mMap = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    List<Integer> userCoupon = JSON.parseObject(mMap.get("userCoupon").toString(), List.class);
                                    List<CouponDistributionDao> coupon = Utils.jsonToList(CouponDistributionDao.class, (List) mMap.get("coupon"));
                                    mHandler.post(() -> {
                                        CouponReceiveAdapter adapter = new CouponReceiveAdapter(CouponReceiveActivity.this, mUser, coupon, userCoupon);
                                        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(CouponReceiveActivity.this));
                                        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view, null);
                                        adapter.setEmptyView(emptyView);
                                        mBinding.recyclerView.setAdapter(adapter);
                                    });
                                }
                            } else {
                                System.out.println("失败了啊");
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