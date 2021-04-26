package com.example.beautysalon.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Lee
 * @date 2021.4.12  17:48
 * @description
 */
public class CountDown extends CountDownTimer {
    private Date mCreateDate;
    private TextView mTextView;
    private long mInterval;

    public CountDown(long millisInFuture, long countDownInterval, Date createDate, TextView textView) {
        super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        this.mTextView = textView;
        this.mInterval = System.currentTimeMillis() - createDate.getTime();
    }
    @Override
    public void onFinish() {// 计时完毕时触发

    }
    @Override
    public void onTick(long millisUntilFinished) {// 计时过程显示
        Date date = new Date(millisUntilFinished - mInterval);


        String time = new SimpleDateFormat("mm:ss").format(date);
        mTextView.setText("(" + time + "后关闭交易)");
    }
}
