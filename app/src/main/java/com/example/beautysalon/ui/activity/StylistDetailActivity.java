package com.example.beautysalon.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.BarbershopDao;
import com.example.beautysalon.dao.BusinessHoursDao;
import com.example.beautysalon.dao.CommentDao;
import com.example.beautysalon.dao.CouponDao;
import com.example.beautysalon.dao.EvaluationDao;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.ServicesDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityStylistDetailBinding;
import com.example.beautysalon.ui.adapter.RecommendTimeAdapter;
import com.example.beautysalon.ui.adapter.ServiceAdapter;
import com.example.beautysalon.ui.adapter.StylistDetailAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.mylhyl.circledialog.CircleDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Lee
 * @date 2021.3.28  13:08
 * @description
 */
public class StylistDetailActivity extends AppCompatActivity {
    private ActivityStylistDetailBinding mBinding;
    private BottomSheetBehavior mDialogBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View mDialogView;
    private TextView mTime, mTakeUp, mValue;
    private Integer mUserId;
    private Date mReserveTime;
    private ReserveDao mReserveDao;
    private ServiceAdapter mServiceAdapter;
    private StylistDetailAdapter mAdapter;
    private StylistDao mStylistDao;
    private EvaluationDao mEvaluationDao;
    private BarbershopDao mBarbershopDao;
    private List<CommentDao> mCommentDaoList;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private static final MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
    private HashMap<String, Object> mMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityStylistDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
        requestForData();
    }

    public void initData() {
        mStylistDao = (StylistDao) getIntent().getExtras().get("stylist");
        mEvaluationDao = (EvaluationDao) getIntent().getExtras().get("evaluation");
        mUserId = getIntent().getExtras().getInt("userId");
        mMap.put("stylist", mStylistDao);
        mMap.put("userId", mUserId);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }

    private void requestForData() {
        NetClient.getNetClient().callNet(NetworkSettings.COMMENT_DATA,
                RequestBody.create(JSON.toJSONString(mMap), mediaType), new NetClient.MyCallBack() {
            @Override
            public void onFailure(int code) {
                mMessage.what = ResponseCode.REQUEST_COMMENT_DATA_FAILED;
                mHandler.post(()-> Utils.showMessage(StylistDetailActivity.this, mMessage));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                        mMessage.what = restResponse.getCode();
                        if (mMessage.what == ResponseCode.REQUEST_COMMENT_DATA_SUCCESS) {
                            mMap = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                            BarbershopDao barbershopDao = JSON.parseObject(mMap.get("barbershop").toString(), BarbershopDao.class);
                            mBarbershopDao = JSON.parseObject(mMap.get("barbershop").toString(), BarbershopDao.class);
                            mCommentDaoList = Utils.jsonToList(CommentDao.class, (List) mMap.get("comment"));
                            if (mMap.containsKey("reserve")) {
                                mReserveDao = JSON.parseObject(mMap.get("reserve").toString(), ReserveDao.class);
                                mHandler.post(() -> {
                                    mBinding.btnReserve.setText("取消预约");
                                    mBinding.btnReserve.setBackgroundColor(Color.parseColor("#ea3d3d"));
                                });
                            }
                            mHandler.post(() -> {
                                mAdapter = new StylistDetailAdapter(StylistDetailActivity.this,
                                        mStylistDao, mEvaluationDao, mBarbershopDao, mCommentDaoList);
                                LinearLayoutManager manager = new LinearLayoutManager(StylistDetailActivity.this);
                                manager.setOrientation(LinearLayoutManager.VERTICAL);
                                mBinding.recyclerView.setLayoutManager(manager);
                                mBinding.recyclerView.setAdapter(mAdapter);
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

    private void setListeners() {
        mBinding.btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.btnReserve.getText().toString().equals("预约")) {
                    showSheetDialog();
                } else {
                    System.out.println(mReserveDao.getServeDate());
                    @SuppressLint("SimpleDateFormat") String reserveDate = new SimpleDateFormat("MM月dd日 HH时mm分").format(mReserveDao.getServeDate());
                    new CircleDialog.Builder()
                            .setTitle("预约取消确认")
                            .setText("是否取消与该发型师在" + reserveDate + "的预约？")//内容
                            .setPositive("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancelReserve();
                                }
                            })
                            .setNegative("手滑点错", null)
                            .show(getSupportFragmentManager());
                }
            }
        });
    }

    public void showSheetDialog() {
        mBottomSheetDialog = new BottomSheetDialog(StylistDetailActivity.this, R.style.BottomSheetDialog);
        mDialogView = View.inflate(this, R.layout.dialog_reservation_detail, null);
        mBottomSheetDialog.setContentView(mDialogView);
        mDialogBehavior = BottomSheetBehavior.from((View) mDialogView.getParent());
        mTime = mDialogView.findViewById(R.id.time);
        mTakeUp = mDialogView.findViewById(R.id.takeUp);
        mValue = mDialogView.findViewById(R.id.value);

        mDialogBehavior.setPeekHeight(getPeekHeight());

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker();
            }
        });

        mDialogView.findViewById(R.id.btn_reserve).setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View v) {
                boolean isPassed = true;
                if (mServiceAdapter.getSelectList().size() <= 0) {
                    Toast.makeText(StylistDetailActivity.this, "请选择至少一项服务", Toast.LENGTH_SHORT).show();
                    isPassed = false;
                }
                if (mTime.getText().equals("请选择预约时间")) {
                    Toast.makeText(StylistDetailActivity.this, "未选择预约时间", Toast.LENGTH_SHORT).show();
                    isPassed = false;
                }
                if (isPassed && new Date().compareTo(mReserveTime) > 0 && mDialogView.findViewById(R.id.today).isSelected()) {
                    Toast.makeText(StylistDetailActivity.this, "时间不合法", Toast.LENGTH_SHORT).show();
                    isPassed = false;
                }
                if (isPassed) {
                    reserve();
                }
            }
        });

        NetClient.getNetClient().callNet(NetworkSettings.BUSINESS_HOURS_DATA,
                RequestBody.create(JSON.toJSONString(mStylistDao.getStylistId()), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_BUSINESS_HOURS_DATA_FAILED;
                        mHandler.post(()-> Utils.showMessage(StylistDetailActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.REQUEST_BUSINESS_HOURS_DATA_SUCCESS) {
                                    HashMap<String, Object> map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    List<BusinessHoursDao> today = Utils.jsonToList(BusinessHoursDao.class, JSON.parseObject(map.get("today").toString(), List.class));
                                    List<BusinessHoursDao> tomorrow = Utils.jsonToList(BusinessHoursDao.class, JSON.parseObject(map.get("tomorrow").toString(), List.class));
                                    List<ServicesDao> servicesDaoList = Utils.jsonToList(ServicesDao.class, JSON.parseObject(map.get("service").toString(), List.class));

                                    mHandler.post(() -> {
                                        mDialogView.findViewById(R.id.today).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mDialogView.findViewById(R.id.today).setSelected(true);
                                                mDialogView.findViewById(R.id.tomorrow).setSelected(false);
                                                RecommendTimeAdapter adapter = new RecommendTimeAdapter(today);
                                                RecyclerView rvRecommend = mDialogView.findViewById(R.id.rv_recommend);
                                                rvRecommend.setLayoutManager(new GridLayoutManager(StylistDetailActivity.this, 2));
                                                rvRecommend.setAdapter(adapter);
                                            }
                                        });
                                        mDialogView.findViewById(R.id.tomorrow).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mDialogView.findViewById(R.id.today).setSelected(false);
                                                mDialogView.findViewById(R.id.tomorrow).setSelected(true);
                                                RecommendTimeAdapter adapter = new RecommendTimeAdapter(tomorrow);
                                                RecyclerView rvRecommend = mDialogView.findViewById(R.id.rv_recommend);
                                                rvRecommend.setLayoutManager(new GridLayoutManager(StylistDetailActivity.this, 2));
                                                rvRecommend.setAdapter(adapter);
                                            }
                                        });
                                        mDialogView.findViewById(R.id.today).setSelected(true);
                                        RecommendTimeAdapter adapter = new RecommendTimeAdapter(today);
                                        RecyclerView rvRecommend = mDialogView.findViewById(R.id.rv_recommend);
                                        rvRecommend.setLayoutManager(new GridLayoutManager(StylistDetailActivity.this, 2));
                                        rvRecommend.setAdapter(adapter);

                                        mServiceAdapter = new ServiceAdapter(servicesDaoList, mDialogView);
                                        RecyclerView rvService = mDialogView.findViewById(R.id.rv_service);
                                        rvService.setLayoutManager(new GridLayoutManager(StylistDetailActivity.this, 3));
                                        rvService.setAdapter(mServiceAdapter);
                                        mBottomSheetDialog.show();
                                    });
                                }
                            }
                        }
                    }
                });
    }

    protected int getPeekHeight() {
        int peekHeight = getResources().getDisplayMetrics().heightPixels;
        //设置弹窗高度为屏幕高度的3/4
        return peekHeight - peekHeight / 3;
    }
    private void initTimePicker() {//Dialog 模式下，在底部弹出
        TimePickerView mTimePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mReserveTime = date;
                mTime.setText(getTime(date));
                mTime.setSelected(true);
                mTime.setTextColor(Color.parseColor("#3656fd"));
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i("pvTime", "onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{false, false, false, true, true, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("pvTime", "onCancelClickListener");
                    }
                })
                .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setLineSpacingMultiplier(2.0f)
                .isAlphaGradient(true)
                .setTitleText("选择日期")
                .isCyclic(true)
                .build();

        mTimePicker.show();
        Dialog mDialog = mTimePicker.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            mTimePicker.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f);
            }
        }
    }
    private String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    public Date getStartDate(Date date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.parse(simpleDateFormat.format(date));
    }

    //预约
    private void reserve() throws ParseException {
        HashMap<String, Object> map = new HashMap<>();
        List<ServicesDao> servicesDaoList = mServiceAdapter.getSelectList();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < servicesDaoList.size(); i++) {
            stringBuilder.append(servicesDaoList.get(i).getName());
            if (i != servicesDaoList.size() - 1) {
                stringBuilder.append("、");
            }
        }
        map.put("Service", stringBuilder.toString());
        map.put("stylistId", mStylistDao.getStylistId());
        map.put("userId", mUserId);
        map.put("takeUp", mTakeUp.getText().toString());
        map.put("value", mValue.getText().toString());

        long takeUp = Long.parseLong(mTakeUp.getText().toString());
        if (mDialogView.findViewById(R.id.today).isSelected()) {
            map.put("dateFrom", mReserveTime);
            map.put("dateTo", new Date(mReserveTime.getTime() + takeUp * 60000));
        } else {
            map.put("dateFrom", new Date(mReserveTime.getTime() + 86400000L));
            map.put("dateTo", new Date(mReserveTime.getTime() + 86400000 + takeUp * 60000));
        }
        mBottomSheetDialog.dismiss();
        System.out.println(JSON.toJSONString(map));
        NetClient.getNetClient().callNet(NetworkSettings.RESERVE,
                RequestBody.create(JSON.toJSONString(map), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.RESERVE_FAILED;
                        mHandler.post(()-> Utils.showMessage(StylistDetailActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.RESERVE_SUCCESS) {
                                    mMap = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    Bundle bundle = new Bundle();
                                    UserDao userDao = JSON.parseObject(mMap.get("user").toString(), UserDao.class);
                                    ReserveDao reserveDao = JSON.parseObject(mMap.get("order").toString(), ReserveDao.class);
                                    String result = (String) mMap.get("points");
                                    bundle.putSerializable("user", userDao);
                                    bundle.putSerializable("order", reserveDao);
                                    bundle.putString("result", result);
                                    bundle.putString("stylistRealName", mStylistDao.getRealName());
                                    bundle.putString("address", mBarbershopDao.getAddress());
                                    bundle.putString("barbershopName", mBarbershopDao.getBarbershopName());

                                    List<CouponDao> couponDaoList;
                                    if (mMap.containsKey("coupon")) {
                                        couponDaoList = Utils.jsonToList(CouponDao.class, JSON.parseObject(mMap.get("coupon").toString(), List.class));
                                        ArrayList arrayList = new ArrayList();
                                        arrayList.add(couponDaoList);
                                        bundle.putParcelableArrayList("coupon", arrayList);
                                    }

                                    mHandler.post(()-> {
                                        Utils.showMessage(StylistDetailActivity.this, mMessage);
                                        Intent intent = new Intent(StylistDetailActivity.this, PayActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        mBinding.btnReserve.setBackgroundColor(Color.parseColor("#ea3d3d"));
                                        mBinding.btnReserve.setText("取消预约");
                                    });
                                } else if (mMessage.what == ResponseCode.RESERVE_FAILED) {
                                    mHandler.post(()-> Utils.showMessage(StylistDetailActivity.this, mMessage));
                                }
                            }
                        }
                    }
                });
    }

    //取消预约
    private void cancelReserve() {
        NetClient.getNetClient().callNet(NetworkSettings.CANCEL_RESERVE,
                RequestBody.create(JSON.toJSONString(mReserveDao.getId()), Utils.MEDIA_TYPE),
                new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.CANCEL_RESERVE_FAILED;
                        mHandler.post(()-> Utils.showMessage(StylistDetailActivity.this, mMessage));
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
                                        mBinding.btnReserve.setBackgroundColor(Color.parseColor("#7b8efd"));
                                        mBinding.btnReserve.setText("预约");
                                        Utils.showMessage(StylistDetailActivity.this, mMessage);
                                    });
                                }
                            }
                        }
                    }
                });
    }
}