package com.example.beautysalon.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.alipay.OrderInfoUtil2_0;
import com.example.beautysalon.alipay.PayResult;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityTopUpBinding;
import com.example.beautysalon.ui.adapter.TopUpAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;
import com.mylhyl.circledialog.CircleDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TopUpActivity extends AppCompatActivity {
    private ActivityTopUpBinding mBinding;
    UserDao mUser;
    private final Message mMessage = new Message();
    private final Handler handler = new Handler(Looper.getMainLooper());
    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    /* 应用的APPID */
    public static final String APPID = "2021000117631537";//参数都填自己的

    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    /* 商家的UUID */
    public static final String PID = "2088621955497589";

    /**
     * 用于支付宝账户登录授权业务的入参 target_id。
     */
    /* 商家的支付宝沙箱账号 */
    public static final String TARGET_ID = "akqilq3213@sandbox.com";

    /**
     *  pkcs8 格式的商户私钥。
     *
     * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * 	RSA2_PRIVATE。
     *
     * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    /* 应用私钥 */
    public static final String RSA2_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCzHxCINIcGB4fvJonkt7FBxplynEOXaHQGY4+L/43g9TO7V5DsGltNUvczq/ecvjFESQr3LvLC1BFefN+Nwl9cDWxiUVj9EpDsX1g++RS4lNRJMgCPbCz8iA3uRPJsSCwSb7mdGNe8+BD6gvHFktQBLtXC3OEWPoi6qfolh8y+S5zui3aZBuhTqFrKj36SLPAcmyXAA1nMr8LUk0/1RyA++nc2Kj0CtNG1CVQD9yT8RBrDnFyYL9sJf1VqMdIdeMcrKtfqQneIiUckAnkA2RYo6bzyQXbx/HHpeUPu7d3I2n31i/sa5AtmwS9fH2fQXFlW2yWLah3nChZOrLME3grVAgMBAAECggEBAJs4WEMwAbIf5JRBeU1SQbEWQPMopwoV4jWHIWXYV0KSOfomr9dqH6SqOL32T+TsjBT4lyAM9yIOU97lp3RY8/n5R+sLQ2pLJHsxRG+NuX8aubuUMRgYTgOogynvZYSgJlDzsI2Kcam37j6oKotj6YP86hCrCAMWvbXEhpjvTWW4/IaaQEGu60afp2lQjaUewgHDVfMOBQsgdi96kEgSJ2LpF7adgYKjb+UCVRO35l8UdyIaAmTPt+xa0arGaOBFymOlbHUCiFYdxZi1c+Km3J4MaUsZPGs7WhDDTZ4HgRyI1QJE0oUoIS3Z4/anYSOIPc4RvOtoJrKPcy1oUL2P3AECgYEA5YKq57KpG0gsIc7VsjaY/gON2J/oWPrsI+PF1QLhk6TSOeHLUfRNMSSK3HM6wO9A0DE2wwjR0yNsooj+flXOiDKPPNkUDyM31QZR+P3RuwJitEZ6t9QkHSVwiFjPy7w0YiZzEULMdKObXlRoBOvUyphmpshDE53wrfnQrM5NvzUCgYEAx8uNuvL92vCIvqiat9AuQaWhn4hzB1diCGJtxjsxllDg032Z/HkpDJo4dmJWpvMJLWeWOfduWGfPRdc/SH+oCMOrpxCJZxg6NJofqByVejabMfaqHa9/hia+/pUe517cMZy2FWW6vP8aoR0g8GJzJAu8msJDoJ0Fw6c1P6HqcSECgYAKIZuDwNpALw5rq6K5TDmytNt1HZH3I5R6/RoFjiwBBXyWO4ZrJrqlmbRbiOamD6mvxwxgzandhSnNfvilAQqt7nVuPTLqfCaBy9aghCvW8oMC7D2MkdvKQZXXHvyFRNiXIJVhxK4b1AEOx/ETjomFK/cUE7EKU6WFOIripfS+pQKBgC/eb0i5a2UHeFvl3ZvovwxveLfWY2kA4LYNjyuNhyXynstNLpW1GcaxNJH4obZCSJKeXPnqdH+t75VWKw1duI7RAtF2PtV6koeD3c3Te2TarHheFwghwjYMFnq8k7jvm2Yvm9iolQMvQ9wSNJAjpl6eiUOzA6z5siXca/+g9aDBAoGBAIYuaZQ4k4OJUzSnrqtR5yjFo1RiUlvfNIFPH9U10OQJgcvsDa6IlU8vMmCR1By2XmneLHJuz2EYMQj3Pr/c4zTu7FXq1Nelc8K73shOVaZMBkEeCfsn8rX168VO+LKyHzhLVwnDb8F4m60w7YX43VK1G76CYAPQKjrirY/Ecf21";
    public static final String RSA_PRIVATE = "";//不用填

    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        new CircleDialog.Builder()
                                .setTitle("支付结果")
                                //标题字体颜值 0x909090 or Color.parseColor("#909090")

                                .setText("充值成功！")//内容
                                .setPositive("确定", null)
                                .setNegative("返回首页", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                })
                                .show(getSupportFragmentManager());
                        HashMap<String, Integer> map = new HashMap<>();
                        map.put("userId", mUser.getUserId());
                        map.put("value", Integer.parseInt(mBinding.num.getText().toString()));
                        NetClient.getNetClient().callNet(NetworkSettings.TOP_UP,
                                RequestBody.create(JSON.toJSONString(map), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                                    @Override
                                    public void onFailure(int code) {
                                        mMessage.what = ResponseCode.TOP_UP_FAILED;
                                        //TODO 更新余额失败后在本地保存充值的金额，待下次联网时再更新
                                    }

                                    @Override
                                    public void onResponse(Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            ResponseBody responseBody = response.body();
                                            if (responseBody != null) {
                                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                                mMessage.what = restResponse.getCode();
                                                if (mMessage.what == ResponseCode.TOP_UP_SUCCESS) {
                                                    mHandler.post(() -> {
                                                        mBinding.balance.setText(String.valueOf(mUser.getMoney() + Integer.parseInt(mBinding.num.getText().toString())));
                                                    });
                                                }
                                            }
                                        }
                                    }
                                });
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        new CircleDialog.Builder()
                                .setTitle("支付结果")
                                //标题字体颜值 0x909090 or Color.parseColor("#909090")
                                .setText("您已取消支付")//内容
                                .setPositive("确定", null)
                                .show(getSupportFragmentManager());
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        super.onCreate(savedInstanceState);
        mBinding = ActivityTopUpBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mUser = (UserDao) getIntent().getExtras().getSerializable("user");
        List<Integer> numList = new ArrayList<>();
        numList.add(50);
        numList.add(80);
        numList.add(100);
        numList.add(150);
        numList.add(200);

        TopUpAdapter adapter = new TopUpAdapter(TopUpActivity.this, mBinding, numList);
        RecyclerView.LayoutManager manager = new GridLayoutManager(TopUpActivity.this, 3);
        mBinding.recyclerView.setLayoutManager(manager);
        mBinding.recyclerView.setAdapter(adapter);
        initData();
        setListeners();
    }


    /**
     * 支付宝支付业务示例
     */
    public void payV2(View v, String value) {
        /* 判断信息是否为空 */
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            Utils.showAlert(TopUpActivity.this, getString(R.string.error_missing_appid_rsa_private), null);
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);

        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2, value);

        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        /* 判断用户使用的是RSA2还是RSA */
        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(TopUpActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private void setListeners() {
        mBinding.btnTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.num.getText().toString().matches("^(0|[1-9][0-9]*)$") ||
                        Integer.parseInt(mBinding.num.getText().toString()) == 0){
                    Toast.makeText(TopUpActivity.this, "数额不正确", Toast.LENGTH_SHORT).show();
                } else {
                    payV2(v, mBinding.num.getText().toString());
                }
            }
        });
    }
    private void initData() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
        UserDao userDao = (UserDao) getIntent().getExtras().getSerializable("user");
        NetClient.getNetClient().callNet(NetworkSettings.USER_DATA,
                RequestBody.create(JSON.toJSONString(mUser.getUserId()), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_USER_DATA_FAILED;
                        mHandler.post(()-> Utils.showMessage(TopUpActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.REQUEST_USER_DATA_SUCCESS) {
                                    UserDao userDao = JSON.parseObject(restResponse.getData().toString(), UserDao.class);
                                    mHandler.post(() -> {
                                        mBinding.balance.setText(String.valueOf(userDao.getMoney()));
                                    });
                                }
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
}