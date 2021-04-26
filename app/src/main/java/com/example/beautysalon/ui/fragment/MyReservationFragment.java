package com.example.beautysalon.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.BarbershopDao;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.FragmentMyCouponBinding;
import com.example.beautysalon.ui.activity.ReserveDetailActivity;
import com.example.beautysalon.ui.adapter.MyReservationAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Lee
 * @date 2021.4.12  21:00
 * @description
 */
public class MyReservationFragment extends Fragment {

    private FragmentMyCouponBinding mBinding;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private HashMap<String, Object> mMap = new HashMap<>();
    private UserDao mUserDao;
    private List<StylistDao> mStylistDaoList;
    private List<ReserveDao> mReserveDaoList;
    private List<BarbershopDao> mBarbershopDaoList;
    private String mType;
    private boolean mIsOk = false; // 是否完成View初始化
    private boolean mIsFirst = true; // 是否为第一次加载

    public MyReservationFragment(){

    }
    public static MyReservationFragment newInstance(String type) {
        MyReservationFragment fragment = new MyReservationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMyCouponBinding.inflate(inflater);
        setListeners();
        mIsOk = true;
        initData();
        return mBinding.getRoot();
    }


    public void initData() {
        mBinding.swipeLayout.setColorSchemeColors(Color.parseColor("#5872fd"));
        mUserDao = (UserDao) getActivity().getIntent().getExtras().getSerializable("user");
        mType = getArguments().getString("type");
        if (mIsOk && mIsFirst) { // 加载数据时判断是否完成view的初始化，以及是不是第一次加载此数据
            requestForData();
            mIsFirst = false; // 加载第一次数据后改变状态，后续不再重复加载
        } else {
            loadData();
        }
    }

    public void setListeners() {
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    requestForData();
                    //刷新完成
                    mBinding.swipeLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "已更新", Toast.LENGTH_SHORT).show();
                }, 400);
            }
        });
    }

    public void loadData() {
        MyReservationAdapter adapter = new MyReservationAdapter(mReserveDaoList, mStylistDaoList);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view, null);
        adapter.setEmptyView(emptyView);
        mBinding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ReserveDao reserveDao = mReserveDaoList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", mUserDao);
                bundle.putSerializable("reserve", mReserveDaoList.get(position));
                bundle.putSerializable("stylist", mStylistDaoList.get(position));
                bundle.putSerializable("barbershop", mBarbershopDaoList.get(position));
                Intent intent = new Intent(getActivity(), ReserveDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void requestForData() {
        NetClient.getNetClient().callNet(NetworkSettings.RESERVATION_DATA, "type", mType,
                RequestBody.create(JSON.toJSONString(mUserDao), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_RESERVATION_DATA_FAILED;
                        mHandler.post(()-> Utils.showMessage(getActivity(), mMessage));
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
                                    mStylistDaoList = Utils.jsonToList(StylistDao.class, JSON.parseObject(map.get("stylist").toString(), List.class));
                                    mBarbershopDaoList = Utils.jsonToList(BarbershopDao.class, JSON.parseObject(map.get("barbershop").toString(), List.class));
                                    Collections.reverse(mReserveDaoList);
                                    Collections.reverse(mStylistDaoList);
                                    Collections.reverse(mBarbershopDaoList);
                                    mHandler.post(() -> {
                                        loadData();
                                    });
                                }
                            }
                        }
                    }
                });
    }
}
