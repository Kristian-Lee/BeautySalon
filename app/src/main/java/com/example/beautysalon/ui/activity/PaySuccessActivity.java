package com.example.beautysalon.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beautysalon.databinding.ActivityPaySuccessBinding;
import com.gyf.immersionbar.ImmersionBar;

public class PaySuccessActivity extends AppCompatActivity {

    private ActivityPaySuccessBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPaySuccessBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
    }

    private void initData() {
        setSupportActionBar(mBinding.toolbar);
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();

    }

    private void setListeners() {
        mBinding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaySuccessActivity.this, MainActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
            }
        });
    }
}