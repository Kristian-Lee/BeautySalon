package com.example.beautysalon.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.InformationDao;
import com.example.beautysalon.databinding.ActivityInformationBinding;
import com.example.beautysalon.ui.adapter.InformationAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class InformationActivity extends AppCompatActivity {

    private ActivityInformationBinding mBinding;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityInformationBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
        requestForData();
    }

    private void requestForData() {
        NetClient.getNetClient().callNet(NetworkSettings.INFORMATION_DATA, new NetClient.MyCallBack() {
            @Override
            public void onFailure(int code) {
                mMessage.what = ResponseCode.REQUEST_INFORMATION_DATA_FAILED;
                mHandler.post(()-> Utils.showMessage(InformationActivity.this, mMessage));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                        mMessage.what = restResponse.getCode();
                        if (mMessage.what == ResponseCode.REQUEST_INFORMATION_DATA_SUCCESS) {
                            List<InformationDao> informationDaoList = Utils.jsonToList(InformationDao.class, JSON.parseObject(restResponse.getData().toString(), List.class));
                            mHandler.post(() -> {
                                InformationAdapter adapter = new InformationAdapter(informationDaoList);
                                mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(InformationActivity.this));
                                View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view, null);
                                adapter.setEmptyView(emptyView);
                                mBinding.recyclerView.setAdapter(adapter);
                                adapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                                        Intent intent = new Intent(InformationActivity.this, InformationDetailActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("information", informationDaoList.get(position));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });
                            });
                        }
                    }
                }
            }
        });
    }

    private void setListeners() {
        mBinding.swipeLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            requestForData();
            //刷新完成
            mBinding.swipeLayout.setRefreshing(false);
            Toast.makeText(InformationActivity.this, "已更新", Toast.LENGTH_SHORT).show();
        }, 400));
    }

    private void initData() {
        mBinding.swipeLayout.setColorSchemeColors(Color.parseColor("#5872fd"));
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
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