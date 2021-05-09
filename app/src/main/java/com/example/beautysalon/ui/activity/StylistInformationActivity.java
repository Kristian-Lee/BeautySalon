package com.example.beautysalon.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.example.beautysalon.GlideEngine;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.BarbershopDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.databinding.ActivityStylistInformationBinding;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.ToastUtils;
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

public class StylistInformationActivity extends AppCompatActivity {

    private static final int PHOTO_PICK_REQUEST_CODE = 1002;
    private ActivityStylistInformationBinding mBinding;
    private TimePickerView mTimePicker;
    private StylistDao mStylistDao;
    private BarbershopDao mBarbershopDao;
    private String path = null;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final MyHandler handler = new MyHandler();
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityStylistInformationBinding.inflate(getLayoutInflater());
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

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void initData() {
        mStylistDao = (StylistDao) getIntent().getExtras().getSerializable("stylist");
        mBarbershopDao = (BarbershopDao) getIntent().getExtras().getSerializable("barbershop");
        System.out.println(mBarbershopDao);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.userName.setText(mStylistDao.getStylistName());
        mBinding.realName.setText(mStylistDao.getRealName());
        mBinding.phone.setText(String.valueOf(mStylistDao.getPhone()));
        mBinding.workingYears.setText(String.valueOf(mStylistDao.getWorkingYears()));
        mBinding.barbershopName.setText(mBarbershopDao.getBarbershopName());
        mBinding.speciality.setText(mStylistDao.getSpeciality());
        Glide.with(mBinding.getRoot())
                .load(mStylistDao.getAvatar())
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
        Resources res = StylistInformationActivity.this.getResources();
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
                    updateStylist();
                }
            }
        });

        mBinding.nameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View Bottom_qqedit_view = View.inflate(StylistInformationActivity.this, R.layout.bottom_sheet_dialog, null);
                final BottomSheetDialog bottom_qqedit_dialog = new BottomSheetDialog(StylistInformationActivity.this, R.style.BottomSheetDialog);
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
                            Toast.makeText(StylistInformationActivity.this, "用户名必须长度为10以内", Toast.LENGTH_SHORT).show();
                        } else if (editText.getText().toString().equals("")) {
                            Toast.makeText(StylistInformationActivity.this, "用户名不可为空", Toast.LENGTH_SHORT).show();
                        } else {
                            bottom_qqedit_dialog.dismiss();
                            mBinding.userName.setText(editText.getText().toString());
                            mStylistDao.setStylistName(editText.getText().toString());
                        }
                    }
                });
                //显示弹窗
                bottom_qqedit_dialog.show();
            }
        });



        mBinding.realNameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View Bottom_qqedit_view = View.inflate(StylistInformationActivity.this, R.layout.bottom_sheet_dialog, null);
                final BottomSheetDialog bottom_qqedit_dialog = new BottomSheetDialog(StylistInformationActivity.this, R.style.BottomSheetDialog);
                bottom_qqedit_dialog.setContentView(Bottom_qqedit_view);

                //下面这里主要
                BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) Bottom_qqedit_view.getParent());
                mDialogBehavior.setPeekHeight(getWindowHeight());


                Button button = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_but);
                EditText editText = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_text);
                editText.setText(mBinding.realName.getText().toString());
                editText.setHint("真实姓名");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().length() > 10) {
                            Toast.makeText(StylistInformationActivity.this, "姓名必须长度为30以内", Toast.LENGTH_SHORT).show();
                        } else if (editText.getText().toString().equals("")) {
                            Toast.makeText(StylistInformationActivity.this, "姓名不可为空", Toast.LENGTH_SHORT).show();
                        } else {
                            bottom_qqedit_dialog.dismiss();
                            mBinding.realName.setText(editText.getText().toString());
                            mStylistDao.setRealName(editText.getText().toString());
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
                View Bottom_qqedit_view = View.inflate(StylistInformationActivity.this, R.layout.bottom_sheet_dialog, null);
                final BottomSheetDialog bottom_qqedit_dialog = new BottomSheetDialog(StylistInformationActivity.this, R.style.BottomSheetDialog);
                bottom_qqedit_dialog.setContentView(Bottom_qqedit_view);

                //下面这里主要
                BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) Bottom_qqedit_view.getParent());
                mDialogBehavior.setPeekHeight(getWindowHeight());


                Button button = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_but);
                EditText editText = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_text);
                editText.setText(mBinding.phone.getText().toString());
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                editText.setHint("手机号");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String regex="1[356789]\\d{9}";
                        if (editText.getText().toString().matches(regex)) {
                            bottom_qqedit_dialog.dismiss();
                            mBinding.phone.setText(editText.getText().toString());
                            mStylistDao.setPhone(editText.getText().toString());
                        } else {
                            Toast.makeText(StylistInformationActivity.this, "请输入合法的手机号", Toast.LENGTH_SHORT).show();
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
        mBinding.workingYearsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View Bottom_qqedit_view = View.inflate(StylistInformationActivity.this, R.layout.bottom_sheet_dialog, null);
                final BottomSheetDialog bottom_qqedit_dialog = new BottomSheetDialog(StylistInformationActivity.this, R.style.BottomSheetDialog);
                bottom_qqedit_dialog.setContentView(Bottom_qqedit_view);

                //下面这里主要
                BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) Bottom_qqedit_view.getParent());
                mDialogBehavior.setPeekHeight(getWindowHeight());


                Button button = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_but);
                EditText editText = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_text);
                editText.setText(mBinding.workingYears.getText().toString());
                editText.setHint("从业经验");
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().length() > 3) {
                            Toast.makeText(StylistInformationActivity.this, "数据不合法", Toast.LENGTH_SHORT).show();
                        } else if (editText.getText().toString().equals("")) {
                            Toast.makeText(StylistInformationActivity.this, "不可为空", Toast.LENGTH_SHORT).show();
                        } else {
                            bottom_qqedit_dialog.dismiss();
                            mBinding.workingYears.setText(editText.getText().toString());
                            mStylistDao.setWorkingYears(Integer.parseInt(editText.getText().toString()));
                        }
                    }
                });
                //显示弹窗
                bottom_qqedit_dialog.show();
            }
        });

        mBinding.specialityContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View Bottom_qqedit_view = View.inflate(StylistInformationActivity.this, R.layout.bottom_sheet_dialog, null);
                final BottomSheetDialog bottom_qqedit_dialog = new BottomSheetDialog(StylistInformationActivity.this, R.style.BottomSheetDialog);
                bottom_qqedit_dialog.setContentView(Bottom_qqedit_view);

                //下面这里主要
                BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) Bottom_qqedit_view.getParent());
                mDialogBehavior.setPeekHeight(getWindowHeight());


                Button button = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_but);
                EditText editText = bottom_qqedit_dialog.findViewById(R.id.bottom_edit_text);
                editText.setText(mBinding.speciality.getText().toString());
                editText.setHint("特长");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().length() > 50) {
                            Toast.makeText(StylistInformationActivity.this, "限50字以内", Toast.LENGTH_SHORT).show();
                        } else if (editText.getText().toString().equals("")) {
                            Toast.makeText(StylistInformationActivity.this, "不可为空", Toast.LENGTH_SHORT).show();
                        } else {
                            bottom_qqedit_dialog.dismiss();
                            mBinding.speciality.setText(editText.getText().toString());
                            mStylistDao.setSpeciality(editText.getText().toString());
                        }
                    }
                });
                //显示弹窗
                bottom_qqedit_dialog.show();
            }
        });

        mBinding.barbershopNameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.getInstance(getApplicationContext()).showShortToast("不可修改");
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
        PictureSelector.create(StylistInformationActivity.this)
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
            ActivityCompat.requestPermissions(StylistInformationActivity.this,
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

    private void updateStylist() {
        HashMap<String, Object> map = new HashMap<>();
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String token = pref.getString("token", "");
        NetClient.getNetClient().callNet(NetworkSettings.UPDATE_STYLIST, "token", token,
                RequestBody.create(JSON.toJSONString(mStylistDao), Utils.MEDIA_TYPE),
                new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.UPDATE_STYLIST_FAILED;
                        mHandler.post(()-> Utils.showMessage(StylistInformationActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.UPDATE_STYLIST_SUCCESS) {
                                    mStylistDao = JSON.parseObject(restResponse.getData().toString(), StylistDao.class);
                                } else if (mMessage.what == ResponseCode.PHONE_EXIST_ERROR){

                                }
                                mHandler.post(()-> Utils.showMessage(StylistInformationActivity.this, mMessage));
                            }
                        }
                    }
                });
    }

    public void upload(String path, String filename) {
        final String key = "dd9636aedf8f196b1924830f6bd545a4";
        final String username = "SuperLee";
        final String userDir = "avatar";
        final String requestUrl = "http://www.xiaobais.net/public/file/uploadOneFile";
        boolean isSuccessed = false;
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
                Toast.makeText(StylistInformationActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    mMessage.what = ResponseCode.UPDATE_AVATAR_SUCCESS;
                    String avatarUrl =  "http://www.xiaobais.net:8080/image/" + key + "/" + userDir + "/" + response.body().string();
                    mStylistDao.setAvatar(avatarUrl);
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
                    updateStylist();
                    break;
                case ResponseCode.UPDATE_AVATAR_FAILED:
                    mHandler.post(() -> {
                        mMessage.what = ResponseCode.UPDATE_AVATAR_FAILED;
                        Utils.showMessage(StylistInformationActivity.this, mMessage);
                    });
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

}