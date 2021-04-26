package com.example.beautysalon.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.utils.NetworkSettings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SplashActivity extends AppCompatActivity {

    private final OkHttpClient mClient = new OkHttpClient();
//    private final ObjectMapper mMapper = new ObjectMapper();
    private static final MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
    private HashMap<String, Object> mMap;
    //因为请求主体RequestBody需要指定主体的Content Type
    private final Message mMessage = new Message();
    private Intent mIntent;
    private final MyHandler mHandler = new MyHandler();
    private static final Bundle mBundle = new Bundle();

    public SplashActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread thread  = new Thread(() -> {
            try {
                Thread.sleep(400);
                SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                String token = pref.getString("token", "");
                if (token != null && token.length() != 0) {
                    Request request = new Request.Builder()
                            .url(NetworkSettings.VERIFY_IS_LOGINED)
                            .addHeader("token", token)
                            .post(RequestBody.create(token, mediaType))
                            .build();
                    mClient.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            mMessage.arg1 = 0;
                            mHandler.sendMessage(mMessage);
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            mMessage.arg1 = 0;
                            if (response.isSuccessful()) {
                                ResponseBody responseBody = response.body();
                                if (responseBody != null) {
                                    RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                    mMessage.what = restResponse.getCode();
                                    if (mMessage.what == ResponseCode.SIGN_IN_SUCCESS) {
                                        mMessage.arg1 = 1;
                                        mMessage.obj = restResponse.getData();
                                    }
                                }
                            }
                            mHandler.sendMessage(mMessage);
                        }
                    });
                } else  {
                    mMessage.arg1 = 0;
                    mHandler.sendMessage(mMessage);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.arg1) {
                case 0:
                    mIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    Toast.makeText(SplashActivity.this, "连接服务器失败，请重新登录！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    mMap = JSON.parseObject(mMessage.obj.toString(), HashMap.class);
                    System.out.println(mMap.get("token"));
                    if (mMap.containsKey("user")) {
                        mIntent = new Intent(SplashActivity.this, MainActivity.class);
                        UserDao userDao = JSON.parseObject(String.valueOf(mMap.get("user")), UserDao.class);
                        mBundle.putSerializable("user", userDao);
                    } else {
                        mIntent = new Intent(SplashActivity.this, StylistMainActivity.class);
                        StylistDao stylistDao = JSON.parseObject(String.valueOf(mMap.get("stylist")), StylistDao.class);
                        mBundle.putSerializable("stylist", stylistDao);
                    }
                    mIntent.putExtras(mBundle);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
            startActivity(mIntent);
            finish();
        }
    }
}