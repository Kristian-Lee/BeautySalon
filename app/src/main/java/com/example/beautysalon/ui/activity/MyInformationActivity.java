package com.example.beautysalon.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.example.beautysalon.GlideEngine;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityMyInformationBinding;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Lee
 * @date 2021.3.23  02:23
 * @description
 */
public class MyInformationActivity extends AppCompatActivity {

    private static final int PHOTO_PICK_REQUEST_CODE = 1002;
    private ActivityMyInformationBinding mBinding;
    private TimePickerView mTimePicker;
    private UserDao mUserDao;
    private String path = null;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final MyHandler handler = new MyHandler();
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMyInformationBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();

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

    @SuppressLint("SimpleDateFormat")
    private void initData() {
        mUserDao = (UserDao) getIntent().getExtras().getSerializable("user");
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.userName.setText(mUserDao.getUserName());
        mBinding.phone.setText(mUserDao.getPhone());
        if (mUserDao.getBirthday() != null) {
            mBinding.birthday.setText(new SimpleDateFormat("yyyy-MM-dd").format(mUserDao.getBirthday()));
        }
        if (mUserDao.getHobby() != null) {
            mBinding.hobby.setText(mUserDao.getHobby());
        }
        Glide.with(mBinding.getRoot())
                .load(mUserDao.getAvatar())
                .centerCrop()
                .placeholder(R.drawable.dog)
                .into(mBinding.avatar);

        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }


    //就是返回页面高度
    private int getWindowHeight() {
        //传入事件页面this
        Resources res = MyInformationActivity.this.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public void setListeners() {
        mBinding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null) {
                    String filename = String.valueOf(System.currentTimeMillis()) + ".jpg";
                    upload(path, filename);
                } else {
                    updateUser();
                }
            }
        });

        mBinding.hobbyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View Bottom_qqedit_view = View.inflate(MyInformationActivity.this, R.layout.bottom_sheet_dialog, null);
                final BottomSheetDialog bottom_qqedit_dialog = new BottomSheetDialog(MyInformationActivity.this, R.style.BottomSheetDialog);
                bottom_qqedit_dialog.setContentView(Bottom_qqedit_view);

                //下面这里主要
                BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) Bottom_qqedit_view.getParent());
                mDialogBehavior.setPeekHeight(getWindowHeight());


                Button button = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_but);
                EditText editText = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_text);
                editText.setText(mBinding.hobby.getText().toString());
                editText.setHint("兴趣");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottom_qqedit_dialog.hide();
                        mBinding.hobby.setText(editText.getText().toString());
                        mUserDao.setHobby(editText.getText().toString());
                    }
                });
                //显示弹窗
                bottom_qqedit_dialog.show();
            }
        });



        mBinding.nameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View Bottom_qqedit_view = View.inflate(MyInformationActivity.this, R.layout.bottom_sheet_dialog, null);
                final BottomSheetDialog bottom_qqedit_dialog = new BottomSheetDialog(MyInformationActivity.this, R.style.BottomSheetDialog);
                bottom_qqedit_dialog.setContentView(Bottom_qqedit_view);

                //下面这里主要
                BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) Bottom_qqedit_view.getParent());
                mDialogBehavior.setPeekHeight(getWindowHeight());


                Button button = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_but);
                EditText editText = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_text);
                editText.setText(mBinding.userName.getText().toString());
                editText.setHint("用户名");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().length() > 10) {
                            Toast.makeText(MyInformationActivity.this, "用户名必须长度为10以内", Toast.LENGTH_SHORT).show();
                        } else if (editText.getText().toString().equals("")) {
                            Toast.makeText(MyInformationActivity.this, "用户名不可为空", Toast.LENGTH_SHORT).show();
                        } else {
                            bottom_qqedit_dialog.hide();
                            mBinding.userName.setText(editText.getText().toString());
                            mUserDao.setUserName(editText.getText().toString());
                        }
                    }
                });
                //显示弹窗
                bottom_qqedit_dialog.show();
            }
        });

        mBinding.phoneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View Bottom_qqedit_view = View.inflate(MyInformationActivity.this, R.layout.bottom_sheet_dialog, null);
                final BottomSheetDialog bottom_qqedit_dialog = new BottomSheetDialog(MyInformationActivity.this, R.style.BottomSheetDialog);
                bottom_qqedit_dialog.setContentView(Bottom_qqedit_view);

                //下面这里主要
                BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) Bottom_qqedit_view.getParent());
                mDialogBehavior.setPeekHeight(getWindowHeight());


                Button button = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_but);
                EditText editText = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_text);
                editText.setText(mBinding.phone.getText().toString());
                editText.setFocusable(View.FOCUSABLE);
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                editText.setHint("手机号");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String regex="1[356789]\\d{9}";
                        if (editText.getText().toString().matches(regex)) {
                            bottom_qqedit_dialog.hide();
                            mBinding.phone.setText(editText.getText().toString());
                            mUserDao.setPhone(editText.getText().toString());
                        } else {
                            Toast.makeText(MyInformationActivity.this, "请输入合法的手机号", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                //显示弹窗
                bottom_qqedit_dialog.show();
            }
        });

        mBinding.avatarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                pickPhoto();
            }
        });
        mBinding.birthdayContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker();
            }
        });

//        mBinding.btnAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uplpadAvatar(path, System.currentTimeMillis() + ".png");
//            }
//        });
    }
    public void pickPhoto() {
        PictureSelector.create(MyInformationActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(1)
                .isEnableCrop(true)//是否开启裁剪
                .withAspectRatio(1, 1)//裁剪比例
                .rotateEnabled(true)//裁剪是否可旋转图片
                .scaleEnabled(true)//裁剪是否可放大缩小图片
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true)//是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isMaxSelectEnabledMask(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    public void checkPermissions () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MyInformationActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PHOTO_PICK_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            path = selectList.get(0).getCutPath();
            Glide.with(this).load(selectList.get(0).getCutPath()).into(mBinding.avatar);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PHOTO_PICK_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhoto();
                }
                break;
        }
    }

    private void initTimePicker() {//Dialog 模式下，在底部弹出
        mTimePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onTimeSelect(Date date, View v) {
                mBinding.birthday.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
                mUserDao.setBirthday(date);
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i("pvTime", "onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
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

    private void updateUser() {
        HashMap<String, Object> map = new HashMap<>();
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String token = pref.getString("token", "");
        NetClient.getNetClient().callNet(NetworkSettings.UPDATE_USER, "token", token,
                RequestBody.create(JSON.toJSONString(mUserDao), Utils.MEDIA_TYPE),
                new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.UPDATE_USER_FAILED;
                        mHandler.post(()-> Utils.showMessage(MyInformationActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.UPDATE_USER_SUCCESS) {
                                    mUserDao = JSON.parseObject(restResponse.getData().toString(), UserDao.class);
                                } else if (mMessage.what == ResponseCode.PHONE_EXIST_ERROR){

                                }
                                mHandler.post(()-> Utils.showMessage(MyInformationActivity.this, mMessage));
                            }
                        }
                    }
                });
    }

//    public boolean uplpadAvatar(String path, String filename) {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        File file = new File(path);
//        System.out.println("尝试上传");
//        if (path.isEmpty() || !file.exists()) {
//            System.out.println("文件为空");
//            return false;
//        }
//
//        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("file", filename, RequestBody.create(new File(path), MediaType.parse("multipart/form-data")))
//                .addFormDataPart("filename", filename)
//                .build();
//        FutureTask<Boolean> task = new FutureTask<>(()-> {
//            try {
//                ResponseBody responseBody = okHttpClient.newCall(
//                        new Request.Builder().post(requestBody).url(NetworkSettings.UPDATE_AVATAR).build()
//                ).execute().body();
//                if (responseBody != null) {
//                    System.out.println("成功");
//                    boolean result = Boolean.parseBoolean(responseBody.string());
//                    System.out.println(result);
//                    return result;
//                }
//                System.out.println("失败");
//                return false;
//            } catch (IOException e) {
//                return false;
//            }
//        });
//        try {
//            new Thread(task).start();
//            return task.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    public Boolean uplpadAvatar(String path, String filename) {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        File file = new File(path);
//        System.out.println("尝试上传");
//        if (path.isEmpty() || !file.exists()) {
//            System.out.println("文件为空");
//        }
//
//        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("file", filename, RequestBody.create(new File(path), MediaType.parse("multipart/form-data")))
//                .addFormDataPart("key", "dd9636aedf8f196b1924830f6bd545a4")
//                .addFormDataPart("username", "SuperLee")
//                .addFormDataPart("userDir", "avatar")
//                .build();
//        FutureTask<String> task = new FutureTask<>(()-> {
//            try {
//                ResponseBody responseBody = okHttpClient.newCall(
//                        new Request.Builder().post(requestBody).url("http://www.xiaobais.net/public/file/uploadOneFile").build()
//                ).execute().body();
//                if (responseBody != null) {
//                    System.out.println("成功");
//                    String result = responseBody.string();
//                    System.out.println(result);
//                    return "失败";
//                }
//                System.out.println("失败");
//                return "失败";
//            } catch (IOException e) {
//                return "失败";
//            }
//        });
//        try {
//            new Thread(task).start();
//            return task.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        return "失败";
//    }

    public void upload(String path, String filename) {
        final String key = "dd9636aedf8f196b1924830f6bd545a4";
        final String username = "SuperLee";
        final String userDir = "avatar";
        final String requestUrl = "http://www.xiaobais.net/public/file/uploadOneFile";
        String avatarUrl = null;

        OkHttpClient okHttpClient = new OkHttpClient();
        File file = new File(path);
        if (path.isEmpty() || !file.exists()) {
            Toast.makeText(this, "文件为空", Toast.LENGTH_SHORT).show();
        }
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", filename, RequestBody.create(new File(path), MediaType.parse("multipart/form-data")))
                .addFormDataPart("key", key)
                .addFormDataPart("username", username)
                .addFormDataPart("userDir", userDir)
                .build();

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(MyInformationActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    mMessage.what = ResponseCode.UPDATE_AVATAR_SUCCESS;
                    String avatarUrl =  "http://www.xiaobais.net:8080/image/" + key + "/" + userDir + "/" + response.body().string();
                    mUserDao.setAvatar(avatarUrl);
                    System.out.println(avatarUrl);
                    handler.handleMessage(mMessage);
                }
            }
        });
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case ResponseCode.UPDATE_AVATAR_SUCCESS:
                    updateUser();
                    break;
                case ResponseCode.UPDATE_AVATAR_FAILED:
                    mHandler.post(() -> {
                        mMessage.what = ResponseCode.UPDATE_AVATAR_FAILED;
                        Utils.showMessage(MyInformationActivity.this, mMessage);
                    });
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

}