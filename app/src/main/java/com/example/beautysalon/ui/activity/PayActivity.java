package com.example.beautysalon.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.CouponDao;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityPayBinding;
import com.example.beautysalon.ui.adapter.CouponPayAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.TimeCount;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PayActivity extends AppCompatActivity implements TimeCount.CallBack {

    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private ActivityPayBinding mBinding;
    private ReserveDao mReserveDao;
    List<CouponDao> mCouponDaoList = new ArrayList<>();
    private UserDao mUserDao;
    private TimeCount mTimeCount;
    private CouponPayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPayBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
    }
    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    private void initData() {
        Bundle bundle = getIntent().getExtras();
        mUserDao = (UserDao) bundle.getSerializable("user");
        mReserveDao = (ReserveDao) bundle.getSerializable("order");
        System.out.println("优惠券最先" + mReserveDao.getCoupon());
        String result = bundle.getString("result");
        String barbershopName = bundle.getString("barbershopName");
        String stylistRealName = bundle.getString("stylistRealName");
        String address = bundle.getString("address");

        if (bundle.containsKey("coupon")) {
            ArrayList list = bundle.getParcelableArrayList("coupon");
            mCouponDaoList = (List<CouponDao>) list.get(0);
        }

        mBinding.orderId.setText(String.valueOf(mReserveDao.getId()));
        mBinding.stylistRealName.setText(stylistRealName);
        mBinding.barbershopName.setText(barbershopName);
        mBinding.barbershopAddress.setText(address);
        mBinding.services.setText(mReserveDao.getServices());
        mBinding.takeUp.setText(mReserveDao.getTakeUp() + "分钟");
        mBinding.reserveDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(mReserveDao.getServeDate()));
        mBinding.createDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(mReserveDao.getCreateDate()));
        mBinding.total.setText(String.valueOf(mReserveDao.getTotal()));
        if (result.equals("true")) {
            mBinding.pointsTip.setText("使用" + ((int)(mReserveDao.getTotal() * 20)) + "积分抵扣" +
                    (mReserveDao.getTotal() / 5) + "元");
        } else {
            mBinding.usePoints.setClickable(false);
            mBinding.pointsTip.setText("积分不足");
            mBinding.points.setText("无");
            mBinding.pointsSymbol.setVisibility(View.INVISIBLE);
        }
        if (mCouponDaoList != null && mCouponDaoList.size() > 0) {
            mBinding.couponSymbol.setVisibility(View.INVISIBLE);
            mBinding.coupon.setText("有" + mCouponDaoList.size() + "张优惠券可用");
        } else {
            mBinding.couponSymbol.setVisibility(View.INVISIBLE);
            mBinding.coupon.setText("无可用优惠券");
        }
        mBinding.realPay.setText(String.valueOf(mReserveDao.getTotal()));
        mTimeCount = new TimeCount(1800000, 1000, mReserveDao.getCreateDate(), mBinding.remainTime, this);
        mTimeCount.start();

//        List<String> list = new ArrayList<>();
//        list.add("满100减40");
//        list.add("满120减50");
//        list.add("满300减100");
//        list.add("不使用优惠券");

        mAdapter = new CouponPayAdapter(mCouponDaoList, mBinding);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(PayActivity.this));
        mBinding.recyclerView.setAdapter(mAdapter);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }

    private void setListeners() {
        mBinding.usePoints.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float realPay = Float.parseFloat(mBinding.realPay.getText().toString());
                if (isChecked) {
                    mBinding.pointsSymbol.setVisibility(View.VISIBLE);
                    mBinding.pointsSymbol.setTextColor(Color.parseColor("#e53d3d"));
                    mBinding.points.setTextColor(Color.parseColor("#e53d3d"));
                    mBinding.points.setText(String.valueOf((mReserveDao.getTotal() / 5)));
                    mBinding.realPay.setText(String.valueOf(realPay - mReserveDao.getTotal() / 5));
                } else {
                    mBinding.pointsSymbol.setTextColor(Color.parseColor("#757575"));
                    mBinding.points.setTextColor(Color.parseColor("#757575"));
                    mBinding.points.setText("无");
                    mBinding.pointsSymbol.setVisibility(View.INVISIBLE);
                    mBinding.realPay.setText(String.valueOf(realPay + mReserveDao.getTotal() / 5));
                }
            }
        });

        mBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        mBinding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.points.getText().toString().equals("无")) {
                    float result = Float.parseFloat(mBinding.points.getText().toString()) * 100;
                    mReserveDao.setPoints((int) result);
                }
                System.out.println("优惠券前" + mReserveDao.getCoupon());
                System.out.println(mAdapter.getLastPosition());
                if (mAdapter.getLastPosition() != -1) {
                    System.out.println("已进入");
                    mReserveDao.setCoupon(mCouponDaoList.get(mAdapter.getLastPosition()).getCouponId());
                }
                mReserveDao.setValue(Float.parseFloat(mBinding.realPay.getText().toString()));
                LayoutInflater inflater = getLayoutInflater();
                View myDialog = inflater.inflate(R.layout.layout_my_dialog, null);
                EditText editText = myDialog.findViewById(R.id.editText);
                final AlertDialog dialog = new AlertDialog.Builder(PayActivity.this)
                        .setView(myDialog)
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().equals("")) {
                            Toast.makeText(PayActivity.this, "密码不可为空", Toast.LENGTH_SHORT).show();
                        } else {
                            String password = Utils.encrypt(editText.getText().toString());
                            if (!password.equals(mUserDao.getPassword())) {
                                Toast.makeText(PayActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            } else if (mUserDao.getMoney() < Float.parseFloat(mBinding.realPay.getText().toString())) {
                                dialog.dismiss();
                                new AlertDialog.Builder(PayActivity.this, R.style.MyDialog)
                                        .setMessage("余额不足，是否前往充值？")
                                        .setNegativeButton("取消", null)
                                        .setPositiveButton("立刻充值", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(PayActivity.this, TopUpActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("user", mUserDao);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }
                                        }).show();
                            } else {
                                pay();
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

//        NetClient.getNetClient().callNet(NetworkSettings.CANCEL_RESERVE,
//                RequestBody.create(JSON.toJSONString(mReserveDao.getId()), Utils.MEDIA_TYPE),
//                new NetClient.MyCallBack() {
//                    @Override
//                    public void onFailure(int code) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Response response) throws IOException {
//
//                    }
//                });
    }

    private void pay() {
        NetClient.getNetClient().callNet(NetworkSettings.PAY,
                RequestBody.create(JSON.toJSONString(mReserveDao), Utils.MEDIA_TYPE),
                new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.PAY_FAILED;
                        mHandler.post(()-> Utils.showMessage(PayActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.PAY_SUCCESS) {
                                    Intent intent = new Intent(PayActivity.this, PaySuccessActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("user", mUserDao);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else if (mMessage.what == ResponseCode.PAY_TWICE_ERROR){

                                }
                                mHandler.post(()-> Utils.showMessage(PayActivity.this, mMessage));
                            }
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
                        mHandler.post(()-> Utils.showMessage(PayActivity.this, mMessage));
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
                                        mBinding.btnCancel.setVisibility(View.GONE);
                                        mBinding.btnPay.setVisibility(View.GONE);
                                        mBinding.btnSuccess.setVisibility(View.VISIBLE);
                                        mBinding.remainTime.setVisibility(View.INVISIBLE);
                                        Utils.showMessage(PayActivity.this, mMessage);
                                        mTimeCount.cancel();
                                    });
                                }
                            }
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeCount.cancel();
    }

    @Override
    public void done() {
        cancel();
    }
}
