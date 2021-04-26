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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.BarbershopDao;
import com.example.beautysalon.dao.EvaluationDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.databinding.FragmentStylistBinding;
import com.example.beautysalon.ui.activity.HistoryReservationActivity;
import com.example.beautysalon.ui.activity.StylistInformationActivity;
import com.example.beautysalon.ui.activity.UserCommentActivity;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Lee
 * @date 2021.4.13  23:22
 * @description
 */
public class StylistFragment extends Fragment {

    private FragmentStylistBinding mBinding;
    private Drawable drawableLeft, drawableRight;
    private StylistDao mStylistDao;
    private static StylistFragment mInstance;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Bundle mBundle = new Bundle();


    public StylistFragment() {
        super();
    }

    public static synchronized StylistFragment getInstance() {
        if (mInstance == null) {
            mInstance = new StylistFragment();
        }
        return mInstance;
    }

    public static StylistFragment newInstance(Bundle bundle) {
        StylistFragment fragment = getInstance();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStylistDao = (StylistDao) getArguments().getSerializable("stylist");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentStylistBinding.inflate(inflater);
        setDrawable(mBinding.profile, R.drawable.profile);
        setDrawable(mBinding.reservation, R.drawable.my_reservations);
        setDrawable(mBinding.comment, R.drawable.my_comments);
        initData();
        setListeners();
        requestForData();
        return mBinding.getRoot();
    }

    private void initData() {

    }

    public void setListeners() {
        mBinding.profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StylistInformationActivity.class);
            intent.putExtras(getArguments());
            startActivity(intent);
        });
        mBinding.reservation.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HistoryReservationActivity.class);
            intent.putExtras(getArguments());
            startActivity(intent);
        });
        mBinding.comment.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserCommentActivity.class);
            intent.putExtras(getArguments());
            startActivity(intent);
        });
    }

    public void setDrawable(TextView textView, int resId) {
        drawableLeft = ResourcesCompat.getDrawable(getResources(), resId, null);
        drawableRight = ResourcesCompat.getDrawable(getResources(), R.drawable.ahead, null);
        drawableLeft.setBounds(0, 0, 70, 70);
        drawableRight.setBounds(0, 0, 60, 60);
        //放在左边和右边
        textView.setCompoundDrawables(drawableLeft, null, drawableRight, null);
        //设置图片和text之间的间距
        textView.setCompoundDrawablePadding(70);
    }

    private void requestForData() {
        NetClient.getNetClient().callNet(NetworkSettings.STYLIST_MAIN_DATA,
                RequestBody.create(JSON.toJSONString(mStylistDao), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_STYLIST_MAIN_DATA_FAILED;
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
                                if (mMessage.what == ResponseCode.REQUEST_STYLIST_MAIN_DATA_SUCCESS) {
                                    HashMap<String, Object> map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    StylistDao stylistDao = JSON.parseObject(map.get("stylist").toString(), StylistDao.class);
                                    EvaluationDao evaluationDao = JSON.parseObject(map.get("evaluation").toString(), EvaluationDao.class);
                                    BarbershopDao barbershopDao = JSON.parseObject(map.get("barbershop").toString(), BarbershopDao.class);
                                    mBundle.putSerializable("stylist", stylistDao);
                                    mBundle.putSerializable("barbershop", barbershopDao);
                                    setArguments(mBundle);
                                    mHandler.post(() -> {
                                        mBinding.popularity.setText("好评率：" + evaluationDao.getPopularity() + "%");
                                        mBinding.stylistRealName.setText(stylistDao.getRealName());
                                        mBinding.workingYears.setText("从业经验：" + stylistDao.getWorkingYears() + " 年");
                                        mBinding.rate.setRating(evaluationDao.getRate());
                                        Glide.with(mBinding.getRoot())
                                                .load(stylistDao.getAvatar())
                                                .centerCrop()
                                                .placeholder(R.drawable.dog)
                                                .into(mBinding.avatar);
                                    });
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestForData();
    }
}
