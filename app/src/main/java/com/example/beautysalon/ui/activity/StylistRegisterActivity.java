package com.example.beautysalon.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.databinding.ActivityStylistRegisterBinding;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.gyf.immersionbar.ImmersionBar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class StylistRegisterActivity extends AppCompatActivity {

    private ActivityStylistRegisterBinding binding;
    private static final MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private HashMap<String, Integer> mMap;
    private Bundle mBundle = new Bundle();
    private int mBarbershopId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStylistRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        setListeners();
    }

    public void initData() {
        NetClient.getNetClient().callNet(NetworkSettings.BARBERSHOP_DATA, new NetClient.MyCallBack() {
            @Override
            public void onFailure(int code) {
                mMessage.what = ResponseCode.REQUEST_BARBERSHOP_DATA_FAILED;
                mHandler.post(()-> Utils.showMessage(getApplicationContext(), mMessage));
                System.out.println("请求数据失败，请检查网络后");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                        mMessage.what = restResponse.getCode();
                        if (mMessage.what == ResponseCode.REQUEST_BARBERSHOP_DATA_SUCCESS) {
                            System.out.println("请求成功");
                            mMap = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                            List<String> itemList = new LinkedList<>();
                            for (Object key : mMap.keySet()) {
                                itemList.add((String) key);
                            }
                            mHandler.post(() -> {
                                binding.spinner.setItems(itemList);
                            });
                        }
                    }
                }
            }
        });
        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(binding.view)
                .init();
    }
    public void setListeners() {
        binding.spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                Snackbar.make(view, "Clicked " + item + mMap.get(item), Snackbar.LENGTH_LONG).show();
                mBarbershopId = mMap.get(item);
            }
        });
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
                if (binding.realName.getText().toString().length() <= 0) {
                    binding.realName.setError("不可为空！");
                    isPassed = false;
                }
                if (binding.speciality.getText().toString().length() <= 0) {
                    binding.speciality.setError("不可为空！");
                    isPassed = false;
                }
                binding.workingYears.validateWith(new RegexpValidator("仅可输入数字", "\\d+"));
                if (binding.workingYears.getText().toString().length() > 2 ||
                        binding.workingYears.getText().toString().length() <= 0) {
                    binding.workingYears.setError("请输入2位内数字!");
                    isPassed = false;
                }
                if (isPassed) {
                    signUp();
                }
            }
        });
    }
    public void signUp() {
        String userName = binding.userName.getText().toString();
        String realName = binding.realName.getText().toString();
        String password = Utils.encrypt(binding.password.getText().toString());
        String phone = binding.phone.getText().toString();
        String speciality = binding.speciality.getText().toString();
        int workingYears = Integer.parseInt(binding.workingYears.getText().toString());
        StylistDao stylistDao = new StylistDao(userName, password, 1, phone, speciality,
                mBarbershopId, realName, workingYears);
        NetClient.getNetClient().callNet(NetworkSettings.SIGN_UP_STYLIST,
                RequestBody.create(JSON.toJSONString(stylistDao), mediaType), new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = code;
                        mHandler.post(()->Utils.showMessage(getApplicationContext(), mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                            mMessage.what = restResponse.getCode();
                            if (mMessage.what == ResponseCode.SIGN_UP_SUCCESS) {
                                HashMap<String, Object> map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                String token = (String) map.get("token");
                                StylistDao stylistDao = JSON.parseObject(String.valueOf(map.get("stylist")), StylistDao.class);
                                mBundle.putSerializable("stylist", stylistDao);
                                Utils.saveToken(getApplicationContext(), token);
                                Intent intent = new Intent(StylistRegisterActivity.this, StylistMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtras(mBundle);
                                startActivity(intent);
                                StylistRegisterActivity.this.finish();
                            } else if (mMessage.what == ResponseCode.USER_EXIST_FAILED) {
                                binding.phone.setError("该手机号已被注册！");
                            }
                        } else {
                            mMessage.what = ResponseCode.EMPTY_RESPONSE;
                            Log.e("RESPONSE_BODY_EMPTY", response.message());
                        }
                        mHandler.post(()->Utils.showMessage(getApplicationContext(), mMessage));
                    }
                });
    }
}