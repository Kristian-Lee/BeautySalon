package com.example.beautysalon.ui.activity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.BarbershopDao;
import com.example.beautysalon.dao.EvaluateDao;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityReserveDetailBinding;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ReserveDetailActivity extends AppCompatActivity {

    private ActivityReserveDetailBinding mBinding;
    private ReserveDao mReserveDao;
    private StylistDao mStylistDao;
    private UserDao mUserDao;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityReserveDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
    }
    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    private void initData() {
        Bundle bundle = getIntent().getExtras();
        mUserDao = (UserDao) bundle.getSerializable("user");
        mReserveDao = (ReserveDao) bundle.getSerializable("reserve");
        mStylistDao = (StylistDao) bundle.getSerializable("stylist");
        BarbershopDao barbershopDao = (BarbershopDao) bundle.getSerializable("barbershop");

        mBinding.orderId.setText(String.valueOf(mReserveDao.getId()));
        mBinding.stylistRealName.setText(mStylistDao.getRealName());
        mBinding.barbershopName.setText(barbershopDao.getBarbershopName());
        mBinding.barbershopAddress.setText(barbershopDao.getAddress());
        mBinding.services.setText(mReserveDao.getServices());
        mBinding.takeUp.setText(mReserveDao.getTakeUp() + "分钟");
        mBinding.reserveDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(mReserveDao.getServeDate()));
        mBinding.createDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(mReserveDao.getCreateDate()));
        mBinding.total.setText(String.valueOf(mReserveDao.getTotal()));

        float points = 0f;
        if (mReserveDao.getPoints() > 0) {
            mBinding.pointsSymbol.setVisibility(View.VISIBLE);
            mBinding.pointsSymbol.setTextColor(Color.parseColor("#ea3d3d"));
            mBinding.points.setTextColor(Color.parseColor("#ea3d3d"));
            points = ((float) mReserveDao.getPoints()) / 100;
            mBinding.points.setText(String.valueOf(points));
        }

        if (mReserveDao.getCoupon() > 0) {
            mBinding.couponSymbol.setVisibility(View.VISIBLE);
            mBinding.couponSymbol.setTextColor(Color.parseColor("#ea3d3d"));
            mBinding.coupon.setTextColor(Color.parseColor("#ea3d3d"));
            mBinding.coupon.setText(String.valueOf(mReserveDao.getTotal() - mReserveDao.getValue() - points));
        }
        mBinding.realPay.setText(String.valueOf(mReserveDao.getValue()));

        if (mReserveDao.getStatus() == 1) {
            if (mReserveDao.getServeDate().compareTo(new Date()) > 0) {
                mBinding.status.setText("待服务");
                mBinding.btnReturn.setVisibility(View.INVISIBLE);
                mBinding.btnNegative.setText("取消预约");
                mBinding.btnNegative.setBackgroundColor(Color.parseColor("#ea3d3d"));
            } else {
                mBinding.status.setText("待评价");
                mBinding.btnReturn.setVisibility(View.INVISIBLE);
                mBinding.btnNegative.setText("发表评价");
            }
        } else if (mReserveDao.getStatus() == 3) {
            mBinding.status.setText("已取消");
            mBinding.btnNegative.setVisibility(View.INVISIBLE);
            mBinding.btnPositive.setVisibility(View.INVISIBLE);
        }

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }

    private void setListeners() {
        mBinding.btnPositive.setOnClickListener(v -> finish());
        mBinding.btnReturn.setOnClickListener(v -> finish());
        mBinding.btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.btnNegative.getText().toString().equals("取消预约")) {
                    cancel();
                } else if (mBinding.btnNegative.getText().toString().equals("发表评价")) {
                    evaluate();
                }
            }
        });
    }

    private void cancel() {
        NetClient.getNetClient().callNet(NetworkSettings.CANCEL_RESERVE,
                RequestBody.create(JSON.toJSONString(mReserveDao.getId()), Utils.MEDIA_TYPE),
                new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.CANCEL_RESERVE_FAILED;
                        mHandler.post(()-> Utils.showMessage(ReserveDetailActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.CANCEL_RESERVE_SUCCESS) {
                                    mHandler.post(()-> {
                                        mBinding.btnNegative.setVisibility(View.INVISIBLE);
                                        mBinding.btnPositive.setVisibility(View.INVISIBLE);
                                        mBinding.btnReturn.setVisibility(View.VISIBLE);
                                        mBinding.status.setText("已取消");
                                        Utils.showMessage(ReserveDetailActivity.this, mMessage);
                                    });
                                }
                            }
                        }
                    }
                });
    }

    private void evaluate() {
        View dialogView = View.inflate(ReserveDetailActivity.this, R.layout.bottom_sheet_dialog_evaluate, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(ReserveDetailActivity.this, R.style.BottomSheetDialog);
        dialog.setContentView(dialogView);

        //下面这里主要
        BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mDialogBehavior.setPeekHeight(getWindowHeight());

        Button btnEvaluate = dialog.findViewById(R.id.evaluate);
        EditText contact = dialog.findViewById(R.id.contact);
        ScaleRatingBar rate = dialog.findViewById(R.id.rate);

        btnEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact.getText().toString().length() > 100) {
                    Toast.makeText(ReserveDetailActivity.this, "内容长度须在100以内", Toast.LENGTH_SHORT).show();
                } else if (contact.getText().toString().equals("")) {
                    Toast.makeText(ReserveDetailActivity.this, "内容不可为空", Toast.LENGTH_SHORT).show();
                } else {
                    EvaluateDao evaluateDao = new EvaluateDao();
                    evaluateDao.setCreateDate(new Date());
                    evaluateDao.setContact(contact.getText().toString());
                    evaluateDao.setOrderId(mReserveDao.getId());
                    evaluateDao.setRate(rate.getRating());
                    evaluateDao.setStylistId(mStylistDao.getStylistId());
                    evaluateDao.setUserId(mUserDao.getUserId());

                    NetClient.getNetClient().callNet(NetworkSettings.EVALUATE,
                            RequestBody.create(JSON.toJSONString(evaluateDao), Utils.MEDIA_TYPE),
                            new NetClient.MyCallBack() {
                                @Override
                                public void onFailure(int code) {
                                    mMessage.what = ResponseCode.EVALUATE_FAILED;
                                    mHandler.post(() -> Utils.showMessage(ReserveDetailActivity.this, mMessage));
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    System.out.println(1);
                                    if (response.isSuccessful()) {
                                        System.out.println(2);
                                        ResponseBody responseBody = response.body();
                                        if (responseBody != null) {
                                            System.out.println(3);
                                            RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                            mMessage.what = restResponse.getCode();
                                            mHandler.post(()-> {
                                                Utils.showMessage(ReserveDetailActivity.this, mMessage);
                                            });
                                        }
                                    }
                                }
                            });
                    dialog.dismiss();
                }
            }
        });
        //显示弹窗
        dialog.show();
    }

    //就是返回页面高度
    private int getWindowHeight() {
        //传入事件页面this
        Resources res = ReserveDetailActivity.this.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
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