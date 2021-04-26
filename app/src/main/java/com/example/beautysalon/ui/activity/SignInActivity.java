package com.example.beautysalon.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivitySignInBinding;
import com.example.beautysalon.ui.widget.StepBean;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding mBinding;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private UserDao mUser;
    private int mConsecutiveDays;
    private ArrayList<StepBean> mStepBeans = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
    }

    private void setListeners() {
        mBinding.signIn.setOnClickListener(v -> {
            HashMap<String, Integer> map = new HashMap<>();
            Random random = new Random();
            int points = random.nextInt(7) + 1;
            map.put("userId", mUser.getUserId());
            map.put("points", points + mStepBeans.get(mConsecutiveDays % 7).getNumber());
            map.put("days", mConsecutiveDays + 1);
            NetClient.getNetClient().callNet(NetworkSettings.RECEIVE_POINTS,
                    RequestBody.create(JSON.toJSONString(map), Utils.MEDIA_TYPE),
                    new NetClient.MyCallBack() {
                        @Override
                        public void onFailure(int code) {
                            mMessage.what = ResponseCode.RECEIVE_POINTS_FAILED;
                            mHandler.post(()-> Utils.showMessage(SignInActivity.this, mMessage));
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (response.isSuccessful()) {
                                ResponseBody responseBody = response.body();
                                if (responseBody != null) {
                                    RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                    mMessage.what = restResponse.getCode();
                                    if (mMessage.what == ResponseCode.RECEIVE_POINTS_SUCCESS) {
                                        mHandler.post(() -> {
                                            mBinding.rlOval.setBackgroundResource(R.drawable.sign_bg);
                                            mBinding.points.setText("签到成功！获得积分 +" + points);
                                            mBinding.reward.setText("连续签到奖励积分 +" + mStepBeans.get(mConsecutiveDays % 7).getNumber());
                                            mBinding.consecutiveDays.setText("连续" + (mConsecutiveDays + 1) + "天");
                                            mBinding.consecutiveDays.setVisibility(View.VISIBLE);
                                            mBinding.stepView.startSignAnimation(mConsecutiveDays % 7);
                                            mBinding.signIn.setClickable(false);
                                            mBinding.signIn.setText("已签到");
                                        });
                                    }
                                }
                            }
                        }
                    });
        });
    }

    private void initData() {
        mUser = (UserDao) getIntent().getExtras().getSerializable("user");
        mStepBeans.add(new StepBean(StepBean.STEP_COMPLETED, 2));
        mStepBeans.add(new StepBean(StepBean.STEP_CURRENT, 4));
        mStepBeans.add(new StepBean(StepBean.STEP_UNDO, 4));
        mStepBeans.add(new StepBean(StepBean.STEP_UNDO, 9));
        mStepBeans.add(new StepBean(StepBean.STEP_UNDO, 4));
        mStepBeans.add(new StepBean(StepBean.STEP_UNDO, 4));
        mStepBeans.add(new StepBean(StepBean.STEP_UNDO, 16));
        mBinding.stepView.setStepNum(mStepBeans);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
        NetClient.getNetClient().callNet(NetworkSettings.SIGN_IN_DATA,
                RequestBody.create(JSON.toJSONString(mUser.getUserId()), Utils.MEDIA_TYPE),
                new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_SIGN_IN_DATA_FAILED;
                        mHandler.post(()-> Utils.showMessage(SignInActivity.this, mMessage));
                    }
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.REQUEST_SIGN_IN_DATA_SUCCESS) {
                                    HashMap<String, Object> map = JSON.parseObject(
                                            restResponse.getData().toString(), HashMap.class);

                                    mHandler.post(() -> {
                                        if (Objects.equals(map.get("hasSignedIn"), "true")) {
                                            mBinding.signIn.setClickable(false);
                                            mBinding.signIn.setText("已签到");
                                            mBinding.consecutiveDays.setVisibility(View.VISIBLE);
                                            mBinding.rlOval.setBackgroundResource(R.drawable.sign_bg);
                                        }
                                        mConsecutiveDays = 0;
                                        if (map.get("isConsecutive").equals("true")) {
                                            mConsecutiveDays = (int) map.get("days");
                                            mBinding.consecutiveDays.setVisibility(View.VISIBLE);
                                            mBinding.consecutiveDays.setText("连续" + mConsecutiveDays + "天");
                                        }
                                        for (int i = 0; i < mConsecutiveDays % 7; i++) {
                                            mStepBeans.get(i).setState(StepBean.STEP_COMPLETED);
                                        }
                                        mStepBeans.get(mConsecutiveDays % 7).setState(StepBean.STEP_CURRENT);
                                        for (int i = 1; i < 7 - (mConsecutiveDays % 7); i++) {
                                            mStepBeans.get((mConsecutiveDays % 7) + 1).setState(StepBean.STEP_UNDO);
                                        }
                                        mBinding.stepView.setStepNum(mStepBeans);

                                    });
                                } else {
                                    mHandler.post(() -> {
                                        mConsecutiveDays = 0;

                                        mStepBeans.get(mConsecutiveDays).setState(StepBean.STEP_CURRENT);
                                        for (int i = 1; i < 7; i++) {
                                            mStepBeans.get(i).setState(StepBean.STEP_UNDO);
                                        }
                                        mBinding.stepView.setStepNum(mStepBeans);

                                    });
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish(); // back button
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}