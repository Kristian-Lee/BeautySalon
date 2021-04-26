package com.example.beautysalon.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.CouponDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.FragmentMyCouponBinding;
import com.example.beautysalon.ui.adapter.MyCouponAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Lee
 * @date 2021.3.30  16:59
 * @description
 */
public class MyCouponFragment extends Fragment {

    private FragmentMyCouponBinding mBinding;
    private HashMap<String, Object> mMap = new HashMap<>();
    private List<CouponDao> mCouponDaoList;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mIsOk = false; // 是否完成View初始化
    private boolean mIsFirst = true; // 是否为第一次加载

    public MyCouponFragment(){

    }
    public static MyCouponFragment newInstance(String type) {
        MyCouponFragment fragment = new MyCouponFragment();
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
        UserDao userDao = (UserDao) getActivity().getIntent().getExtras().getSerializable("user");
        mMap.put("userId", userDao.getUserId());
        mMap.put("type", getArguments().getString("type"));
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
        MyCouponAdapter adapter = new MyCouponAdapter(mCouponDaoList);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view, null);
        adapter.setEmptyView(emptyView);
        mBinding.recyclerView.setAdapter(adapter);
    }

    public void requestForData() {
        NetClient.getNetClient().callNet(NetworkSettings.COUPON_DATA,
                RequestBody.create(JSON.toJSONString(mMap), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
            @Override
            public void onFailure(int code) {
                mMessage.what = ResponseCode.REQUEST_COUPON_DATA_FAILED;
                mHandler.post(()-> Utils.showMessage(getActivity(), mMessage));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                        mMessage.what = restResponse.getCode();
                        if (mMessage.what == ResponseCode.REQUEST_COUPON_DATA_SUCCESS) {
                            mCouponDaoList = Utils.jsonToList(CouponDao.class, JSON.parseObject(restResponse.getData().toString(), List.class));
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
