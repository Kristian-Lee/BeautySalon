package com.example.beautysalon.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityLoginBinding;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;

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

/**
 * @author Lee
 * @date 2021.03.24 10:47
 * @description 用户、发型师登录类
 */
public class LoginActivity extends AppCompatActivity {

    //ViewBinding类，减少组件的初始化
    private ActivityLoginBinding binding;
    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler(Looper.getMainLooper());
    //因为请求主体RequestBody需要指定主体的Content Type
    private static final MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
    private final Message message = new Message();
    private HashMap<String, Object> mMap;
    private Bundle mBundle = new Bundle();
    private long mExitTime;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        ImmersionBar.with(this)
                .statusBarView(binding.view)
                .init();
        initData();
        setListeners();
    }

    private void initData() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String userName = pref.getString("name", "");
        mPassword = pref.getString("password", "");
        if (!userName.equals("") && !mPassword.equals("")) {
            binding.userName.setText(userName);
            binding.password.setText(mPassword);
            binding.remember.setChecked(true);
        }
    }

    private void setListeners() {
        binding.userRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, UserRegisterActivity.class));
            }
        });
        binding.stylistRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, StylistRegisterActivity.class));
            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPassed = true;
                if (binding.userName.getText().toString().length() <= 0) {
                    binding.userName.setError("用户名不能为空！");
                    isPassed = false;
                }
                if (binding.password.getText().toString().length() <= 0) {
                    binding.password.setError("密码不可为空！");
                    isPassed = false;
                }
                if (isPassed) {
                    signIn();
                }
            }
        });
    }

    private void signIn() {
        String userName = binding.userName.getText().toString();
        String password = binding.password.getText().toString();
        //存在记住的密码
        if (!mPassword.equals("")) {
            //不相等，即需要加密
            if (!mPassword.equals(password)) {
                password = Utils.encrypt(password);
            }
        } else {
            password = Utils.encrypt(password);
        }
        Request request = new Request.Builder()
                .url(NetworkSettings.SIGN_IN)
                .post(RequestBody.create(JSON.toJSONString(new UserDao(userName, password)), mediaType))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                message.what = ResponseCode.REQUEST_FAILED;
                handler.post(()-> Utils.showMessage(getApplicationContext(),message));
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                        message.what = restResponse.getCode();
                        String token;
                        Intent intent;
                        if (message.what == ResponseCode.SIGN_IN_SUCCESS) {
                            mMap = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                            token = (String) mMap.get("token");
                            String name, password;
                            if (mMap.containsKey("user")) {
                                UserDao user = JSON.parseObject(String.valueOf(mMap.get("user")), UserDao.class);
                                name = user.getUserName();
                                password = user.getPassword();
                                mBundle.putSerializable("user", user);
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            } else {
                                StylistDao stylist = JSON.parseObject(String.valueOf(mMap.get("stylist")), StylistDao.class);
                                name = stylist.getStylistName();
                                password = stylist.getPassword();
                                mBundle.putSerializable("stylist", stylist);
                                intent = new Intent(LoginActivity.this, StylistMainActivity.class);
                            }
                            Utils.saveToken(getApplicationContext(), token);
                            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                            if (binding.remember.isChecked()) {
                                editor.putString("name", name);
                                editor.putString("password", password);
                                editor.apply();
                            } else {
                                editor.putString("name", "");
                                editor.putString("password", "");
                            }
                            editor.apply();
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtras(mBundle);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    } else {
                        message.what = ResponseCode.EMPTY_RESPONSE;
                        Log.e("RESPONSE_BODY_EMPTY", response.message());
                    }
                } else {
                    //请求失败，连接服务器异常
                    message.what = ResponseCode.SERVER_ERROR;
                    Log.e("SERVER_ERROR", response.message());
                }
                handler.post(()->Utils.showMessage(getApplicationContext(), message));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(LoginActivity.this, "再按一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}