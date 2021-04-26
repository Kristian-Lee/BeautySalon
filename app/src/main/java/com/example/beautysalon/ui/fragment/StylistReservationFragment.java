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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.FragmentStylistReservationBinding;
import com.example.beautysalon.ui.adapter.StylistReservationAdapter;
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
 * @date 2021.4.13  22:51
 * @description
 */
public class StylistReservationFragment extends Fragment {

    private FragmentStylistReservationBinding mBinding;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private String mType;
    private StylistDao mStylistDao;
    private StylistReservationFragment mInstance;

    public StylistReservationFragment() {

    }

    public static StylistReservationFragment newInstance(String type) {
        StylistReservationFragment fragment = new StylistReservationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentStylistReservationBinding.inflate(inflater);
        initData();
        requestForData();
        setListeners();
        return mBinding.getRoot();
    }

    private void initData() {
        mStylistDao = (StylistDao) getActivity().getIntent().getExtras().getSerializable("stylist");
        mType = getArguments().getString("type");
        System.out.println(mType);
        System.out.println(mStylistDao.toString());
        mBinding.swipeLayout.setColorSchemeColors(Color.parseColor("#5872fd"));
    }

    public void requestForData() {
        NetClient.getNetClient().callNet(NetworkSettings.STYLIST_RESERVATION, "type", mType,
                RequestBody.create(JSON.toJSONString(mStylistDao), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_STYLIST_RESERVATION_FAILED;
                        mHandler.post(()-> Utils.showMessage(getActivity(), mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        System.out.println("获取返回");
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                System.out.println("不为空");
                                if (mMessage.what == ResponseCode.REQUEST_STYLIST_RESERVATION_SUCCESS) {
                                    System.out.println("请求成功");
                                    HashMap<String, Object> map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    List<ReserveDao> reserveDaoList = Utils.jsonToList(ReserveDao.class,
                                            JSON.parseObject(map.get("reserve").toString(), List.class));
                                    List<UserDao> userDaoList = Utils.jsonToList(UserDao.class,
                                            JSON.parseObject(map.get("user").toString(), List.class));
                                    mHandler.post(() -> {
                                        StylistReservationAdapter adapter = new StylistReservationAdapter(reserveDaoList, userDaoList);
                                        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view, null);
                                        adapter.setEmptyView(emptyView);
                                        mBinding.recyclerView.setAdapter(adapter);
                                    });
                                }
                            }
                        } else {
                            System.out.println("空");
                        }
                    }
                });
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
}
