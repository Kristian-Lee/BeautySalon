package com.example.beautysalon.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.FragmentMyBinding;
import com.example.beautysalon.ui.activity.MyBalanceActivity;
import com.example.beautysalon.ui.activity.MyCommentActivity;
import com.example.beautysalon.ui.activity.MyCouponActivity;
import com.example.beautysalon.ui.activity.MyInformationActivity;
import com.example.beautysalon.ui.activity.MyPointsActivity;
import com.example.beautysalon.ui.activity.MyReservationActivity;
import com.example.beautysalon.ui.activity.UnpaidActivity;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * @author Lee
 * @date 2021.3.20  17:29
 * @description
 */
public class MyFragment extends Fragment {

    private FragmentMyBinding mBinding;
    private Drawable drawableLeft, drawableRight;
    private String mToken;
    private Badge mBadge = null;
    private UserDao mUserDao;
    private Bundle mBundle = new Bundle();
    private static MyFragment instance;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Integer mBadgeNum = 0;

    public static synchronized MyFragment getInstance() {
        if (instance == null) {
            instance = new MyFragment();
        }
        return instance;
    }

    public static MyFragment newInstance(Bundle bundle) {
        MyFragment fragment = getInstance();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mToken = getArguments().getString("token");
            mUserDao = (UserDao) getArguments().getSerializable("user");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentMyBinding.inflate(inflater);
        setDrawable(mBinding.myReservations, R.drawable.my_reservations);
        setDrawable(mBinding.unpaidReservations, R.drawable.unpaid_reservations);
        setDrawable(mBinding.myComments, R.drawable.my_comments);
        setDrawable(mBinding.myPoints, R.drawable.my_points);
        setDrawable(mBinding.myCoupons, R.drawable.my_coupons);
        setDrawable(mBinding.myBalance, R.drawable.my_balance);
        initData();
        setListeners();
        return mBinding.getRoot();
    }

    public void setDrawable(TextView textView, int resId) {
        drawableLeft = ResourcesCompat.getDrawable(getResources(), resId, null);
        drawableRight = ResourcesCompat.getDrawable(getResources(), R.drawable.ahead, null);
        drawableLeft.setBounds(0, 0, 70, 70);
        drawableRight.setBounds(0, 0, 60, 60);
        //?????????????????????
        textView.setCompoundDrawables(drawableLeft, null, drawableRight, null);
        //???????????????text???????????????
        textView.setCompoundDrawablePadding(70);
    }

    public void setListeners() {
        mBinding.profileContainer.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyInformationActivity.class);
            intent.putExtras(getArguments());
            startActivity(intent);
        });
        mBinding.myCoupons.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyCouponActivity.class);
            intent.putExtras(getArguments());
            startActivity(intent);
        });
        mBinding.myPoints.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyPointsActivity.class);
            intent.putExtras(getArguments());
            startActivity(intent);
        });
        mBinding.myBalance.setOnClickListener(v -> {
            Intent intent =new Intent(getActivity(), MyBalanceActivity.class);
            intent.putExtras(getArguments());
            startActivity(intent);
        });
        mBinding.unpaidReservations.setOnClickListener(v -> {
            Intent intent =new Intent(getActivity(), UnpaidActivity.class);
            intent.putExtras(getArguments());
            startActivity(intent);
        });
        mBinding.myReservations.setOnClickListener(v -> {
            Intent intent =new Intent(getActivity(), MyReservationActivity.class);
            intent.putExtras(getArguments());
            startActivity(intent);
        });
        mBinding.myComments.setOnClickListener(v -> {
            Intent intent =new Intent(getActivity(), MyCommentActivity.class);
            intent.putExtras(getArguments());
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
//        if (mBadgeNum > 0) {
//            addBadgeAt(mBadgeNum);
//        }
    }

    private void initData() {
        NetClient.getNetClient().callNet(NetworkSettings.USER_DATA,
                RequestBody.create(JSON.toJSONString(mUserDao.getUserId()), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_USER_DATA_FAILED;
                        mHandler.post(()-> Utils.showMessage(getActivity(), mMessage));
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.REQUEST_USER_DATA_SUCCESS) {
                                    mUserDao = JSON.parseObject(restResponse.getData().toString(), UserDao.class);
                                    mBundle.putSerializable("user", mUserDao);
                                    setArguments(mBundle);
                                    mHandler.post(() -> {
                                        mBinding.money.setText("?????????" + mUserDao.getMoney());
                                        mBinding.userName.setText(mUserDao.getUserName());
                                        mBinding.points.setText("?????????" + mUserDao.getPoints());
                                        Glide.with(mBinding.getRoot())
                                                .load(mUserDao.getAvatar())
                                                .centerCrop()
                                                .placeholder(R.drawable.dog)
                                                .into(mBinding.avatar);
                                    });
                                }
                            }
                        }
                    }
                });
        NetClient.getNetClient().callNet(NetworkSettings.RESERVATION_DATA, "type", "unpaid",
                RequestBody.create(JSON.toJSONString(mUserDao), Utils.MEDIA_TYPE),
                new NetClient.MyCallBack() {
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
                                if (mMessage.what == ResponseCode.REQUEST_RESERVATION_DATA_SUCCESS) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    List<ReserveDao> reserveDaoList = Utils.jsonToList(ReserveDao.class,
                                            JSON.parseObject(map.get("reserve").toString(), List.class));
                                    setBadgeNum(reserveDaoList.size());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mBadge != null) {
                                                if (mBadgeNum == 0) {
                                                    mBadge.hide(true);
                                                } else {
                                                    mBadge.setBadgeNumber(mBadgeNum);
                                                }
                                            } else if (mBadgeNum > 0){
                                                addBadgeAt(mBadgeNum);
                                            }

                                            if (mBadge == null) {
                                                System.out.println("badge?????????");
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
    }

    private Badge addBadgeAt(int number) {
        // add badge
        mBadge = new QBadgeView(getActivity())
                .setBadgeNumber(number)
                .setGravityOffset(0, 3, true)
                .bindTarget(mBinding.textView)
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
//                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState)
//                            Toast.makeText(getActivity(), "??????", Toast.LENGTH_SHORT).show();
                    }
                });
        return mBadge;
    }

    public void setBadgeNum(Integer num) {
        this.mBadgeNum = num;
    }

    public void hideBadge() {
        mBadge.hide(true);
        mBadgeNum = 0;
    }
}