package com.example.beautysalon.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.CommentDao;
import com.example.beautysalon.dao.ReservationBean;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.databinding.ActivityHistoryReservationBinding;
import com.example.beautysalon.ui.adapter.NodeTwoAdapter;
import com.example.beautysalon.ui.widget.CommentNode;
import com.example.beautysalon.ui.widget.ReservationNode;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HistoryReservationActivity extends AppCompatActivity {

    private ActivityHistoryReservationBinding mBinding;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private NodeTwoAdapter adapter = new NodeTwoAdapter();
    private StylistDao mStylistDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityHistoryReservationBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
        requestForData();
    }

    public void initData() {
        mStylistDao = (StylistDao) getIntent().getExtras().getSerializable("stylist");
        mBinding.swipeLayout.setColorSchemeColors(Color.parseColor("#5872fd"));
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }

    public void setListeners() {
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    requestForData();
                    //刷新完成
                    mBinding.swipeLayout.setRefreshing(false);
                    Toast.makeText(HistoryReservationActivity.this, "已更新", Toast.LENGTH_SHORT).show();
                }, 400);
            }
        });
    }

    private void requestForData() {
        NetClient.getNetClient().callNet(NetworkSettings.RESERVATION_COMMENT_DATA,
                RequestBody.create(JSON.toJSONString(mStylistDao), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_RESERVATION_COMMENT_DATA_FAILED;
                        mHandler.post(()-> Utils.showMessage(HistoryReservationActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        System.out.println("0");
                        if (response.isSuccessful()) {
                            System.out.println("1");
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                System.out.println("2");
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.REQUEST_RESERVATION_COMMENT_DATA_SUCCESS) {
                                    HashMap<String, Object> map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    List<ReservationBean> reservationBeanList = Utils.jsonToList(
                                            ReservationBean.class, JSON.parseObject(map.get("reservation").toString(), List.class));
                                    System.out.println("size" + reservationBeanList.size());
                                    for (ReservationBean reservationBean: reservationBeanList) {
                                        System.out.println("订单id" + reservationBean.getReservationId());
                                    }
                                    List<Object> list = Utils.jsonToList(List.class,
                                            JSON.parseObject(map.get("comment").toString(), List.class));
                                    List<Object> commentDaoList = new ArrayList<>();
                                    for (int i = 0; i < list.size(); i++) {
                                        List<CommentDao> tempList = Utils.jsonToList(CommentDao.class,
                                                JSON.parseObject(list.get(i).toString(), List.class));
                                        if (tempList.size() > 0) {
                                            System.out.println(tempList.get(0).getContact());
                                        }
                                        commentDaoList.add(tempList);
                                    }
                                    System.out.println(commentDaoList.get(0));
                                    mHandler.post(() -> {
                                        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(HistoryReservationActivity.this));
                                        mBinding.recyclerView.setAdapter(adapter);
                                        adapter.setList(getEntity(reservationBeanList, commentDaoList));
                                    });
                                }
                            }
                        }
                    }
                });
    }

    private List<BaseNode> getEntity(List<ReservationBean> reservationBeanList,
                                     List<Object> commentDaoList) {
//        List<BaseNode> list = new ArrayList<>();
//        ReservationBean reservationBean = new ReservationBean();
//        reservationBean.setHobby("敲代码");
//        reservationBean.setReservationId(111);
//        reservationBean.setServeDate(new Date());
//        reservationBean.setServices("箭头、洗头、吹发");
//        reservationBean.setUserName("繁花十里");
//
//        CommentDao commentDao = new CommentDao();
//        commentDao.setUserId(32);
//        commentDao.setStylistId(11);
//        commentDao.setRate(4.5f);
//        commentDao.setContact("真的很不错真的很不错真的很不错");
//        commentDao.setCommentId(1);
//        commentDao.setOrderId(1);
//        commentDao.setUserName("我是谁");
//        commentDao.setCreateDate("2021-04-25 19:13");
//        commentDao.setUserAvatar("http://www.xiaobais.net:8080/image/dd9636aedf8f196b1924830f6bd545a4/avatar/1618320220475.jpg");


        List<BaseNode> result = new ArrayList<>();
        int i = 0, j = 0;
        for (i = 0; i < reservationBeanList.size(); i++) {
            List<BaseNode> firstList = new ArrayList<>();
            List<CommentDao> tempList =  ((List<CommentDao>) commentDaoList.get(i));
            List<BaseNode> secondList = new ArrayList<>();
            for (j = 0; j < tempList.size(); j++) {

                CommentNode commentNode = new CommentNode(tempList.get(j));
                secondList.add(commentNode);
            }
            ReservationNode reservationNode = new ReservationNode(secondList, reservationBeanList.get(i));
            result.add(reservationNode);
        }
        return result;

//        for (int i = 0; i < 8; i++) {
//            List<BaseNode> list1 = new ArrayList<>();
//            for (int j = 0; j < 5; j++) {
//                CommentNode commentNode = new CommentNode(commentDao);
//                list1.add(commentNode);
//            }
//            ReservationNode reservationNode = new ReservationNode(list1, reservationBean);
//            list.add(reservationNode);
//        }
//        return list;
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