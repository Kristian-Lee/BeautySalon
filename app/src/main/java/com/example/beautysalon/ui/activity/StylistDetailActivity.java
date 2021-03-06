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
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
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
        //???????????????
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForData();
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
                            HashMap<String, Object> map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                            BarbershopDao barbershopDao = JSON.parseObject(map.get("barbershop").toString(), BarbershopDao.class);
                            mBarbershopDao = JSON.parseObject(map.get("barbershop").toString(), BarbershopDao.class);
                            mCommentDaoList = Utils.jsonToList(CommentDao.class, (List) map.get("comment"));
                            if (map.containsKey("reserve")) {
                                mReserveDao = JSON.parseObject(map.get("reserve").toString(), ReserveDao.class);
                                mHandler.post(() -> {
                                    mBinding.btnReserve.setText("????????????");
                                    mBinding.btnReserve.setBackgroundColor(Color.parseColor("#ea3d3d"));
                                });
                            } else {
                                mHandler.post(() -> {
                                    mBinding.btnReserve.setText("??????");
                                    mBinding.btnReserve.setBackgroundColor(Color.parseColor("#7b8efd"));
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
                        } else {
                            System.out.println("sasasaffssdssfsd");
                        }
                    } else {
                        System.out.println("????????????");
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
                if (mBinding.btnReserve.getText().toString().equals("??????")) {
                    showSheetDialog();
                } else {
                    System.out.println(mReserveDao.getServeDate());
                    @SuppressLint("SimpleDateFormat") String reserveDate = new SimpleDateFormat("MM???dd??? HH???mm???").format(mReserveDao.getServeDate());
                    new CircleDialog.Builder()
                            .setTitle("??????????????????")
                            .setText("??????????????????????????????" + reserveDate + "????????????")//??????
                            .setPositive("??????", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancelReserve();
                                }
                            })
                            .setNegative("????????????", null)
                            .show(getSupportFragmentManager());
                }
            }
        });
        mBinding.recyclerView.setLoadingMoreEnabled(true);
        mBinding.recyclerView.setLoadingMoreProgressStyle(ProgressStyle.LineScale);
        mBinding.recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    requestForData();
                    mBinding.recyclerView.refreshComplete();
                },400);
            }
            @Override
            public void onLoadMore() {
                mBinding.recyclerView.loadMoreComplete();
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
                    Toast.makeText(StylistDetailActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                    isPassed = false;
                }
                if (mTime.getText().equals("?????????????????????")) {
                    Toast.makeText(StylistDetailActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    isPassed = false;
                }
                if (isPassed && new Date().compareTo(mReserveTime) > 0 && mDialogView.findViewById(R.id.today).isSelected()) {
                    Toast.makeText(StylistDetailActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
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
        //????????????????????????????????????3/4
        return peekHeight - peekHeight / 3;
    }
    private void initTimePicker() {//Dialog ???????????????????????????
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
                .isDialog(true) //????????????false ??????????????????DecorView ????????????????????????
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("pvTime", "onCancelClickListener");
                    }
                })
                .setItemVisibleCount(5) //?????????????????????????????????1???????????????6???????????????????????????7???
                .setLineSpacingMultiplier(2.0f)
                .isAlphaGradient(true)
                .setTitleText("????????????")
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
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//??????????????????
                dialogWindow.setGravity(Gravity.BOTTOM);//??????Bottom,????????????
                dialogWindow.setDimAmount(0.3f);
            }
        }
    }
    private String getTime(Date date) {//???????????????????????????????????????
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    public Date getStartDate(Date date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.parse(simpleDateFormat.format(date));
    }

    //??????
    private void reserve() throws ParseException {
        HashMap<String, Object> map = new HashMap<>();
        List<ServicesDao> servicesDaoList = mServiceAdapter.getSelectList();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < servicesDaoList.size(); i++) {
            stringBuilder.append(servicesDaoList.get(i).getName());
            if (i != servicesDaoList.size() - 1) {
                stringBuilder.append("???");
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
                                    HashMap<String, Object> map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    Bundle bundle = new Bundle();
                                    UserDao userDao = JSON.parseObject(map.get("user").toString(), UserDao.class);
                                    ReserveDao reserveDao = JSON.parseObject(map.get("order").toString(), ReserveDao.class);
                                    String result = (String) map.get("points");
                                    bundle.putSerializable("user", userDao);
                                    bundle.putSerializable("order", reserveDao);
                                    bundle.putString("result", result);
                                    bundle.putString("stylistRealName", mStylistDao.getRealName());
                                    bundle.putString("address", mBarbershopDao.getAddress());
                                    bundle.putString("barbershopName", mBarbershopDao.getBarbershopName());

                                    List<CouponDao> couponDaoList;
                                    if (map.containsKey("coupon")) {
                                        couponDaoList = Utils.jsonToList(CouponDao.class, JSON.parseObject(map.get("coupon").toString(), List.class));
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
                                        mBinding.btnReserve.setText("????????????");
                                    });
                                } else if (mMessage.what == ResponseCode.RESERVE_FAILED) {
                                    mHandler.post(()-> Utils.showMessage(StylistDetailActivity.this, mMessage));
                                }
                            }
                        }
                    }
                });
    }

    //????????????
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
                                        mBinding.btnReserve.setText("??????");
                                        Utils.showMessage(StylistDetailActivity.this, mMessage);
                                    });
                                }
                            }
                        }
                    }
                });
    }
}