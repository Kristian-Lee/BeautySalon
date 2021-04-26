package com.example.beautysalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityUserRegisterBinding;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyf.immersionbar.ImmersionBar;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

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

public class UserRegisterActivity extends AppCompatActivity {

    private ActivityUserRegisterBinding binding;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Handler handler = new Handler(Looper.getMainLooper());
    //因为请求主体RequestBody需要指定主体的Content Type
    private static final MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
    private final Message message = new Message();
    private HashMap<String, Object> mMap;
    private Bundle mBundle = new Bundle();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserRegisterBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initData();
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        ImmersionBar.with(this)
                .statusBarView(binding.view)
                .init();
        setListeners();
    }

    private void initData() {

    }
    private void setListeners() {
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPassed = true;
                if (binding.userName.getText().toString().length() <= 0) {
                    binding.userName.setError("不可为空！");
                    isPassed = false;
                }
                binding.phone.validateWith(new RegexpValidator("仅可输入数字", "\\d+"));
                if (binding.phone.getText().toString().length() < 11) {
                    binding.phone.setError("请输入11位数字!");
                    isPassed = false;
                }
                if (binding.password.getText().toString().length() <= 0) {
                    binding.password.setError("不可为空");
                    isPassed = false;
                }
                if (binding.confirmPassword.getText().toString().length() <= 0) {
                    binding.confirmPassword.setError("不可为空");
                    isPassed = false;
                }
                if (binding.password.getText().toString().length() > 0 &&
                        binding.confirmPassword.getText().toString().length() > 0 &&
                        !binding.password.getText().toString().equals(binding.confirmPassword.getText().toString())) {
                    binding.confirmPassword.setError("密码不一致");
                    isPassed = false;
                }
                if (isPassed) {
                    signUp();
                }
            }
        });
    }

    private void signUp() {
        String userName = binding.userName.getText().toString();
        String password = Utils.encrypt(binding.password.getText().toString());
        String phone = binding.phone.getText().toString();
        String hobby = binding.hobby.getText().toString();
        UserDao userDao = new UserDao(userName, password, 0, phone, hobby);
        Request request = new Request.Builder()
                .url(NetworkSettings.SIGN_UP)
                .post(RequestBody.create(
                        JSON.toJSONString(userDao), mediaType))
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
                        if (message.what == ResponseCode.SIGN_UP_SUCCESS) {
                            mMap = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                            String token = (String) mMap.get("token");
                            UserDao user = JSON.parseObject(String.valueOf(mMap.get("user")), UserDao.class);
                            mBundle.putSerializable("user", user);
                            Utils.saveToken(getApplicationContext(), token);
                            Intent intent = new Intent(UserRegisterActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtras(mBundle);
                            startActivity(intent);
                            UserRegisterActivity.this.finish();
                        } else if (message.what == ResponseCode.USER_EXIST_FAILED) {
                            binding.phone.setError("该手机号已被注册！");
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
}