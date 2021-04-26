package com.example.beautysalon.ui.activity;

import android.content.Intent;
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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.BarbershopDao;
import com.example.beautysalon.dao.CouponDao;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityUnpaidBinding;
import com.example.beautysalon.ui.adapter.UnpaidReservationAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UnpaidActivity extends AppCompatActivity {

    private ActivityUnpaidBinding mBinding;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private List<ReserveDao> mReserveDaoList;
    private HashMap<String, Object> mMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityUnpaidBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
    }

    private void initData() {
        mBinding.swipeLayout.setColorSchemeColors(Color.parseColor("#5872fd"));
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
        requestForData();
    }
    private void setListeners() {
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    requestForData();
                    //刷新完成
                    mBinding.swipeLayout.setRefreshing(false);
                    Toast.makeText(UnpaidActivity.this, "已更新", Toast.LENGTH_SHORT).show();
                }, 400);
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

    public void requestForData() {
        UserDao userDao = (UserDao) getIntent().getExtras().getSerializable("user");
        NetClient.getNetClient().callNet(NetworkSettings.RESERVATION_DATA, "type", "unpaid",
                RequestBody.create(JSON.toJSONString(userDao), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_RESERVATION_DATA_FAILED;
                        mHandler.post(()-> Utils.showMessage(UnpaidActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                HashMap<String, Object> map = null;
                                if (mMessage.what == ResponseCode.REQUEST_RESERVATION_DATA_SUCCESS) {
                                    map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    mReserveDaoList = Utils.jsonToList(ReserveDao.class, JSON.parseObject(map.get("reserve").toString(), List.class));
                                    List<StylistDao> stylistDaoList = Utils.jsonToList(StylistDao.class, JSON.parseObject(map.get("stylist").toString(), List.class));
                                    mHandler.post(() -> {
                                        UnpaidReservationAdapter adapter = new UnpaidReservationAdapter(mReserveDaoList, stylistDaoList);
                                        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(UnpaidActivity.this));
                                        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view, null);
                                        adapter.setEmptyView(emptyView);
                                        mBinding.recyclerView.setAdapter(adapter);
                                        adapter.setOnItemClickListener(new OnItemClickListener() {
                                            @Override
                                            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                                                HashMap<String, Object> map = new HashMap<>();
                                                ReserveDao reserveDao = mReserveDaoList.get(position);
                                                map.put("reserve", reserveDao);
                                                map.put("userId", reserveDao.getUserId());
                                                getValidPointsCoupon(map);
                                            }
                                        });
                                    });
                                }
                            }
                        }
                    }
                });
    }
    private void getValidPointsCoupon(HashMap<String, Object> map) {
        NetClient.getNetClient().callNet(NetworkSettings.VALID_POINTS_COUPON,
                RequestBody.create(JSON.toJSONString(map), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_VALID_POINTS_COUPON_FAILED;
                        mHandler.post(()-> Utils.showMessage(UnpaidActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.REQUEST_VALID_POINTS_COUPON_SUCCESS) {
                                    mMap = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    Bundle bundle = new Bundle();
                                    UserDao userDao = JSON.parseObject(mMap.get("user").toString(), UserDao.class);
                                    ReserveDao reserveDao = JSON.parseObject(mMap.get("order").toString(), ReserveDao.class);
                                    StylistDao stylistDao = JSON.parseObject(mMap.get("stylist").toString(), StylistDao.class);
                                    BarbershopDao barbershopDao = JSON.parseObject(mMap.get("barbershop").toString(), BarbershopDao.class);
                                    String result = (String) mMap.get("points");
                                    bundle.putSerializable("user", userDao);
                                    bundle.putSerializable("order", reserveDao);
                                    bundle.putString("result", result);
                                    bundle.putString("stylistRealName", stylistDao.getRealName());
                                    bundle.putString("address", barbershopDao.getAddress());
                                    bundle.putString("barbershopName", barbershopDao.getBarbershopName());

                                    List<CouponDao> couponDaoList;
                                    if (mMap.containsKey("coupon")) {
                                        couponDaoList = Utils.jsonToList(CouponDao.class, JSON.parseObject(mMap.get("coupon").toString(), List.class));
                                        ArrayList arrayList = new ArrayList();
                                        arrayList.add(couponDaoList);
                                        bundle.putParcelableArrayList("coupon", arrayList);
                                    }

                                    mHandler.post(()-> {
                                        Intent intent = new Intent(UnpaidActivity.this, PayActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    });
                                }
                            }
                        }
                    }
                });
    }
}